import { useEffect, useRef } from 'react'
import { formatDateTime } from '../utils/format'

const ICONS = {
  ORDER_PLACED: 'bag-check',
  PAYMENT_RECEIVED: 'credit-card',
  CONFIRMED: 'check2',
  PROCESSING: 'gem',
  PACKED: 'box-seam',
  DELIVERY_ASSIGNED: 'person-check',
  DELIVERY_ACCEPTED: 'bicycle',
  SHIPPED: 'truck',
  OUT_FOR_DELIVERY: 'geo-alt',
  DELIVERED: 'house-heart',
  DELIVERY_FAILED: 'exclamation-octagon',
  CANCELLED: 'x-circle',
  PAYMENT_REFUNDED: 'arrow-counterclockwise',
  STORE_UPDATE: 'megaphone',
}
const BAD = new Set(['DELIVERY_FAILED', 'CANCELLED'])
const ROLE_NAMES = { CUSTOMER: 'You', STAFF: 'Store team', STORE_MANAGER: 'Store manager', ADMIN: 'JewelCraft', DELIVERY_PARTNER: 'Delivery partner' }

/**
 * Courier-style tracking history, newest first. Events that arrive while the page is
 * open slide in, so the customer sees each update happen.
 * `viewer` = 'customer' hides staff names (shows "Store team"); the store sees who did what.
 */
export default function TrackingHistory({ events = [], viewer = 'customer' }) {
  // Ids already shown; filled after each render so StrictMode double-renders stay consistent.
  const seen = useRef(null)
  useEffect(() => {
    seen.current = new Set(events.map((e) => e.id))
  }, [events])

  if (events.length === 0) return <p className="small text-muted-jc mb-0">Tracking updates will appear here.</p>

  const list = [...events].reverse()
  const rows = list.map((e, i) => {
    const fresh = seen.current !== null && !seen.current.has(e.id)
    const by =
      viewer === 'customer'
        ? e.actorRole === 'DELIVERY_PARTNER' ? e.actorName : e.actorRole === 'CUSTOMER' ? 'You' : null
        : `${e.actorName}${ROLE_NAMES[e.actorRole] ? ` · ${ROLE_NAMES[e.actorRole]}` : ''}`
    return (
      <li key={e.id} className={`${i === 0 ? 'latest' : ''} ${BAD.has(e.event) ? 'bad' : ''} ${fresh ? 'fresh' : ''}`}>
        <span className="ic"><i className={`bi bi-${ICONS[e.event] || 'dot'}`} /></span>
        <div className="d-flex justify-content-between flex-wrap gap-2">
          <span className="t-title">{e.title}</span>
          <span className="t-time">{formatDateTime(e.createdAt)}</span>
        </div>
        {e.message && <div className="t-msg">{e.message}</div>}
        {by && <div className="t-by">by {by}</div>}
      </li>
    )
  })
  return <ul className="track-list">{rows}</ul>
}
