import { useEffect, useState } from 'react'
import { useForm } from 'react-hook-form'
import toast from 'react-hot-toast'
import { DashHeader } from '../components/dashboard/StatCard'
import JewelImage from '../components/JewelImage'
import Modal, { ConfirmModal } from '../components/Modal'
import Pagination from '../components/Pagination'
import useApi from '../hooks/useApi'
import { applyServerErrors, errorMessage } from '../services/api'
import { metalRateService, productService } from '../services/productService'
import { METAL_TYPES } from '../utils/constants'
import { formatDateTime, formatINR, formatINRExact, metalLabel } from '../utils/format'
import { estimatePrice } from '../utils/pricing'

/** One metal rate row with an inline price editor. */
function RateRow({ rate, onSaved, onDelete }) {
  const [price, setPrice] = useState(String(rate.pricePerGram))
  const [saving, setSaving] = useState(false)
  const dirty = Number(price) !== Number(rate.pricePerGram)

  const save = async () => {
    if (!(Number(price) > 0)) {
      toast.error('Price per gram must be greater than 0')
      return
    }
    setSaving(true)
    try {
      const updated = await metalRateService.update(rate.id, Number(price))
      toast.success(`${metalLabel(rate.metalType)} ${rate.purity} updated · ${updated.productsRepriced ?? 0} products re-priced`)
      onSaved(updated)
    } catch (err) {
      toast.error(errorMessage(err))
    } finally {
      setSaving(false)
    }
  }

  return (
    <tr>
      <td className="fw-500">{metalLabel(rate.metalType)}</td>
      <td>{rate.purity}</td>
      <td style={{ width: 200 }}>
        <div className="input-group input-group-sm">
          <span className="input-group-text">₹</span>
          <input type="number" step="0.01" min="0.01" className="form-control" value={price} onChange={(e) => setPrice(e.target.value)} aria-label="Price per gram" />
          <span className="input-group-text">/g</span>
        </div>
      </td>
      <td className="small text-muted-jc">{formatDateTime(rate.updatedAt)}</td>
      <td className="text-end text-nowrap">
        <button className="btn btn-jc btn-sm me-1" disabled={!dirty || saving} onClick={save}>{saving ? '…' : 'Update'}</button>
        <button className="btn btn-outline-jc btn-sm btn-icon" aria-label="Delete rate" onClick={() => onDelete(rate)}><i className="bi bi-trash" /></button>
      </td>
    </tr>
  )
}

function NewRateForm({ onSaved, onCancel }) {
  const { register, handleSubmit, setError, formState: { errors, isSubmitting } } = useForm({ defaultValues: { metalType: 'GOLD', purity: '', pricePerGram: '' } })
  const submit = async (v) => {
    try {
      onSaved(await metalRateService.create({ ...v, purity: v.purity.trim(), pricePerGram: Number(v.pricePerGram) }))
      toast.success('Metal rate added')
    } catch (err) {
      if (!applyServerErrors(err, setError)) toast.error(errorMessage(err))
    }
  }
  return (
    <form onSubmit={handleSubmit(submit)} noValidate>
      <div className="row g-3">
        <div className="col-md-4">
          <label className="form-label" htmlFor="nr-metal">Metal</label>
          <select id="nr-metal" className="form-select" {...register('metalType')}>
            {METAL_TYPES.map((m) => <option key={m} value={m}>{metalLabel(m)}</option>)}
          </select>
        </div>
        <div className="col-md-4">
          <label className="form-label" htmlFor="nr-purity">Purity</label>
          <input id="nr-purity" placeholder="e.g. 22K, 925, 950" className={`form-control ${errors.purity ? 'is-invalid' : ''}`} {...register('purity', { required: 'Purity is required', maxLength: { value: 20, message: 'Too long' } })} />
          <div className="invalid-feedback">{errors.purity?.message}</div>
        </div>
        <div className="col-md-4">
          <label className="form-label" htmlFor="nr-price">Price per gram (₹)</label>
          <input id="nr-price" type="number" step="0.01" className={`form-control ${errors.pricePerGram ? 'is-invalid' : ''}`} {...register('pricePerGram', { required: 'Price per gram is required', min: { value: 0.01, message: 'Must be greater than 0' } })} />
          <div className="invalid-feedback">{errors.pricePerGram?.message}</div>
        </div>
      </div>
      <div className="d-flex justify-content-end gap-2 mt-4">
        <button type="button" className="btn btn-outline-jc btn-sm" onClick={onCancel}>Cancel</button>
        <button className="btn btn-jc btn-sm" disabled={isSubmitting}>Add rate</button>
      </div>
    </form>
  )
}

/** Edit the price-related fields of one product, with a live estimate. */
function PricingForm({ product, onSaved, onCancel }) {
  const { register, handleSubmit, watch, setError, formState: { errors, isSubmitting } } = useForm({
    defaultValues: {
      weight: product.weight, makingCharges: product.makingCharges, stoneCharges: product.stoneCharges ?? 0,
      discount: product.discount ?? 0, tax: product.tax ?? 3,
    },
  })
  const v = watch()
  const est = estimatePrice({ ...v, metalPrice: product.metalPrice })

  const submit = async (values) => {
    try {
      const body = Object.fromEntries(Object.entries(values).map(([k, x]) => [k, Number(x)]))
      const updated = await productService.updatePricing(product.id, body)
      toast.success(`New price ${formatINR(updated.finalPrice)}`)
      onSaved(updated)
    } catch (err) {
      if (!applyServerErrors(err, setError)) toast.error(errorMessage(err))
    }
  }

  const field = (name, label, rules, step = '0.01') => (
    <div className="col-6">
      <label className="form-label" htmlFor={`pr-${name}`}>{label}</label>
      <input id={`pr-${name}`} type="number" step={step} className={`form-control ${errors[name] ? 'is-invalid' : ''}`} {...register(name, rules)} />
      <div className="invalid-feedback">{errors[name]?.message}</div>
    </div>
  )
  const nonNeg = { required: 'Required', min: { value: 0, message: 'Cannot be negative' } }

  return (
    <form onSubmit={handleSubmit(submit)} noValidate>
      <div className="row g-4">
        <div className="col-md-6">
          <div className="row g-3">
            {field('weight', 'Weight (g)', { required: 'Weight is required', min: { value: 0.001, message: 'Must be greater than 0' } }, '0.001')}
            {field('makingCharges', 'Making (₹)', nonNeg)}
            {field('stoneCharges', 'Stones (₹)', nonNeg)}
            {field('discount', 'Discount (₹)', nonNeg)}
            {field('tax', 'GST (%)', { ...nonNeg, max: { value: 50, message: 'Tax cannot exceed 50%' } })}
          </div>
        </div>
        <div className="col-md-6">
          <div className="breakdown">
            <div className="row-line"><span>{metalLabel(product.metalType)} {product.purity} rate</span><span>{formatINRExact(product.metalPrice)}/g</span></div>
            <div className="row-line"><span>Metal value</span><span>{formatINRExact(est.metalValue)}</span></div>
            <div className="row-line"><span>Subtotal</span><span>{formatINRExact(est.subtotal)}</span></div>
            <div className="row-line"><span>After discount</span><span>{formatINRExact(est.discounted)}</span></div>
            <div className="row-line"><span>GST</span><span>{formatINRExact(est.taxAmount)}</span></div>
            <div className="row-line total"><span>New price</span><span>{formatINRExact(est.finalPrice)}</span></div>
            <div className="small text-muted-jc mt-2">Current: {formatINRExact(product.finalPrice)}</div>
          </div>
        </div>
      </div>
      <div className="d-flex justify-content-end gap-2 mt-4">
        <button type="button" className="btn btn-outline-jc btn-sm" onClick={onCancel}>Cancel</button>
        <button className="btn btn-jc btn-sm" disabled={isSubmitting}>{isSubmitting ? 'Saving…' : 'Save pricing'}</button>
      </div>
    </form>
  )
}

export default function PriceManagement() {
  const [rates, setRates] = useState(null)
  const [addingRate, setAddingRate] = useState(false)
  const [deletingRate, setDeletingRate] = useState(null)
  const [filters, setFilters] = useState({ search: '', metalType: '', page: 0 })
  const [searchInput, setSearchInput] = useState('')
  const [pricing, setPricing] = useState(null)
  const { data: products, reload, setData } = useApi(() => productService.searchAll({ ...filters, sort: 'name_asc', size: 10 }), [JSON.stringify(filters)])

  const loadRates = () => metalRateService.list().then(setRates).catch((err) => toast.error(errorMessage(err)))
  useEffect(() => {
    loadRates()
  }, [])

  const set = (patch) => setFilters((f) => ({ ...f, ...patch, page: 'page' in patch ? patch.page : 0 }))

  const deleteRate = async () => {
    try {
      await metalRateService.remove(deletingRate.id)
      toast.success('Rate deleted')
      setDeletingRate(null)
      loadRates()
    } catch (err) {
      toast.error(errorMessage(err))
    }
  }

  return (
    <>
      <DashHeader title="Price Management" subtitle="Updating a metal rate re-prices every product of that metal and purity on the server." />

      <div className="card-jc mb-4">
        <div className="card-jc-head">
          <h5>Today’s metal rates</h5>
          <button className="btn btn-jc btn-sm" onClick={() => setAddingRate(true)}><i className="bi bi-plus-lg me-1" />Add rate</button>
        </div>
        <div className="table-responsive">
          <table className="table table-jc">
            <thead><tr><th>Metal</th><th>Purity</th><th>Price per gram</th><th>Last updated</th><th /></tr></thead>
            <tbody>
              {!rates ? (
                <tr><td colSpan={5} className="text-center text-muted-jc py-4">Loading…</td></tr>
              ) : (
                rates.map((r) => (
                  <RateRow
                    key={`${r.id}-${r.pricePerGram}`}
                    rate={r}
                    onDelete={setDeletingRate}
                    onSaved={(u) => {
                      setRates((list) => list.map((x) => (x.id === u.id ? u : x)))
                      reload()
                    }}
                  />
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      <div className="card-jc">
        <div className="card-jc-head">
          <h5>Product pricing</h5>
          <form className="filters-row" onSubmit={(e) => { e.preventDefault(); set({ search: searchInput.trim() }) }}>
            <input className="form-control form-control-sm" placeholder="Search product" value={searchInput} onChange={(e) => setSearchInput(e.target.value)} />
            <select className="form-select form-select-sm" value={filters.metalType} onChange={(e) => set({ metalType: e.target.value })} aria-label="Metal">
              <option value="">All metals</option>
              {METAL_TYPES.map((m) => <option key={m} value={m}>{metalLabel(m)}</option>)}
            </select>
            <button className="btn btn-outline-jc btn-sm">Search</button>
          </form>
        </div>
        <div className="table-responsive">
          <table className="table table-jc">
            <thead>
              <tr>
                <th>Product</th><th>Metal</th><th className="text-end">Weight</th><th className="text-end">Metal value</th>
                <th className="text-end">Making</th><th className="text-end">Stones</th><th className="text-end">Discount</th>
                <th className="text-end">GST</th><th className="text-end">Final price</th><th />
              </tr>
            </thead>
            <tbody>
              {!products ? (
                <tr><td colSpan={10} className="text-center text-muted-jc py-4">Loading…</td></tr>
              ) : products.content.length === 0 ? (
                <tr><td colSpan={10} className="text-center text-muted-jc py-4">No products found.</td></tr>
              ) : (
                products.content.map((p) => (
                  <tr key={p.id}>
                    <td>
                      <div className="d-flex align-items-center gap-2">
                        <div className="thumb-sm" style={{ width: 36, height: 40 }}><JewelImage src={p.imageUrl} alt="" metal={p.metalType} category={p.categoryName} name={p.name} /></div>
                        <span className="fw-500">{p.name}</span>
                      </div>
                    </td>
                    <td className="small">{metalLabel(p.metalType)} {p.purity}</td>
                    <td className="text-end">{Number(p.weight)} g</td>
                    <td className="text-end">{formatINR(p.metalValue)}</td>
                    <td className="text-end">{formatINR(p.makingCharges)}</td>
                    <td className="text-end">{formatINR(p.stoneCharges)}</td>
                    <td className="text-end">{formatINR(p.discount)}</td>
                    <td className="text-end">{Number(p.tax)}%</td>
                    <td className="text-end fw-500">{formatINR(p.finalPrice)}</td>
                    <td className="text-end"><button className="btn btn-outline-jc btn-sm" onClick={() => setPricing(p)}>Edit</button></td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
        {products && <div className="p-3"><Pagination page={products.page} totalPages={products.totalPages} onChange={(page) => set({ page })} /></div>}
      </div>

      <Modal show={addingRate} title="Add metal rate" onClose={() => setAddingRate(false)}>
        {addingRate && <NewRateForm onCancel={() => setAddingRate(false)} onSaved={() => { setAddingRate(false); loadRates() }} />}
      </Modal>
      <Modal show={Boolean(pricing)} title={pricing ? `Pricing · ${pricing.name}` : 'Pricing'} size="lg" onClose={() => setPricing(null)}>
        {pricing && (
          <PricingForm
            product={pricing}
            onCancel={() => setPricing(null)}
            onSaved={(u) => {
              setPricing(null)
              setData((d) => ({ ...d, content: d.content.map((x) => (x.id === u.id ? u : x)) }))
            }}
          />
        )}
      </Modal>
      <ConfirmModal
        show={Boolean(deletingRate)}
        title="Delete metal rate?"
        message={`${metalLabel(deletingRate?.metalType)} ${deletingRate?.purity} will be removed. Products using it must be moved to another purity first.`}
        confirmText="Delete"
        onConfirm={deleteRate}
        onClose={() => setDeletingRate(null)}
      />
    </>
  )
}
