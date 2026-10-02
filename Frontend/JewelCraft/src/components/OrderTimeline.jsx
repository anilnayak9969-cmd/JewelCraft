import { ORDER_FLOW } from '../utils/constants'
import { humanize } from '../utils/format'

const ICONS = ['bag-check', 'check2', 'gear', 'box-seam', 'truck', 'geo-alt', 'house-heart']

/** Progress tracker for an order: PLACED → … → DELIVERED. */
export default function OrderTimeline({ status }) {
  if (status === 'CANCELLED') {
    return (
      <div className="d-flex align-items-center gap-2 py-2" style={{ color: 'var(--jc-bad)' }}>
        <i className="bi bi-x-circle" /> This order was cancelled.
      </div>
    )
  }
  const current = ORDER_FLOW.indexOf(status)
  return (
    <div className="timeline" aria-label={`Order status: ${humanize(status)}`}>
      {ORDER_FLOW.map((s, i) => (
        <div key={s} className={`tl-step ${i <= current ? 'done' : ''} ${i === current ? 'current' : ''}`}>
          <div className="dot"><i className={`bi bi-${i < current ? 'check2' : ICONS[i]}`} /></div>
          <div>{humanize(s)}</div>
        </div>
      ))}
    </div>
  )
}
