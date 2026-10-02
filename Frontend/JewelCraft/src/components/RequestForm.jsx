import { useForm } from 'react-hook-form'
import toast from 'react-hot-toast'
import { applyServerErrors, errorMessage } from '../services/api'
import { requestService } from '../services/userService'
import { REQUEST_TYPES } from '../utils/constants'
import { humanize } from '../utils/format'

/** Customer support request (return, exchange, customisation, query, complaint). */
export default function RequestForm({ orderId, orders = [], onDone, onCancel }) {
  const {
    register,
    handleSubmit,
    setError,
    formState: { errors, isSubmitting },
  } = useForm({ defaultValues: { type: 'QUERY', orderId: orderId ?? '', subject: '', message: '' } })

  const submit = async (values) => {
    try {
      const created = await requestService.create({ ...values, orderId: values.orderId ? Number(values.orderId) : null })
      toast.success('Request sent – our team will get back to you soon')
      onDone?.(created)
    } catch (err) {
      if (!applyServerErrors(err, setError)) toast.error(errorMessage(err))
    }
  }

  return (
    <form onSubmit={handleSubmit(submit)} noValidate>
      <div className="row g-3">
        <div className="col-md-6">
          <label className="form-label" htmlFor="rq-type">Request type</label>
          <select id="rq-type" className="form-select" {...register('type', { required: true })}>
            {REQUEST_TYPES.map((t) => <option key={t} value={t}>{humanize(t)}</option>)}
          </select>
        </div>
        {orderId == null && (
          <div className="col-md-6">
            <label className="form-label" htmlFor="rq-order">Related order <span className="text-muted-jc">(optional)</span></label>
            <select id="rq-order" className="form-select" {...register('orderId')}>
              <option value="">None</option>
              {orders.map((o) => <option key={o.id} value={o.id}>{o.orderNumber}</option>)}
            </select>
          </div>
        )}
        <div className="col-12">
          <label className="form-label" htmlFor="rq-subject">Subject</label>
          <input id="rq-subject" className={`form-control ${errors.subject ? 'is-invalid' : ''}`} {...register('subject', { required: 'Subject is required', maxLength: { value: 150, message: 'At most 150 characters' } })} />
          <div className="invalid-feedback">{errors.subject?.message}</div>
        </div>
        <div className="col-12">
          <label className="form-label" htmlFor="rq-msg">Message</label>
          <textarea
            id="rq-msg"
            rows={4}
            className={`form-control ${errors.message ? 'is-invalid' : ''}`}
            {...register('message', {
              required: 'Message is required',
              minLength: { value: 10, message: 'Message must be 10-2000 characters' },
              maxLength: { value: 2000, message: 'Message must be 10-2000 characters' },
            })}
          />
          <div className="invalid-feedback">{errors.message?.message}</div>
        </div>
      </div>
      <div className="d-flex justify-content-end gap-2 mt-4">
        {onCancel && <button type="button" className="btn btn-outline-jc btn-sm" onClick={onCancel}>Cancel</button>}
        <button className="btn btn-jc btn-sm" disabled={isSubmitting}>{isSubmitting ? 'Sending…' : 'Send request'}</button>
      </div>
    </form>
  )
}
