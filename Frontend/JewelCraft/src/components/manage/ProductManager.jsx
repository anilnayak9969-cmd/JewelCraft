import { useEffect, useState } from 'react'
import toast from 'react-hot-toast'
import { Link } from 'react-router-dom'
import useApi from '../../hooks/useApi'
import useAuth from '../../hooks/useAuth'
import { errorMessage } from '../../services/api'
import { categoryService, metalRateService, productService } from '../../services/productService'
import { METAL_TYPES } from '../../utils/constants'
import { formatINR, metalLabel } from '../../utils/format'
import { DashHeader } from '../dashboard/StatCard'
import JewelImage from '../JewelImage'
import { ConfirmModal } from '../Modal'
import Pagination from '../Pagination'
import StatusBadge from '../StatusBadge'
import ImageManager from './ImageManager'
import ProductFormModal from './ProductFormModal'

/**
 * Product catalogue screen shared by staff, store managers and admins.
 * Staff can add/edit products and images; managers & admins can also hide/delete
 * products and delete images (the backend enforces the same split).
 */
export default function ProductManager({ title = 'Products' }) {
  const { user } = useAuth()
  const isManager = user.role === 'STORE_MANAGER' || user.role === 'ADMIN'

  const [filters, setFilters] = useState({ search: '', categoryId: '', metalType: '', active: '', page: 0 })
  const [searchInput, setSearchInput] = useState('')
  const [categories, setCategories] = useState([])
  const [rates, setRates] = useState([])
  const [editing, setEditing] = useState(null) // null | 'new' | product
  const [imagesFor, setImagesFor] = useState(null)
  const [deleting, setDeleting] = useState(null)

  const { data, loading, error, reload, setData } = useApi(
    () => productService.searchAll({ ...filters, sort: 'newest', size: 12 }),
    [JSON.stringify(filters)],
  )

  useEffect(() => {
    categoryService.list(true).then(setCategories).catch(() => {})
    metalRateService.list().then(setRates).catch(() => {})
  }, [])

  const set = (patch) => setFilters((f) => ({ ...f, ...patch, page: 'page' in patch ? patch.page : 0 }))

  const replaceRow = (updated) =>
    setData((d) => d && { ...d, content: d.content.map((p) => (p.id === updated.id ? { ...p, ...updated } : p)) })

  const toggleActive = async (p) => {
    try {
      replaceRow(await productService.setAvailability(p.id, !p.active))
      toast.success(p.active ? 'Hidden from the store' : 'Visible in the store')
    } catch (err) {
      toast.error(errorMessage(err))
    }
  }

  const remove = async () => {
    try {
      await productService.remove(deleting.id)
      toast.success('Product deactivated')
      setDeleting(null)
      reload()
    } catch (err) {
      toast.error(errorMessage(err))
    }
  }

  return (
    <>
      <DashHeader title={title} subtitle="Prices are calculated by the server from the current metal rate.">
        <button className="btn btn-jc btn-sm" onClick={() => setEditing('new')}><i className="bi bi-plus-lg me-1" />Add product</button>
      </DashHeader>

      <div className="card-jc">
        <div className="card-jc-head">
          <form
            className="filters-row"
            onSubmit={(e) => {
              e.preventDefault()
              set({ search: searchInput.trim() })
            }}
          >
            <input className="form-control form-control-sm" placeholder="Search name or SKU" value={searchInput} onChange={(e) => setSearchInput(e.target.value)} />
            <select className="form-select form-select-sm" value={filters.categoryId} onChange={(e) => set({ categoryId: e.target.value })} aria-label="Category">
              <option value="">All categories</option>
              {categories.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
            </select>
            <select className="form-select form-select-sm" value={filters.metalType} onChange={(e) => set({ metalType: e.target.value })} aria-label="Metal">
              <option value="">All metals</option>
              {METAL_TYPES.map((m) => <option key={m} value={m}>{metalLabel(m)}</option>)}
            </select>
            <select className="form-select form-select-sm" value={filters.active} onChange={(e) => set({ active: e.target.value })} aria-label="Visibility">
              <option value="">Active &amp; hidden</option>
              <option value="true">Active only</option>
              <option value="false">Hidden only</option>
            </select>
            <button className="btn btn-outline-jc btn-sm">Search</button>
          </form>
          <span className="small text-muted-jc">{data ? `${data.totalElements} products` : ''}</span>
        </div>

        <div className="table-responsive">
          <table className="table table-jc">
            <thead>
              <tr>
                <th>Product</th>
                <th>Category</th>
                <th>Metal</th>
                <th className="text-end">Final price</th>
                <th className="text-end">Stock</th>
                <th>Status</th>
                <th className="text-end">Actions</th>
              </tr>
            </thead>
            <tbody>
              {loading && !data ? (
                <tr><td colSpan={7} className="text-center text-muted-jc py-5">Loading…</td></tr>
              ) : error ? (
                <tr><td colSpan={7} className="text-center py-5" style={{ color: 'var(--jc-bad)' }}>{error}</td></tr>
              ) : data.content.length === 0 ? (
                <tr><td colSpan={7} className="text-center text-muted-jc py-5">No products match these filters.</td></tr>
              ) : (
                data.content.map((p) => (
                  <tr key={p.id}>
                    <td>
                      <div className="d-flex align-items-center gap-3">
                        <div className="thumb-sm"><JewelImage src={p.imageUrl} alt="" metal={p.metalType} category={p.categoryName} name={p.name} /></div>
                        <div>
                          <Link to={`/products/${p.id}`} target="_blank" className="fw-500">{p.name}</Link>
                          <div className="small text-muted-jc">{p.sku}{p.featured ? ' · Featured' : ''}</div>
                        </div>
                      </div>
                    </td>
                    <td>{p.categoryName}</td>
                    <td className="small">{metalLabel(p.metalType)} {p.purity}<br /><span className="text-muted-jc">{Number(p.weight)} g</span></td>
                    <td className="text-end fw-500">{formatINR(p.finalPrice)}</td>
                    <td className="text-end">
                      {p.stockQuantity === 0 ? <StatusBadge status="OUT" label="0" /> : p.stockQuantity <= p.lowStockThreshold ? <StatusBadge status="LOW" label={String(p.stockQuantity)} /> : p.stockQuantity}
                    </td>
                    <td>
                      {isManager ? (
                        <div className="form-check form-switch mb-0">
                          <input className="form-check-input" type="checkbox" checked={p.active} onChange={() => toggleActive(p)} aria-label={p.active ? 'Hide product' : 'Show product'} />
                          <span className="small">{p.active ? 'Active' : 'Hidden'}</span>
                        </div>
                      ) : (
                        <StatusBadge status={p.active ? 'ACTIVE' : 'INACTIVE'} label={p.active ? 'Active' : 'Hidden'} />
                      )}
                    </td>
                    <td className="text-end text-nowrap">
                      <button className="btn btn-outline-jc btn-sm btn-icon me-1" title="Edit" aria-label="Edit" onClick={() => setEditing(p)}><i className="bi bi-pencil" /></button>
                      <button className="btn btn-outline-jc btn-sm btn-icon me-1" title="Images" aria-label="Images" onClick={() => setImagesFor(p.id)}><i className="bi bi-images" /></button>
                      {isManager && p.active && (
                        <button className="btn btn-outline-jc btn-sm btn-icon" title="Delete" aria-label="Delete" onClick={() => setDeleting(p)}><i className="bi bi-trash" /></button>
                      )}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
        {data && <div className="p-3"><Pagination page={data.page} totalPages={data.totalPages} onChange={(page) => set({ page })} /></div>}
      </div>

      <ProductFormModal
        show={Boolean(editing)}
        product={editing === 'new' ? null : editing}
        categories={categories}
        rates={rates}
        onClose={() => setEditing(null)}
        onSaved={(saved, created) => {
          setEditing(null)
          reload()
          if (created) setImagesFor(saved.id)
        }}
      />
      <ImageManager show={Boolean(imagesFor)} productId={imagesFor} canDelete={isManager} onClose={() => setImagesFor(null)} onChanged={replaceRow} />
      <ConfirmModal
        show={Boolean(deleting)}
        title="Delete product?"
        message={`“${deleting?.name}” will be removed from the store. Past orders keep their details, and you can re-activate it later.`}
        confirmText="Delete"
        onConfirm={remove}
        onClose={() => setDeleting(null)}
      />
    </>
  )
}
