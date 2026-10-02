import { useForm } from 'react-hook-form'
import { applyServerErrors, errorMessage } from '../services/api'
import { INDIAN_STATES, PATTERNS } from '../utils/constants'
import toast from 'react-hot-toast'

const EMPTY = { fullName: '', phone: '', line1: '', line2: '', city: '', state: '', pincode: '', country: 'India', defaultAddress: false }

/** Add / edit a delivery address. `onSubmit(values)` must return a promise. */
export default function AddressForm({ initial, onSubmit, onCancel, submitText = 'Save address' }) {
  const {
    register,
    handleSubmit,
    setError,
    formState: { errors, isSubmitting },
  } = useForm({ defaultValues: { ...EMPTY, ...initial } })

  const submit = async (values) => {
    try {
      await onSubmit(values)
    } catch (err) {
      if (!applyServerErrors(err, setError)) toast.error(errorMessage(err))
    }
  }

  const field = (name) => `form-control ${errors[name] ? 'is-invalid' : ''}`

  return (
    <form onSubmit={handleSubmit(submit)} noValidate>
      <div className="row g-3">
        <div className="col-md-6">
          <label className="form-label" htmlFor="a-name">Full name</label>
          <input id="a-name" className={field('fullName')} {...register('fullName', { required: 'Full name is required', maxLength: { value: 100, message: 'Too long' } })} />
          <div className="invalid-feedback">{errors.fullName?.message}</div>
        </div>
        <div className="col-md-6">
          <label className="form-label" htmlFor="a-phone">Mobile number</label>
          <input id="a-phone" inputMode="numeric" maxLength={10} className={field('phone')} {...register('phone', { required: 'Phone number is required', pattern: { value: PATTERNS.phone, message: 'Enter a valid 10-digit mobile number' } })} />
          <div className="invalid-feedback">{errors.phone?.message}</div>
        </div>
        <div className="col-12">
          <label className="form-label" htmlFor="a-line1">Address line 1</label>
          <input id="a-line1" placeholder="House no., building, street" className={field('line1')} {...register('line1', { required: 'Address line 1 is required', maxLength: { value: 255, message: 'Too long' } })} />
          <div className="invalid-feedback">{errors.line1?.message}</div>
        </div>
        <div className="col-12">
          <label className="form-label" htmlFor="a-line2">Address line 2 <span className="text-muted-jc">(optional)</span></label>
          <input id="a-line2" placeholder="Area, landmark" className={field('line2')} {...register('line2', { maxLength: { value: 255, message: 'Too long' } })} />
        </div>
        <div className="col-md-4">
          <label className="form-label" htmlFor="a-city">City</label>
          <input id="a-city" className={field('city')} {...register('city', { required: 'City is required' })} />
          <div className="invalid-feedback">{errors.city?.message}</div>
        </div>
        <div className="col-md-4">
          <label className="form-label" htmlFor="a-state">State</label>
          <select id="a-state" className={`form-select ${errors.state ? 'is-invalid' : ''}`} {...register('state', { required: 'State is required' })}>
            <option value="">Select state</option>
            {INDIAN_STATES.map((s) => <option key={s}>{s}</option>)}
          </select>
          <div className="invalid-feedback">{errors.state?.message}</div>
        </div>
        <div className="col-md-4">
          <label className="form-label" htmlFor="a-pin">Pincode</label>
          <input id="a-pin" inputMode="numeric" maxLength={6} className={field('pincode')} {...register('pincode', { required: 'Pincode is required', pattern: { value: PATTERNS.pincode, message: 'Pincode must be 6 digits' } })} />
          <div className="invalid-feedback">{errors.pincode?.message}</div>
        </div>
        <div className="col-12">
          <div className="form-check">
            <input id="a-default" type="checkbox" className="form-check-input" {...register('defaultAddress')} />
            <label htmlFor="a-default" className="form-check-label small">Make this my default address</label>
          </div>
        </div>
      </div>
      <div className="d-flex gap-2 justify-content-end mt-4">
        {onCancel && <button type="button" className="btn btn-outline-jc btn-sm" onClick={onCancel}>Cancel</button>}
        <button className="btn btn-jc btn-sm" disabled={isSubmitting}>{isSubmitting ? 'Saving…' : submitText}</button>
      </div>
    </form>
  )
}
