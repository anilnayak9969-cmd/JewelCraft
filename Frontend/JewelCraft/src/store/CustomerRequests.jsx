import { useState } from 'react'
import toast from 'react-hot-toast'
import { DashHeader } from '../components/dashboard/StatCard'
import Modal from '../components/Modal'
import Pagination from '../components/Pagination'
import StatusBadge from '../components/StatusBadge'
import useApi from '../hooks/useApi'
import { errorMessage } from '../services/api'
import { requestService } from '../services/userService'
import { REQUEST_STATUSES } from '../utils/constants'
import { formatDateTime, humanize } from '../utils/format'

/** Support tickets from customers – staff, store managers and admins. */
export default function CustomerRequests() {
  const [filters, setFilters] = useState({ status: 'OPEN', page: 0 })
  const [open, setOpen] = useState(null)
  const [status, setStatus] = useState('')
  const [response, setResponse] = useState('')
  const [saving, setSaving] = useState(false)
  const { data, loading, error, reload } = useApi(() => requestService.search({ ...filters, size: 15 }), [JSON.stringify(filters)])

  const show = (r) => {
    setOpen(r)
    setStatus(r.status === 'OPEN' ? 'IN_PROGRESS' : r.status)
    setResponse(r.response || '')
  }

  const save = async () => {
    setSaving(true)
    try {
      await requestService.update(open.id, { status, response: response || null })
      toast.success('Request updated')
      setOpen(null)
      reload()
    } catch (err) {
      toast.error(errorMessage(err))
    } finally {
      setSaving(false)
    }
  }

  return (
    <>
      <DashHeader title="Customer Requests" subtitle="Returns, exchanges, customisation and questions from customers." />
      <div className="nav-tabs-jc mb-3">
        {['', ...REQUEST_STATUSES].map((s) => (
          <button key={s || 'all'} className={filters.status === s ? 'active' : ''} onClick={() => setFilters({ status: s, page: 0 })}>{s ? humanize(s) : 'All'}</button>
        ))}
      </div>
      <div className="card-jc">
        <div className="table-responsive">
          <table className="table table-jc">
            <thead><tr><th>Customer</th><th>Type</th><th>Subject</th><th>Order</th><th>Raised</th><th>Status</th><th /></tr></thead>
            <tbody>
              {loading && !data ? (
                <tr><td colSpan={7} className="text-center text-muted-jc py-5">Loading…</td></tr>
              ) : error ? (
                <tr><td colSpan={7} className="text-center py-5" style={{ color: 'var(--jc-bad)' }}>{error}</td></tr>
              ) : data.content.length === 0 ? (
                <tr><td colSpan={7} className="text-center text-muted-jc py-5">No requests here.</td></tr>
              ) : (
                data.content.map((r) => (
                  <tr key={r.id}>
                    <td>{r.customerName}<div className="small text-muted-jc">{r.customerEmail}</div></td>
                    <td>{humanize(r.type)}</td>
                    <td style={{ maxWidth: 280 }}><div className="text-truncate">{r.subject}</div></td>
                    <td className="small">{r.orderNumber || '—'}</td>
                    <td className="small">{formatDateTime(r.createdAt)}</td>
                    <td><StatusBadge status={r.status} /></td>
                    <td className="text-end"><button className="btn btn-outline-jc btn-sm" onClick={() => show(r)}>Respond</button></td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
        {data && <div className="p-3"><Pagination page={data.page} totalPages={data.totalPages} onChange={(page) => setFilters((f) => ({ ...f, page }))} /></div>}
      </div>

      <Modal
        show={Boolean(open)}
        title={open ? open.subject : ''}
        size="lg"
        onClose={() => setOpen(null)}
        footer={
          <>
            <button className="btn btn-outline-jc btn-sm" onClick={() => setOpen(null)}>Close</button>
            <button className="btn btn-jc btn-sm" disabled={saving} onClick={save}>{saving ? 'Saving…' : 'Save'}</button>
          </>
        }
      >
        {open && (
          <>
            <div className="small text-muted-jc mb-2">
              {humanize(open.type)} · {open.customerName} ({open.customerEmail}){open.orderNumber ? ` · Order ${open.orderNumber}` : ''} · {formatDateTime(open.createdAt)}
            </div>
            <div className="bg-cream p-3 mb-4" style={{ whiteSpace: 'pre-wrap' }}>{open.message}</div>
            <div className="row g-3">
              <div className="col-md-4">
                <label className="form-label" htmlFor="rq-status">Status</label>
                <select id="rq-status" className="form-select" value={status} onChange={(e) => setStatus(e.target.value)}>
                  {REQUEST_STATUSES.map((s) => <option key={s} value={s}>{humanize(s)}</option>)}
                </select>
              </div>
              <div className="col-12">
                <label className="form-label" htmlFor="rq-reply">Reply to customer</label>
                <textarea id="rq-reply" className="form-control" rows={4} maxLength={2000} value={response} onChange={(e) => setResponse(e.target.value)} />
              </div>
            </div>
            {open.handledBy && <div className="small text-muted-jc mt-2">Last handled by {open.handledBy}</div>}
          </>
        )}
      </Modal>
    </>
  )
}
