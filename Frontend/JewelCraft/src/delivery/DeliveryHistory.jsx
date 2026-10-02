import { DashHeader } from '../components/dashboard/StatCard'
import StatusBadge from '../components/StatusBadge'
import useApi from '../hooks/useApi'
import { deliveryService } from '../services/orderService'
import { formatDateTime, formatINR } from '../utils/format'

export default function DeliveryHistory() {
  const { data, loading, error } = useApi(deliveryService.history, [])

  return (
    <>
      <DashHeader title="Delivery History" subtitle="Completed and failed deliveries." />
      <div className="card-jc">
        <div className="table-responsive">
          <table className="table table-jc">
            <thead><tr><th>Order</th><th>Customer</th><th>City</th><th className="text-end">Amount</th><th>Payment</th><th>Finished</th><th>Result</th></tr></thead>
            <tbody>
              {loading && !data ? (
                <tr><td colSpan={7} className="text-center text-muted-jc py-5">Loading…</td></tr>
              ) : error ? (
                <tr><td colSpan={7} className="text-center py-5" style={{ color: 'var(--jc-bad)' }}>{error}</td></tr>
              ) : data.length === 0 ? (
                <tr><td colSpan={7} className="text-center text-muted-jc py-5">No completed deliveries yet.</td></tr>
              ) : (
                data.map((o) => (
                  <tr key={o.id}>
                    <td className="fw-500">{o.orderNumber}</td>
                    <td>{o.shippingAddress.fullName}</td>
                    <td>{o.shippingAddress.city}</td>
                    <td className="text-end">{formatINR(o.totalAmount)}</td>
                    <td>{o.payment?.method}</td>
                    <td className="small">{formatDateTime(o.delivery?.deliveredAt || o.delivery?.updatedAt)}</td>
                    <td>
                      <StatusBadge status={o.delivery?.status} />
                      {o.delivery?.notes && <div className="small text-muted-jc mt-1">{o.delivery.notes}</div>}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </>
  )
}
