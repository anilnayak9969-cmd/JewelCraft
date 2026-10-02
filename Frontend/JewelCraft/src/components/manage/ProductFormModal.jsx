import { useEffect } from 'react'
import { useForm } from 'react-hook-form'
import toast from 'react-hot-toast'
import { applyServerErrors, errorMessage } from '../../services/api'
import { productService } from '../../services/productService'
import { METAL_TYPES } from '../../utils/constants'
import { formatINRExact, metalLabel } from '../../utils/format'
import { estimatePrice } from '../../utils/pricing'
import Modal from '../Modal'

const toNumber = (v) => (v === '' || v === null || v === undefined ? null : Number(v))

const EMPTY = {
  name: '', sku: '', description: '', categoryId: '', metalType: 'GOLD', purity: '', weight: '',
  makingCharges: '', stoneCharges: '0', discount: '0', tax: '3', stockQuantity: '1', lowStockThreshold: '3',
  featured: false, active: true,
}

/**
 * Create / edit a product. There is deliberately no price field: the server works out the
 * final price from the current metal rate. The estimate shown here is only a preview.
 */
export default function ProductFormModal({ show, product, categories, rates, onClose, onSaved }) {
  const isEdit = Boolean(product)
  const {
    register,
    handleSubmit,
    watch,
    reset,
    setValue,
    setError,
    formState: { errors, isSubmitting },
  } = useForm({ defaultValues: EMPTY })

  useEffect(() => {
    if (!show) return
    reset(
      product
        ? {
            ...EMPTY,
            ...Object.fromEntries(Object.keys(EMPTY).map((k) => [k, product[k] ?? EMPTY[k]])),
            categoryId: String(product.categoryId ?? ''),
          }
        : EMPTY,
    )
  }, [show, product, reset])

  const v = watch()
  const purities = rates.filter((r) => r.metalType === v.metalType).map((r) => r.purity)
  const rate = rates.find((r) => r.metalType === v.metalType && r.purity === v.purity)
  const estimate = estimatePrice({ ...v, metalPrice: rate?.pricePerGram })

  // Pick a valid purity whenever the metal changes.
  useEffect(() => {
    if (purities.length && !purities.includes(v.purity)) setValue('purity', purities[0])
  }, [v.metalType, purities.join(), v.purity, setValue]) // eslint-disable-line react-hooks/exhaustive-deps

  const submit = async (values) => {
    const body = {
      ...values,
      categoryId: toNumber(values.categoryId),
      weight: toNumber(values.weight),
      makingCharges: toNumber(values.makingCharges),
      stoneCharges: toNumber(values.stoneCharges) ?? 0,
      discount: toNumber(values.discount) ?? 0,
      tax: toNumber(values.tax) ?? 0,
      stockQuantity: isEdit ? undefined : toNumber(values.stockQuantity) ?? 0,
      lowStockThreshold: toNumber(values.lowStockThreshold),
      sku: values.sku?.trim() || null,
    }
    try {
      const saved = isEdit ? await productService.update(product.id, body) : await productService.create(body)
      toast.success(isEdit ? 'Product updated' : 'Product created')
      onSaved(saved, !isEdit)
    } catch (err) {
      if (!applyServerErrors(err, setError)) toast.error(errorMessage(err))
    }
  }

  const cls = (name, base = 'form-control') => `${base} ${errors[name] ? 'is-invalid' : ''}`
  const money = { min: { value: 0, message: 'Cannot be negative' } }

  return (
    <Modal show={show} title={isEdit ? 'Edit product' : 'New product'} size="xl" onClose={onClose}>
      <form onSubmit={handleSubmit(submit)} noValidate>
        <div className="row g-4">
          <div className="col-lg-8">
            <div className="row g-3">
              <div className="col-md-8">
                <label className="form-label" htmlFor="pf-name">Product name</label>
                <input id="pf-name" className={cls('name')} {...register('name', { required: 'Product name is required', minLength: { value: 3, message: 'Product name must be 3-150 characters' }, maxLength: { value: 150, message: 'Product name must be 3-150 characters' } })} />
                <div className="invalid-feedback">{errors.name?.message}</div>
              </div>
              <div className="col-md-4">
                <label className="form-label" htmlFor="pf-sku">SKU <span className="text-muted-jc">(auto if blank)</span></label>
                <input id="pf-sku" className={cls('sku')} {...register('sku', { maxLength: { value: 40, message: 'At most 40 characters' } })} />
                <div className="invalid-feedback">{errors.sku?.message}</div>
              </div>
              <div className="col-md-4">
                <label className="form-label" htmlFor="pf-cat">Category</label>
                <select id="pf-cat" className={cls('categoryId', 'form-select')} {...register('categoryId', { required: 'Category is required' })}>
                  <option value="">Select…</option>
                  {categories.map((c) => <option key={c.id} value={c.id}>{c.name}{c.active ? '' : ' (inactive)'}</option>)}
                </select>
                <div className="invalid-feedback">{errors.categoryId?.message}</div>
              </div>
              <div className="col-md-4">
                <label className="form-label" htmlFor="pf-metal">Metal</label>
                <select id="pf-metal" className="form-select" {...register('metalType', { required: true })}>
                  {METAL_TYPES.map((m) => <option key={m} value={m}>{metalLabel(m)}</option>)}
                </select>
              </div>
              <div className="col-md-4">
                <label className="form-label" htmlFor="pf-purity">Purity</label>
                <select id="pf-purity" className={cls('purity', 'form-select')} {...register('purity', { required: 'Purity is required' })}>
                  {purities.length === 0 && <option value="">No rate set for this metal</option>}
                  {purities.map((p) => <option key={p} value={p}>{p}</option>)}
                </select>
                <div className="invalid-feedback">{errors.purity?.message}</div>
              </div>
              <div className="col-md-4">
                <label className="form-label" htmlFor="pf-weight">Weight (g)</label>
                <input id="pf-weight" type="number" step="0.001" className={cls('weight')} {...register('weight', { required: 'Weight is required', min: { value: 0.001, message: 'Weight must be greater than 0' }, max: { value: 10000, message: 'Weight looks too large' } })} />
                <div className="invalid-feedback">{errors.weight?.message}</div>
              </div>
              <div className="col-md-4">
                <label className="form-label" htmlFor="pf-making">Making charges (₹)</label>
                <input id="pf-making" type="number" step="0.01" className={cls('makingCharges')} {...register('makingCharges', { required: 'Making charges are required', ...money })} />
                <div className="invalid-feedback">{errors.makingCharges?.message}</div>
              </div>
              <div className="col-md-4">
                <label className="form-label" htmlFor="pf-stone">Stone charges (₹)</label>
                <input id="pf-stone" type="number" step="0.01" className={cls('stoneCharges')} {...register('stoneCharges', money)} />
                <div className="invalid-feedback">{errors.stoneCharges?.message}</div>
              </div>
              <div className="col-md-4">
                <label className="form-label" htmlFor="pf-discount">Discount (₹)</label>
                <input id="pf-discount" type="number" step="0.01" className={cls('discount')} {...register('discount', money)} />
                <div className="invalid-feedback">{errors.discount?.message}</div>
              </div>
              <div className="col-md-4">
                <label className="form-label" htmlFor="pf-tax">GST (%)</label>
                <input id="pf-tax" type="number" step="0.01" className={cls('tax')} {...register('tax', { ...money, max: { value: 50, message: 'Tax cannot exceed 50%' } })} />
                <div className="invalid-feedback">{errors.tax?.message}</div>
              </div>
              {!isEdit && (
                <div className="col-md-4">
                  <label className="form-label" htmlFor="pf-stock">Opening stock</label>
                  <input id="pf-stock" type="number" className={cls('stockQuantity')} {...register('stockQuantity', { min: { value: 0, message: 'Stock cannot be negative' }, max: { value: 100000, message: 'Too large' } })} />
                  <div className="invalid-feedback">{errors.stockQuantity?.message}</div>
                </div>
              )}
              <div className="col-md-4">
                <label className="form-label" htmlFor="pf-low">Low-stock alert at</label>
                <input id="pf-low" type="number" className={cls('lowStockThreshold')} {...register('lowStockThreshold', { min: { value: 0, message: 'Cannot be negative' } })} />
                <div className="invalid-feedback">{errors.lowStockThreshold?.message}</div>
              </div>
              <div className="col-12">
                <label className="form-label" htmlFor="pf-desc">Description</label>
                <textarea id="pf-desc" rows={3} className={cls('description')} {...register('description', { maxLength: { value: 5000, message: 'Too long' } })} />
              </div>
              <div className="col-12 d-flex gap-4">
                <div className="form-check form-switch">
                  <input id="pf-featured" type="checkbox" className="form-check-input" {...register('featured')} />
                  <label htmlFor="pf-featured" className="form-check-label small">Featured (Signature Edit)</label>
                </div>
                <div className="form-check form-switch">
                  <input id="pf-active" type="checkbox" className="form-check-input" {...register('active')} />
                  <label htmlFor="pf-active" className="form-check-label small">Visible in store</label>
                </div>
              </div>
            </div>
          </div>

          <div className="col-lg-4">
            <div className="breakdown">
              <div className="small-caps mb-2">Price estimate</div>
              <div className="row-line">
                <span>Rate {v.purity || ''}</span>
                <span>{rate ? `${formatINRExact(rate.pricePerGram)}/g` : '—'}</span>
              </div>
              <div className="row-line"><span>Metal value</span><span>{formatINRExact(estimate.metalValue)}</span></div>
              <div className="row-line"><span>+ Making</span><span>{formatINRExact(v.makingCharges)}</span></div>
              <div className="row-line"><span>+ Stones</span><span>{formatINRExact(v.stoneCharges)}</span></div>
              <div className="row-line"><span>− Discount</span><span>{formatINRExact(v.discount)}</span></div>
              <div className="row-line"><span>+ GST {Number(v.tax) || 0}%</span><span>{formatINRExact(estimate.taxAmount)}</span></div>
              <div className="row-line total"><span>Final price</span><span>{formatINRExact(estimate.finalPrice)}</span></div>
              <p className="small text-muted-jc mt-2 mb-0">The server calculates the real price from the live metal rate when you save.</p>
            </div>
          </div>
        </div>
        <div className="d-flex justify-content-end gap-2 mt-4 pt-3 border-top">
          <button type="button" className="btn btn-outline-jc btn-sm" onClick={onClose}>Cancel</button>
          <button className="btn btn-jc btn-sm" disabled={isSubmitting}>{isSubmitting ? 'Saving…' : isEdit ? 'Save changes' : 'Create product'}</button>
        </div>
      </form>
    </Modal>
  )
}
