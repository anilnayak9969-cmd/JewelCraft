import { useEffect, useState } from 'react'
import { useForm } from 'react-hook-form'
import toast from 'react-hot-toast'
import { DashHeader } from '../components/dashboard/StatCard'
import Modal, { ConfirmModal } from '../components/Modal'
import StatusBadge from '../components/StatusBadge'
import useApi from '../hooks/useApi'
import { applyServerErrors, errorMessage } from '../services/api'
import { offerService } from '../services/productService'
import { PATTERNS } from '../utils/constants'
import { formatDate, formatINR, isoDate } from '../utils/format'

const num = (v) => (v === '' || v == null ? null : Number(v))

function OfferForm({ offer, onSaved, onCancel }) {
  const {
    register,
    handleSubmit,
    reset,
    watch,
    setError,
    formState: { errors, isSubmitting },
  } = useForm()

  useEffect(() => {
    const today = new Date()
    reset(
      offer
        ? { ...offer, maxDiscountAmount: offer.maxDiscountAmount ?? '', minOrderAmount: offer.minOrderAmount ?? '' }
        : { code: '', title: '', description: '', discountPercent: '', maxDiscountAmount: '', minOrderAmount: '', startDate: isoDate(today), endDate: isoDate(new Date(today.getFullYear(), today.getMonth() + 1, today.getDate())), active: true },
    )
  }, [offer, reset])

  const submit = async (v) => {
    const body = {
      ...v,
      code: v.code.trim().toUpperCase(),
      discountPercent: num(v.discountPercent),
      maxDiscountAmount: num(v.maxDiscountAmount),
      minOrderAmount: num(v.minOrderAmount),
    }
    try {
      const saved = offer ? await offerService.update(offer.id, body) : await offerService.create(body)
      toast.success(offer ? 'Offer updated' : 'Offer created')
      onSaved(saved)
    } catch (err) {
      if (!applyServerErrors(err, setError)) toast.error(errorMessage(err))
    }
  }

  const cls = (n) => `form-control ${errors[n] ? 'is-invalid' : ''}`

  return (
    <form onSubmit={handleSubmit(submit)} noValidate>
      <div className="row g-3">
        <div className="col-md-5">
          <label className="form-label" htmlFor="o-code">Coupon code</label>
          <input id="o-code" className={cls('code')} style={{ textTransform: 'uppercase' }} {...register('code', { required: 'Coupon code is required', pattern: { value: PATTERNS.coupon, message: '3-30 letters, numbers, - or _' } })} />
          <div className="invalid-feedback">{errors.code?.message}</div>
        </div>
        <div className="col-md-7">
          <label className="form-label" htmlFor="o-title">Title</label>
          <input id="o-title" className={cls('title')} {...register('title', { required: 'Title is required', maxLength: { value: 100, message: 'Too long' } })} />
          <div className="invalid-feedback">{errors.title?.message}</div>
        </div>
        <div className="col-12">
          <label className="form-label" htmlFor="o-desc">Description</label>
          <input id="o-desc" className={cls('description')} {...register('description', { maxLength: 500 })} />
        </div>
        <div className="col-md-4">
          <label className="form-label" htmlFor="o-pct">Discount %</label>
          <input id="o-pct" type="number" step="0.01" className={cls('discountPercent')} {...register('discountPercent', { required: 'Discount percent is required', min: { value: 0.01, message: 'Must be greater than 0' }, max: { value: 90, message: 'Cannot exceed 90%' } })} />
          <div className="invalid-feedback">{errors.discountPercent?.message}</div>
        </div>
        <div className="col-md-4">
          <label className="form-label" htmlFor="o-max">Max discount (₹)</label>
          <input id="o-max" type="number" className={cls('maxDiscountAmount')} {...register('maxDiscountAmount', { min: { value: 0, message: 'Cannot be negative' } })} />
          <div className="invalid-feedback">{errors.maxDiscountAmount?.message}</div>
        </div>
        <div className="col-md-4">
          <label className="form-label" htmlFor="o-min">Min. order (₹)</label>
          <input id="o-min" type="number" className={cls('minOrderAmount')} {...register('minOrderAmount', { min: { value: 0, message: 'Cannot be negative' } })} />
          <div className="invalid-feedback">{errors.minOrderAmount?.message}</div>
        </div>
        <div className="col-md-6">
          <label className="form-label" htmlFor="o-start">Starts</label>
          <input id="o-start" type="date" className={cls('startDate')} {...register('startDate', { required: 'Start date is required' })} />
          <div className="invalid-feedback">{errors.startDate?.message}</div>
        </div>
        <div className="col-md-6">
          <label className="form-label" htmlFor="o-end">Ends</label>
          <input id="o-end" type="date" className={cls('endDate')} {...register('endDate', { required: 'End date is required', validate: (v) => v >= watch('startDate') || 'End date must be after the start date' })} />
          <div className="invalid-feedback">{errors.endDate?.message}</div>
        </div>
        <div className="col-12">
          <div className="form-check form-switch">
            <input id="o-active" type="checkbox" className="form-check-input" {...register('active')} />
            <label htmlFor="o-active" className="form-check-label small">Active</label>
          </div>
        </div>
      </div>
      <div className="d-flex justify-content-end gap-2 mt-4">
        <button type="button" className="btn btn-outline-jc btn-sm" onClick={onCancel}>Cancel</button>
        <button className="btn btn-jc btn-sm" disabled={isSubmitting}>{isSubmitting ? 'Saving…' : 'Save offer'}</button>
      </div>
    </form>
  )
}

export default function ManageOffers() {
  const { data, loading, error, reload } = useApi(offerService.all, [])
  const [editing, setEditing] = useState(null)
  const [deleting, setDeleting] = useState(null)

  const remove = async () => {
    try {
      await offerService.remove(deleting.id)
      toast.success('Offer deleted')
      setDeleting(null)
      reload()
    } catch (err) {
      toast.error(errorMessage(err))
    }
  }

  return (
    <>
      <DashHeader title="Offers & Coupons" subtitle="Coupon codes customers apply at checkout. Discounts are validated on the server.">
        <button className="btn btn-jc btn-sm" onClick={() => setEditing('new')}><i className="bi bi-plus-lg me-1" />New offer</button>
      </DashHeader>
      <div className="card-jc">
        <div className="table-responsive">
          <table className="table table-jc">
            <thead><tr><th>Code</th><th>Title</th><th className="text-end">Discount</th><th className="text-end">Max / Min order</th><th>Valid</th><th>Status</th><th /></tr></thead>
            <tbody>
              {loading && !data ? (
                <tr><td colSpan={7} className="text-center text-muted-jc py-5">Loading…</td></tr>
              ) : error ? (
                <tr><td colSpan={7} className="text-center py-5" style={{ color: 'var(--jc-bad)' }}>{error}</td></tr>
              ) : data.length === 0 ? (
                <tr><td colSpan={7} className="text-center text-muted-jc py-5">No offers yet.</td></tr>
              ) : (
                data.map((o) => (
                  <tr key={o.id}>
                    <td><span className="status gold">{o.code}</span></td>
                    <td>{o.title}<div className="small text-muted-jc">{o.description}</div></td>
                    <td className="text-end fw-500">{Number(o.discountPercent)}%</td>
                    <td className="text-end small">{o.maxDiscountAmount ? formatINR(o.maxDiscountAmount) : 'No cap'}<br /><span className="text-muted-jc">{o.minOrderAmount ? `min ${formatINR(o.minOrderAmount)}` : 'No minimum'}</span></td>
                    <td className="small">{formatDate(o.startDate)} – {formatDate(o.endDate)}</td>
                    <td>
                      {o.currentlyValid ? <StatusBadge status="ACTIVE" label="Live" /> : <StatusBadge status="INACTIVE" label={o.active ? 'Scheduled / expired' : 'Off'} />}
                    </td>
                    <td className="text-end text-nowrap">
                      <button className="btn btn-outline-jc btn-sm btn-icon me-1" aria-label="Edit" onClick={() => setEditing(o)}><i className="bi bi-pencil" /></button>
                      <button className="btn btn-outline-jc btn-sm btn-icon" aria-label="Delete" onClick={() => setDeleting(o)}><i className="bi bi-trash" /></button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
      <Modal show={Boolean(editing)} title={editing === 'new' ? 'New offer' : 'Edit offer'} size="lg" onClose={() => setEditing(null)}>
        {editing && (
          <OfferForm
            offer={editing === 'new' ? null : editing}
            onCancel={() => setEditing(null)}
            onSaved={() => {
              setEditing(null)
              reload()
            }}
          />
        )}
      </Modal>
      <ConfirmModal show={Boolean(deleting)} title="Delete offer?" message={`Coupon ${deleting?.code} will stop working immediately.`} confirmText="Delete" onConfirm={remove} onClose={() => setDeleting(null)} />
    </>
  )
}
