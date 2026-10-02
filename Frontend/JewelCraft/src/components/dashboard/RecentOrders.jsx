import { Link } from 'react-router-dom'
import { formatDate, formatINR } from '../../utils/format'
import StatusBadge from '../StatusBadge'

export default function RecentOrders({ orders = [], manageLink }) {
  return (
    <div className="card-jc h-100">
      <div className="card-jc-head">
        <h5>Recent orders</h5>
        {manageLink && <Link to={manageLink} className="btn-link-jc">View all</Link>}
      </div>
      <div className="table-responsive">
        <table className="table table-jc">
          <thead><tr><th>Order</th><th>Customer</th><th>Date</th><th className="text-end">Total</th><th>Status</th></tr></thead>
          <tbody>
            {orders.length === 0 ? (
              <tr><td colSpan={5} className="text-center text-muted-jc py-4">No orders yet.</td></tr>
            ) : (
              orders.map((o) => (
                <tr key={o.id}>
                  <td className="fw-500">{o.orderNumber}</td>
                  <td>{o.customerName}</td>
                  <td className="small">{formatDate(o.createdAt)}</td>
                  <td className="text-end">{formatINR(o.totalAmount)}</td>
                  <td><StatusBadge status={o.status} /></td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  )
}
