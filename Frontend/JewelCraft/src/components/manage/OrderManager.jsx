import { useState } from 'react'
import useApi from '../../hooks/useApi'
import usePolling from '../../hooks/usePolling'
import { orderService } from '../../services/orderService'
import { ORDER_STATUSES } from '../../utils/constants'
import { formatDate, formatINR, humanize } from '../../utils/format'
import { DashHeader } from '../dashboard/StatCard'
import LiveBadge from '../LiveBadge'
import Pagination from '../Pagination'
import StatusBadge from '../StatusBadge'
import OrderManageModal from './OrderManageModal'

/** Order list for staff, store managers and admins (actions depend on the signed-in role). */
export default function OrderManager({ title = 'Orders', subtitle }) {
  const [filters, setFilters] = useState({ status: '', search: '', page: 0 })
  const [searchInput, setSearchInput] = useState('')
  const [openId, setOpenId] = useState(null)
  const { data, loading, error, setData, reload } = useApi(() => orderService.search({ ...filters, size: 15 }), [JSON.stringify(filters)])
  // New orders and status changes from other team members show up without reloading the page.
  const { lastUpdated } = usePolling(reload, 20000)

  const set = (patch) => setFilters((f) => ({ ...f, ...patch, page: 'page' in patch ? patch.page : 0 }))
  const replaceRow = (o) => setData((d) => d && { ...d, content: d.content.map((x) => (x.id === o.id ? o : x)) })

  return (
    <>
      <DashHeader title={title} subtitle={subtitle}>
        <LiveBadge lastUpdated={lastUpdated} />
      </DashHeader>

      <div className="nav-tabs-jc mb-3">
        {['', ...ORDER_STATUSES].map((s) => (
          <button key={s || 'all'} className={filters.status === s ? 'active' : ''} onClick={() => set({ status: s })}>
            {s ? humanize(s) : 'All'}
          </button>
        ))}
      </div>

      <div className="card-jc">
        <div className="card-jc-head">
          <form
            className="filters-row"
            onSubmit={(e) => {
              e.preventDefault()
              set({ search: searchInput.trim() })
            }}
          >
            <input className="form-control form-control-sm" placeholder="Order number, customer name or email" style={{ minWidth: 260 }} value={searchInput} onChange={(e) => setSearchInput(e.target.value)} />
            <button className="btn btn-outline-jc btn-sm">Search</button>
          </form>
          <span className="small text-muted-jc">{data ? `${data.totalElements} orders` : ''}</span>
        </div>
        <div className="table-responsive">
          <table className="table table-jc">
            <thead>
              <tr>
                <th>Order</th>
                <th>Customer</th>
                <th>Date</th>
                <th className="text-end">Items</th>
                <th className="text-end">Total</th>
                <th>Payment</th>
                <th>Status</th>
                <th>Delivery</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {loading && !data ? (
                <tr><td colSpan={9} className="text-center text-muted-jc py-5">Loading…</td></tr>
              ) : error ? (
                <tr><td colSpan={9} className="text-center py-5" style={{ color: 'var(--jc-bad)' }}>{error}</td></tr>
              ) : data.content.length === 0 ? (
                <tr><td colSpan={9} className="text-center text-muted-jc py-5">No orders found.</td></tr>
              ) : (
                data.content.map((o) => (
                  <tr key={o.id}>
                    <td className="fw-500">{o.orderNumber}</td>
                    <td>{o.customerName}<div className="small text-muted-jc">{o.customerPhone}</div></td>
                    <td className="small">{formatDate(o.createdAt)}</td>
                    <td className="text-end">{o.totalItems}</td>
                    <td className="text-end fw-500">{formatINR(o.totalAmount)}</td>
                    <td>{o.payment && <StatusBadge status={o.payment.status} label={`${o.payment.method} · ${humanize(o.payment.status)}`} />}</td>
                    <td><StatusBadge status={o.status} /></td>
                    <td className="small">{o.delivery ? <>{o.delivery.partnerName}<div className="text-muted-jc">{humanize(o.delivery.status)}</div></> : <span className="text-muted-jc">—</span>}</td>
                    <td className="text-end"><button className="btn btn-outline-jc btn-sm" onClick={() => setOpenId(o.id)}>Manage</button></td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
        {data && <div className="p-3"><Pagination page={data.page} totalPages={data.totalPages} onChange={(page) => set({ page })} /></div>}
      </div>

      <OrderManageModal orderId={openId} onClose={() => setOpenId(null)} onUpdated={replaceRow} />
    </>
  )
}
