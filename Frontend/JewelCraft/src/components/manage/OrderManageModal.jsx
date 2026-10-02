import { useEffect, useState } from 'react'
import toast from 'react-hot-toast'
import useAuth from '../../hooks/useAuth'
import usePolling from '../../hooks/usePolling'
import { errorMessage } from '../../services/api'
import { orderService } from '../../services/orderService'
import { userService } from '../../services/userService'
import { ASSIGNABLE_STATUSES, ORDER_TRANSITIONS, STAFF_ORDER_STATUSES, STORE_ORDER_STATUSES } from '../../utils/constants'
import { formatDateTime, formatINR, humanize } from '../../utils/format'
import JewelImage from '../JewelImage'
import Modal from '../Modal'
import OrderTimeline from '../OrderTimeline'
import StatusBadge from '../StatusBadge'
import TrackingHistory from '../TrackingHistory'
import OrderChecklist from './OrderChecklist'

/** What each store-side status means, and the standard message the customer sees for it. */
const STATUS_HELP = {
  CONFIRMED: { what: 'Accept the order and reserve the pieces for this customer.', customer: 'Your order is confirmed and the pieces are reserved for you.' },
  PROCESSING: { what: 'The pieces are being prepared and quality-checked.', customer: 'Our artisans are preparing and quality-checking your jewellery.' },
  PACKED: { what: 'Sealed in insured packaging, ready for the delivery partner to collect.', customer: 'Packed in tamper-proof, fully insured packaging.' },
  CANCELLED: { what: 'Stops the order and puts the stock back. An online payment is refunded automatically.' },
}
const UPDATE_SUGGESTIONS = [
  'Your hallmark certificate has been added to the package.',
  'Your piece passed our final quality check.',
  'Delivery may take one extra day – thank you for your patience.',
]

/** Order detail + actions for the store team. */
export default function OrderManageModal({ orderId, onClose, onUpdated }) {
  const { user } = useAuth()
  const isStaff = user.role === 'STAFF'
  const isManager = user.role === 'STORE_MANAGER' || user.role === 'ADMIN'
  const [order, setOrder] = useState(null)
  const [tracking, setTracking] = useState([])
  const [nextStatus, setNextStatus] = useState('')
  const [note, setNote] = useState('')
  const [partners, setPartners] = useState([])
  const [partnerId, setPartnerId] = useState('')
  const [update, setUpdate] = useState('')
  const [busy, setBusy] = useState(false)

  useEffect(() => {
    if (!orderId) return
    setOrder(null)
    setNextStatus('')
    setNote('')
    setUpdate('')
    setTracking([])
    orderService.get(orderId).then(setOrder).catch((err) => toast.error(errorMessage(err)))
    orderService.tracking(orderId).then(setTracking).catch(() => {})
  }, [orderId])

  // While the panel is open, pick up changes made by delivery partners or colleagues.
  usePolling(
    async () => {
      const [o, t] = await Promise.all([orderService.get(orderId), orderService.tracking(orderId)])
      setOrder(o)
      setTracking(t)
    },
    10000,
    Boolean(orderId),
  )

  useEffect(() => {
    if (isManager) userService.deliveryPartners().then(setPartners).catch(() => {})
  }, [isManager])

  const apply = (updated, message) => {
    setOrder(updated)
    onUpdated?.(updated)
    orderService.tracking(updated.id).then(setTracking).catch(() => {})
    toast.success(message)
  }

  const updateStatus = async () => {
    if (nextStatus === 'CANCELLED' && !note.trim()) {
      toast.error('Please add a reason for cancelling')
      return
    }
    setBusy(true)
    try {
      apply(await orderService.updateStatus(order.id, nextStatus, note || null), `Order marked ${humanize(nextStatus).toLowerCase()}`)
      setNextStatus('')
      setNote('')
    } catch (err) {
      toast.error(errorMessage(err))
    } finally {
      setBusy(false)
    }
  }

  const postUpdate = async () => {
    setBusy(true)
    try {
      apply(await orderService.postUpdate(order.id, update.trim()), 'Update posted – the customer can see it in tracking')
      setUpdate('')
    } catch (err) {
      toast.error(errorMessage(err))
    } finally {
      setBusy(false)
    }
  }

  const assign = async () => {
    setBusy(true)
    try {
      apply(await orderService.assignDelivery(order.id, Number(partnerId)), 'Delivery partner assigned')
      setPartnerId('')
    } catch (err) {
      toast.error(errorMessage(err))
    } finally {
      setBusy(false)
    }
  }

  const allowed = order
    ? (ORDER_TRANSITIONS[order.status] || []).filter(
        (s) => STORE_ORDER_STATUSES.includes(s) && (!isStaff || STAFF_ORDER_STATUSES.includes(s)),
      )
    : []
  const delivery = order?.delivery
  const deliveryFailed = delivery?.status === 'FAILED'
  // The server won't confirm an online order until it is paid, so say so up front.
  const awaitingPayment = order?.payment?.method === 'RAZORPAY' && order.payment.status !== 'SUCCESS'
  const canPostUpdate = isManager && order && !['DELIVERED', 'CANCELLED'].includes(order.status)
  // Re-picking the current partner is only allowed as a retry after a failed attempt.
  const partnerChoices = partners.filter((p) => p.enabled && (deliveryFailed || p.id !== delivery?.partnerId))
  const canAssign =
    isManager &&
    order &&
    (ASSIGNABLE_STATUSES.includes(order.status) || (deliveryFailed && ['SHIPPED', 'OUT_FOR_DELIVERY'].includes(order.status))) &&
    !['PICKED_UP', 'OUT_FOR_DELIVERY', 'DELIVERED'].includes(delivery?.status)

  return (
    <Modal show={Boolean(orderId)} title={order ? `Order ${order.orderNumber}` : 'Order'} size="xl" onClose={onClose}>
      {!order ? (
        <div className="text-center text-muted-jc py-5">Loading…</div>
      ) : (
        <div className="row g-4">
          <div className="col-lg-7">
            <div className="d-flex justify-content-between flex-wrap gap-2">
              <div className="small text-muted-jc">Placed {formatDateTime(order.createdAt)}</div>
              <StatusBadge status={order.status} />
            </div>
            <OrderTimeline status={order.status} />
            {order.cancelReason && <p className="small text-soft">Cancel reason: {order.cancelReason}</p>}

            <h6 className="small-caps mt-4 mb-2">Items</h6>
            {order.items.map((i) => (
              <div key={i.id} className="d-flex gap-3 align-items-center py-2 border-bottom">
                <div className="thumb-sm"><JewelImage src={i.productImage} alt="" name={i.productName} /></div>
                <div className="flex-grow-1 small">
                  <div className="fw-500">{i.productName}</div>
                  <div className="text-muted-jc">{i.metalDetails} · Qty {i.quantity}</div>
                </div>
                <div className="small fw-500">{formatINR(i.lineTotal)}</div>
              </div>
            ))}
            <div className="small mt-3 ms-auto" style={{ maxWidth: 300 }}>
              <div className="d-flex justify-content-between"><span>Subtotal</span><span>{formatINR(order.subtotal)}</span></div>
              {Number(order.discount) > 0 && <div className="d-flex justify-content-between"><span>Discount {order.couponCode}</span><span>− {formatINR(order.discount)}</span></div>}
              <div className="d-flex justify-content-between"><span>GST</span><span>{formatINR(order.tax)}</span></div>
              <div className="d-flex justify-content-between"><span>Delivery</span><span>{formatINR(order.deliveryCharge)}</span></div>
              <div className="d-flex justify-content-between fw-500 border-top mt-1 pt-1"><span>Total</span><span>{formatINR(order.totalAmount)}</span></div>
            </div>

            <h6 className="small-caps mt-4 mb-3">Tracking history</h6>
            <TrackingHistory events={tracking} viewer="store" />
          </div>

          <div className="col-lg-5 d-flex flex-column gap-3">
            <div className="panel">
              <h6 className="small-caps mb-2">Customer</h6>
              <div className="fw-500">{order.customerName}</div>
              <div className="small text-soft">{order.customerEmail} · {order.customerPhone}</div>
              <hr />
              <h6 className="small-caps mb-2">Ship to</h6>
              <div className="small text-soft">
                {order.shippingAddress.fullName}<br />
                {order.shippingAddress.line1}{order.shippingAddress.line2 ? `, ${order.shippingAddress.line2}` : ''}<br />
                {order.shippingAddress.city}, {order.shippingAddress.state} – {order.shippingAddress.pincode}<br />
                {order.shippingAddress.phone}
              </div>
            </div>

            <div className="panel">
              <h6 className="small-caps mb-2">Payment &amp; delivery</h6>
              <div className="d-flex justify-content-between align-items-center small mb-2">
                <span>{order.payment?.method === 'COD' ? 'Cash on delivery' : 'Razorpay'}</span>
                {order.payment && <StatusBadge status={order.payment.status} />}
              </div>
              <div className="d-flex justify-content-between align-items-center small">
                <span>{delivery ? delivery.partnerName : 'No delivery partner yet'}</span>
                {delivery && <StatusBadge status={delivery.status} />}
              </div>
              {delivery?.notes && <div className="small text-muted-jc mt-1">{delivery.notes}</div>}            </div>

            <OrderChecklist order={order} />

            {allowed.length > 0 && (
              <div className="panel">
                <h6 className="small-caps mb-2">Update status</h6>
                <select className="form-select form-select-sm mb-2" value={nextStatus} onChange={(e) => setNextStatus(e.target.value)} aria-label="New status">
                  <option value="">Choose next status…</option>
                  {allowed.map((s) => (
                    <option key={s} value={s} disabled={s === 'CONFIRMED' && awaitingPayment}>
                      {humanize(s)}{s === 'CONFIRMED' && awaitingPayment ? ' – waiting for online payment' : ''}
                    </option>
                  ))}
                </select>
                {STATUS_HELP[nextStatus] && <div className="small text-soft mb-2">{STATUS_HELP[nextStatus].what}</div>}
                {nextStatus && (
                  <>
                    <label className="form-label small mb-1" htmlFor="status-note">
                      {nextStatus === 'CANCELLED' ? 'Reason for cancelling (shown to the customer)' : 'Message to the customer (optional)'}
                    </label>
                    <textarea
                      id="status-note"
                      className="form-control form-control-sm mb-1"
                      rows={2}
                      maxLength={500}
                      placeholder={STATUS_HELP[nextStatus]?.customer || 'e.g. Customer asked to cancel by phone'}
                      value={note}
                      onChange={(e) => setNote(e.target.value)}
                    />
                    {nextStatus !== 'CANCELLED' && <div className="small text-muted-jc mb-2">Leave empty to send the standard message shown above.</div>}
                  </>
                )}
                <button className="btn btn-jc btn-sm w-100 mt-1" disabled={!nextStatus || busy} onClick={updateStatus}>Update</button>
                {isStaff && <div className="small text-muted-jc mt-2">Staff can move orders up to Packed.</div>}
              </div>
            )}

            {canPostUpdate && (
              <div className="panel">
                <h6 className="small-caps mb-1">Post an update</h6>
                <div className="small text-muted-jc mb-2">Tell the customer something without changing the order status.</div>
                <div className="mb-1">
                  {UPDATE_SUGGESTIONS.map((s) => (
                    <button key={s} type="button" className="chip chip-btn" onClick={() => setUpdate(s)}>{s}</button>
                  ))}
                </div>
                <textarea className="form-control form-control-sm mb-2" rows={2} maxLength={300} placeholder="Write an update for the customer" aria-label="Update for the customer" value={update} onChange={(e) => setUpdate(e.target.value)} />
                <button className="btn btn-outline-jc btn-sm w-100" disabled={update.trim().length < 5 || busy} onClick={postUpdate}>
                  <i className="bi bi-megaphone me-1" />Post update
                </button>
              </div>
            )}

            {canAssign && (
              <div className="panel">
                <h6 className="small-caps mb-2">{delivery ? 'Re-assign delivery' : 'Assign delivery partner'}</h6>
                <select className="form-select form-select-sm mb-2" value={partnerId} onChange={(e) => setPartnerId(e.target.value)} aria-label="Delivery partner">
                  <option value="">Choose partner…</option>
                  {partnerChoices.map((p) => <option key={p.id} value={p.id}>{p.fullName} · {p.phone}</option>)}
                </select>
                <button className="btn btn-gold btn-sm w-100" disabled={!partnerId || busy} onClick={assign}>Assign</button>
              </div>
            )}
          </div>
        </div>
      )}
    </Modal>
  )
}
