import { useState } from 'react'
import toast from 'react-hot-toast'
import useApi from '../../hooks/useApi'
import { errorMessage } from '../../services/api'
import { inventoryService } from '../../services/dashboardService'
import { formatDateTime } from '../../utils/format'
import { DashHeader } from '../dashboard/StatCard'
import JewelImage from '../JewelImage'
import Pagination from '../Pagination'
import StatusBadge from '../StatusBadge'

/** One editable stock row; saves quantity and low-stock threshold together. */
function StockRow({ item, onSaved }) {
  const [qty, setQty] = useState(String(item.quantity))
  const [threshold, setThreshold] = useState(String(item.lowStockThreshold))
  const [saving, setSaving] = useState(false)
  const dirty = qty !== String(item.quantity) || threshold !== String(item.lowStockThreshold)
  const valid = /^\d+$/.test(qty) && /^\d+$/.test(threshold) && Number(qty) <= 100000

  const save = async () => {
    setSaving(true)
    try {
      const updated = await inventoryService.update(item.productId, { quantity: Number(qty), lowStockThreshold: Number(threshold) })
      onSaved(updated)
      toast.success(`Stock updated for ${item.productName}`)
    } catch (err) {
      toast.error(errorMessage(err))
    } finally {
      setSaving(false)
    }
  }

  return (
    <tr>
      <td>
        <div className="d-flex align-items-center gap-3">
          <div className="thumb-sm"><JewelImage src={item.imageUrl} alt="" category={item.categoryName} name={item.productName} /></div>
          <div>
            <div className="fw-500">{item.productName}</div>
            <div className="small text-muted-jc">{item.sku} · {item.categoryName}{item.active ? '' : ' · hidden'}</div>
          </div>
        </div>
      </td>
      <td>
        {item.quantity === 0 ? <StatusBadge status="OUT" label="Out of stock" /> : item.lowStock ? <StatusBadge status="LOW" label="Low stock" /> : <StatusBadge status="IN_STOCK" label="In stock" />}
      </td>
      <td style={{ width: 120 }}>
        <input type="number" min="0" className="form-control form-control-sm" value={qty} onChange={(e) => setQty(e.target.value)} aria-label="Quantity" />
      </td>
      <td style={{ width: 120 }}>
        <input type="number" min="0" className="form-control form-control-sm" value={threshold} onChange={(e) => setThreshold(e.target.value)} aria-label="Low-stock threshold" />
      </td>
      <td className="small text-muted-jc">{formatDateTime(item.updatedAt)}</td>
      <td className="text-end">
        <button className="btn btn-jc btn-sm" disabled={!dirty || !valid || saving} onClick={save}>{saving ? '…' : 'Save'}</button>
      </td>
    </tr>
  )
}

export default function InventoryManager({ title = 'Inventory' }) {
  const [filters, setFilters] = useState({ search: '', lowOnly: false, page: 0 })
  const [searchInput, setSearchInput] = useState('')
  const { data, loading, error, setData } = useApi(() => inventoryService.list({ ...filters, size: 20 }), [JSON.stringify(filters)])
  const set = (patch) => setFilters((f) => ({ ...f, ...patch, page: 'page' in patch ? patch.page : 0 }))

  return (
    <>
      <DashHeader title={title} subtitle="Update stock counts and the level at which a product is flagged as low." />
      <div className="card-jc">
        <div className="card-jc-head">
          <form
            className="filters-row"
            onSubmit={(e) => {
              e.preventDefault()
              set({ search: searchInput.trim() })
            }}
          >
            <input className="form-control form-control-sm" placeholder="Search product or SKU" value={searchInput} onChange={(e) => setSearchInput(e.target.value)} />
            <button className="btn btn-outline-jc btn-sm">Search</button>
            <div className="form-check form-switch ms-2 mb-0">
              <input id="low-only" className="form-check-input" type="checkbox" checked={filters.lowOnly} onChange={(e) => set({ lowOnly: e.target.checked })} />
              <label htmlFor="low-only" className="form-check-label small">Low stock only</label>
            </div>
          </form>
          <span className="small text-muted-jc">{data ? `${data.totalElements} products` : ''}</span>
        </div>
        <div className="table-responsive">
          <table className="table table-jc">
            <thead>
              <tr><th>Product</th><th>Status</th><th>Quantity</th><th>Alert at</th><th>Updated</th><th /></tr>
            </thead>
            <tbody>
              {loading && !data ? (
                <tr><td colSpan={6} className="text-center text-muted-jc py-5">Loading…</td></tr>
              ) : error ? (
                <tr><td colSpan={6} className="text-center py-5" style={{ color: 'var(--jc-bad)' }}>{error}</td></tr>
              ) : data.content.length === 0 ? (
                <tr><td colSpan={6} className="text-center text-muted-jc py-5">Nothing to show.</td></tr>
              ) : (
                data.content.map((item) => (
                  <StockRow
                    key={`${item.productId}-${item.quantity}-${item.lowStockThreshold}`}
                    item={item}
                    onSaved={(u) => setData((d) => ({ ...d, content: d.content.map((x) => (x.productId === u.productId ? u : x)) }))}
                  />
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
