import { humanize } from '../utils/format'

/** Every status gets a tone AND an icon, so it never relies on colour alone. */
const STYLES = {
  // orders
  PLACED: ['info', 'bag-check'],
  CONFIRMED: ['info', 'check2-circle'],
  PROCESSING: ['gold', 'gear'],
  PACKED: ['gold', 'box-seam'],
  SHIPPED: ['gold', 'truck'],
  OUT_FOR_DELIVERY: ['gold', 'geo-alt'],
  DELIVERED: ['good', 'check-circle-fill'],
  CANCELLED: ['bad', 'x-circle'],
  // deliveries
  ASSIGNED: ['info', 'person-check'],
  ACCEPTED: ['info', 'hand-thumbs-up'],
  PICKED_UP: ['gold', 'box-arrow-up'],
  FAILED: ['bad', 'exclamation-octagon'],
  // payments
  PENDING: ['warn', 'hourglass-split'],
  SUCCESS: ['good', 'check-circle-fill'],
  REFUNDED: ['neutral', 'arrow-counterclockwise'],
  // customer requests
  OPEN: ['warn', 'envelope-open'],
  IN_PROGRESS: ['gold', 'arrow-repeat'],
  RESOLVED: ['good', 'check-circle-fill'],
  CLOSED: ['neutral', 'archive'],
  // generic
  ACTIVE: ['good', 'circle-fill'],
  INACTIVE: ['neutral', 'circle'],
  LOW: ['warn', 'exclamation-triangle'],
  OUT: ['bad', 'x-octagon'],
  IN_STOCK: ['good', 'check2'],
}

export default function StatusBadge({ status, label }) {
  const [tone, icon] = STYLES[status] || ['neutral', 'dot']
  return (
    <span className={`status ${tone}`}>
      <i className={`bi bi-${icon}`} aria-hidden="true" />
      {label || humanize(status)}
    </span>
  )
}
