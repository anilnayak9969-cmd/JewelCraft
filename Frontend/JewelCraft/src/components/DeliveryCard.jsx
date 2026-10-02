import { useState } from 'react'
import toast from 'react-hot-toast'
import { errorMessage } from '../services/api'
import { deliveryService } from '../services/orderService'
import { DELIVERY_TRANSITIONS } from '../utils/constants'
import { formatDateTime, formatINR, humanize } from '../utils/format'
import Modal from './Modal'
import StatusBadge from './StatusBadge'

const ACTION_LABELS = {
  PICKED_UP: ['Mark picked up', 'box-arrow-up'],
  OUT_FOR_DELIVERY: ['Out for delivery', 'geo-alt'],
  DELIVERED: ['Mark delivered', 'check2-circle'],
  FAILED: ['Delivery failed', 'exclamation-octagon'],
}

/** One assigned order for a delivery partner, with the next allowed actions. */
export default function DeliveryCard({ order, onUpdated }) {
  const d = order.delivery
  const a = order.shippingAddress
  const [busy, setBusy] = useState(false)
  const [failing, setFailing] = useState(false)
  const [notes, setNotes] = useState('')

  const run = async (action, message) => {
    setBusy(true)
    try {
      onUpdated(await action())
      toast.success(message)
      return true
    } catch (err) {
      toast.error(errorMessage(err))
      return false
    } finally {
      setBusy(false)
    }
  }

  const move = (status, note) => run(() => deliveryService.updateStatus(d.id, status, note || null), `Marked ${humanize(status).toLowerCase()}`)
  const cod = order.payment?.method === 'COD' && order.payment.status !== 'SUCCESS'
  const mapsUrl = `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(`${a.line1} ${a.line2 || ''} ${a.city} ${a.state} ${a.pincode}`)}`

  return (
    <div className="card-jc h-100 d-flex flex-column">
      <div className="card-jc-head">
        <div>
          <h5>{order.orderNumber}</h5>
          <div className="small text-muted-jc">Assigned {formatDateTime(d.assignedAt)}</div>
        </div>
        <StatusBadge status={d.status} />
      </div>
      <div className="card-jc-body flex-grow-1">
        <div className="fw-500">{a.fullName}</div>
        <div className="small text-soft">
          {a.line1}{a.line2 ? `, ${a.line2}` : ''}<br />{a.city}, {a.state} – {a.pincode}
        </div>
        <div className="d-flex flex-wrap gap-3 mt-2 small">
          <a href={`tel:${a.phone}`} className="text-gold"><i className="bi bi-telephone me-1" />{a.phone}</a>
          <a href={mapsUrl} target="_blank" rel="noreferrer" className="text-gold"><i className="bi bi-map me-1" />Open in Maps</a>
        </div>
        <hr />
        <div className="small">
          {order.items.map((i) => <div key={i.id}>{i.quantity} × {i.productName}</div>)}
        </div>
        <div className="d-flex justify-content-between align-items-center mt-3">
          <span className="small text-muted-jc">Order status: {humanize(order.status)}</span>
          {cod ? (
            <span className="status warn"><i className="bi bi-cash-coin" />Collect {formatINR(order.totalAmount)}</span>
          ) : (
            <span className="status good"><i className="bi bi-check2" />Prepaid</span>
          )}
        </div>
        {d.notes && <div className="small text-soft mt-2">Note: {d.notes}</div>}
      </div>
      <div className="p-3 border-top d-flex flex-wrap gap-2">
        {d.status === 'ASSIGNED' && (
          <button className="btn btn-gold btn-sm flex-grow-1" disabled={busy} onClick={() => run(() => deliveryService.accept(d.id), 'Delivery accepted')}>
            <i className="bi bi-hand-thumbs-up me-1" />Accept delivery
          </button>
        )}
        {(DELIVERY_TRANSITIONS[d.status] || []).map((s) => (
          <button
            key={s}
            className={`btn btn-sm flex-grow-1 ${s === 'FAILED' ? 'btn-outline-jc' : 'btn-jc'}`}
            disabled={busy}
            onClick={() => (s === 'FAILED' ? setFailing(true) : move(s))}
          >
            <i className={`bi bi-${ACTION_LABELS[s][1]} me-1`} />{ACTION_LABELS[s][0]}
          </button>
        ))}
        {d.status === 'ACCEPTED' && order.status !== 'PACKED' && order.status !== 'SHIPPED' && (
          <div className="small text-muted-jc w-100">Waiting for the store to pack this order.</div>
        )}
      </div>

      <Modal
        show={failing}
        title="Delivery failed"
        onClose={() => setFailing(false)}
        footer={
          <>
            <button className="btn btn-outline-jc btn-sm" onClick={() => setFailing(false)}>Cancel</button>
            <button
              className="btn btn-jc btn-sm"
              disabled={busy || !notes.trim()}
              onClick={async () => {
                if (await move('FAILED', notes.trim())) setFailing(false)
              }}
            >
              Confirm
            </button>
          </>
        }
      >
        <label className="form-label" htmlFor={`fail-${d.id}`}>What happened?</label>
        <textarea id={`fail-${d.id}`} className="form-control" rows={3} maxLength={500} placeholder="e.g. Customer not reachable, address locked" value={notes} onChange={(e) => setNotes(e.target.value)} />
        <p className="small text-muted-jc mt-2 mb-0">The store will be notified and can re-assign the order.</p>
      </Modal>
    </div>
  )
}
