import { formatDateTime, formatINR } from '../../utils/format'

const RANK = ['PLACED', 'CONFIRMED', 'PROCESSING', 'PACKED', 'SHIPPED', 'OUT_FOR_DELIVERY', 'DELIVERED']
const ICON = { done: 'check-circle-fill', wait: 'hourglass-split', todo: 'circle', bad: 'x-octagon-fill', info: 'cash-coin' }

function paymentStep(order) {
  const p = order.payment
  if (!p) return null
  const amount = formatINR(order.totalAmount)
  if (p.method === 'COD') {
    return order.status === 'DELIVERED'
      ? { state: 'done', label: 'Cash collected', detail: `${amount} paid at the door` }
      : { state: 'info', label: 'Cash on delivery', detail: `Partner collects ${amount} at the door` }
  }
  if (p.status === 'SUCCESS') return { state: 'done', label: 'Paid online', detail: `${amount} via Razorpay` }
  if (p.status === 'REFUNDED') return { state: 'done', label: 'Payment refunded', detail: amount }
  if (p.status === 'FAILED') return { state: 'bad', label: 'Online payment failed', detail: 'The customer has not paid' }
  return { state: 'wait', label: 'Waiting for online payment', detail: 'The order can be confirmed once the customer pays' }
}

function steps(order) {
  const rank = RANK.indexOf(order.status)
  const d = order.delivery
  const at = (date) => (date ? formatDateTime(date) : null)
  const stage = (reached, label, detail) => ({ state: reached ? 'done' : 'todo', label, detail: reached ? detail : null })
  const partner = !d
    ? { state: 'todo', label: 'Delivery partner assigned' }
    : d.status === 'FAILED'
      ? { state: 'bad', label: 'Delivery attempt failed', detail: d.notes || `${d.partnerName} could not deliver – assign again` }
      : { state: 'done', label: 'Delivery partner assigned', detail: `${d.partnerName} · ${at(d.assignedAt)}` }
  const active = d && d.status !== 'FAILED'
  return [
    paymentStep(order),
    stage(rank >= 1, 'Confirmed by the store'),
    stage(rank >= 2, 'Prepared & quality-checked'),
    stage(rank >= 3, 'Packed in insured packaging'),
    partner,
    stage(active && d.acceptedAt, 'Partner accepted the delivery', active && at(d.acceptedAt)),
    stage(rank >= 4, 'Picked up from the store', active && at(d.pickedUpAt)),
    stage(rank >= 5, 'Out for delivery'),
    stage(rank >= 6, 'Delivered to the customer', active && at(d.deliveredAt)),
  ].filter(Boolean)
}

/** Who has to act next, in plain words. */
function nextStep(order) {
  const d = order.delivery
  const unpaidOnline = order.payment?.method === 'RAZORPAY' && order.payment.status !== 'SUCCESS'
  const noPartner = !d || d.status === 'FAILED'
  switch (order.status) {
    case 'DELIVERED':
      return { who: 'Completed', text: 'The customer has received this order.' }
    case 'PLACED':
      return unpaidOnline
        ? { who: 'Customer', text: 'Complete the online payment. The store can confirm the order after that.' }
        : { who: 'Store team', text: 'Confirm the order to reserve the pieces.' }
    case 'CONFIRMED':
      return { who: 'Store team', text: `Start preparing the pieces – move it to Processing.${noPartner ? ' A delivery partner can already be assigned.' : ''}` }
    case 'PROCESSING':
      return { who: 'Store team', text: `Quality-check and pack – move it to Packed.${noPartner ? ' A delivery partner can already be assigned.' : ''}` }
    default:
      if (noPartner) {
        return { who: 'Store manager', text: d ? 'The last attempt failed – assign a delivery partner again.' : 'Assign a delivery partner to collect the package.' }
      }
      if (order.status === 'PACKED') {
        return d.status === 'ASSIGNED'
          ? { who: d.partnerName, text: 'Accept the delivery in the delivery app.' }
          : { who: d.partnerName, text: 'Collect the package from the store.' }
      }
      return order.status === 'SHIPPED'
        ? { who: d.partnerName, text: 'Head out for delivery.' }
        : { who: d.partnerName, text: 'Hand the order over to the customer.' }
  }
}

/** Store-side progress checklist: what is done, what is pending, and who acts next. */
export default function OrderChecklist({ order }) {
  if (order.status === 'CANCELLED') {
    return (
      <div className="panel">
        <h6 className="small-caps mb-2">Order progress</h6>
        <div className="notice bad"><i className="bi bi-x-octagon" /><div>This order was cancelled{order.cancelReason ? `: ${order.cancelReason}` : '.'}</div></div>
      </div>
    )
  }
  const next = nextStep(order)
  return (
    <div className="panel">
      <h6 className="small-caps mb-2">Order progress</h6>
      <div className={`notice mb-3 ${next.who === 'Completed' ? '' : 'warn'}`}>
        <i className={`bi bi-${next.who === 'Completed' ? 'patch-check' : 'arrow-right-circle'}`} />
        <div><span className="fw-500">{next.who === 'Completed' ? 'Completed' : `Next: ${next.who}`}</span> – {next.text}</div>
      </div>
      <ul className="checklist">
        {steps(order).map((s) => (
          <li key={s.label} className={s.state}>
            <i className={`bi bi-${ICON[s.state]}`} />
            <div>
              <div>{s.label}</div>
              {s.detail && <div className="small text-muted-jc">{s.detail}</div>}
            </div>
          </li>
        ))}
      </ul>
    </div>
  )
}
