import { useState } from 'react'
import toast from 'react-hot-toast'
import { DashHeader } from '../components/dashboard/StatCard'
import Pagination from '../components/Pagination'
import StatusBadge from '../components/StatusBadge'
import useApi from '../hooks/useApi'
import { errorMessage } from '../services/api'
import { paymentService } from '../services/orderService'
import { PAYMENT_METHODS, PAYMENT_STATUSES } from '../utils/constants'
import { formatDateTime, formatINR, humanize } from '../utils/format'

export default function ManagePayments() {
  const [filters, setFilters] = useState({ status: '', method: '', page: 0 })
  const { data, loading, error, setData } = useApi(() => paymentService.search({ ...filters, size: 20 }), [JSON.stringify(filters)])
  const set = (patch) => setFilters((f) => ({ ...f, ...patch, page: 'page' in patch ? patch.page : 0 }))

  const changeStatus = async (p, status) => {
    if (!status || status === p.status) return
    try {
      const updated = await paymentService.updateStatus(p.id, status)
      setData((d) => ({ ...d, content: d.content.map((x) => (x.id === p.id ? updated : x)) }))
      toast.success(`Payment marked ${humanize(status).toLowerCase()}`)
    } catch (err) {
      toast.error(errorMessage(err))
    }
  }

  return (
    <>
      <DashHeader title="Payments" subtitle="Online payments are confirmed automatically after Razorpay signature checks. Use manual changes for refunds and COD corrections." />
      <div className="card-jc">
        <div className="card-jc-head">
          <div className="filters-row">
            <select className="form-select form-select-sm" value={filters.status} onChange={(e) => set({ status: e.target.value })} aria-label="Status">
              <option value="">All statuses</option>
              {PAYMENT_STATUSES.map((s) => <option key={s} value={s}>{humanize(s)}</option>)}
            </select>
            <select className="form-select form-select-sm" value={filters.method} onChange={(e) => set({ method: e.target.value })} aria-label="Method">
              <option value="">All methods</option>
              {PAYMENT_METHODS.map((m) => <option key={m} value={m}>{m === 'COD' ? 'Cash on delivery' : 'Razorpay'}</option>)}
            </select>
          </div>
          <span className="small text-muted-jc">{data ? `${data.totalElements} payments` : ''}</span>
        </div>
        <div className="table-responsive">
          <table className="table table-jc">
            <thead><tr><th>Order</th><th>Customer</th><th>Method</th><th className="text-end">Amount</th><th>Transaction</th><th>Date</th><th>Status</th><th>Change</th></tr></thead>
            <tbody>
              {loading && !data ? (
                <tr><td colSpan={8} className="text-center text-muted-jc py-5">Loading…</td></tr>
              ) : error ? (
                <tr><td colSpan={8} className="text-center py-5" style={{ color: 'var(--jc-bad)' }}>{error}</td></tr>
              ) : data.content.length === 0 ? (
                <tr><td colSpan={8} className="text-center text-muted-jc py-5">No payments found.</td></tr>
              ) : (
                data.content.map((p) => (
                  <tr key={p.id}>
                    <td className="fw-500">{p.orderNumber}</td>
                    <td>{p.customerName}</td>
                    <td>{p.method === 'COD' ? 'COD' : 'Razorpay'}</td>
                    <td className="text-end fw-500">{formatINR(p.amount)}</td>
                    <td className="small text-muted-jc">{p.transactionId || p.gatewayOrderId || '—'}</td>
                    <td className="small">{formatDateTime(p.paidAt || p.createdAt)}</td>
                    <td><StatusBadge status={p.status} /></td>
                    <td style={{ minWidth: 150 }}>
                      <select className="form-select form-select-sm" value="" onChange={(e) => changeStatus(p, e.target.value)} aria-label="Change status">
                        <option value="">Set status…</option>
                        {PAYMENT_STATUSES.filter((s) => s !== p.status).map((s) => <option key={s} value={s}>{humanize(s)}</option>)}
                      </select>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
        {data && <div className="p-3"><Pagination page={data.page} totalPages={data.totalPages} onChange={(page) => set({ page })} /></div>}
      </div>
    </>
  )
}
