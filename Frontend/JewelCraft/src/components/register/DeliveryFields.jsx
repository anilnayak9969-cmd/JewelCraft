import { PATTERNS, VEHICLE_TYPES } from '../../utils/constants'
import FormSection from './FormSection'

/** Vehicle, licence and service-area details for delivery partners. */
export default function DeliveryFields({ register, errors, watch, start }) {
  const cls = (name) => `form-control ${errors[name] ? 'is-invalid' : ''}`
  const vehicle = watch('vehicleType')
  const motor = vehicle && vehicle !== 'BICYCLE'
  const upper = { setValueAs: (v) => (v ? v.toUpperCase().trim() : v) }

  return (
    <>
      <FormSection number={start} title="Vehicle & licence">
        <label className="form-label d-block">Vehicle</label>
        <div className={`choice-grid five mb-1 ${errors.vehicleType ? 'is-invalid' : ''}`}>
          {VEHICLE_TYPES.map((v) => (
            <span key={v.value}>
              <input id={`v-${v.value}`} type="radio" value={v.value} {...register('vehicleType', { required: 'Please choose your vehicle' })} />
              <label htmlFor={`v-${v.value}`}>
                <i className={`bi bi-${v.icon}`} />
                {v.label}
              </label>
            </span>
          ))}
        </div>
        {errors.vehicleType && <div className="small" style={{ color: 'var(--bs-form-invalid-color)' }}>{errors.vehicleType.message}</div>}

        {motor ? (
          <div className="row g-3 mt-1">
            <div className="col-sm-6">
              <label className="form-label" htmlFor="vehicleNumber">Vehicle number</label>
              <input id="vehicleNumber" placeholder="MH 12 AB 1234" style={{ textTransform: 'uppercase' }} className={cls('vehicleNumber')}
                {...register('vehicleNumber', { ...upper, required: 'Vehicle number is required', pattern: { value: PATTERNS.vehicle, message: 'Enter a valid number, e.g. MH 12 AB 1234' } })} />
              <div className="invalid-feedback">{errors.vehicleNumber?.message}</div>
            </div>
            <div className="col-sm-6">
              <label className="form-label" htmlFor="drivingLicenseNumber">Driving licence number</label>
              <input id="drivingLicenseNumber" placeholder="MH12 20110012345" style={{ textTransform: 'uppercase' }} className={cls('drivingLicenseNumber')}
                {...register('drivingLicenseNumber', { ...upper, required: 'Driving licence is required', pattern: { value: PATTERNS.licence, message: 'Enter a valid licence, e.g. MH12 20110012345' } })} />
              <div className="invalid-feedback">{errors.drivingLicenseNumber?.message}</div>
            </div>
          </div>
        ) : vehicle === 'BICYCLE' ? (
          <p className="small text-muted-jc mt-2 mb-0">No registration or licence needed for bicycle deliveries.</p>
        ) : null}
      </FormSection>

      <FormSection number={start + 1} title="Service area & emergency contact">
        <div className="row g-3">
          <div className="col-sm-6">
            <label className="form-label" htmlFor="serviceCity">City</label>
            <input id="serviceCity" className={cls('serviceCity')} {...register('serviceCity', { required: 'Service city is required', maxLength: { value: 60, message: 'At most 60 characters' } })} />
            <div className="invalid-feedback">{errors.serviceCity?.message}</div>
          </div>
          <div className="col-sm-6">
            <label className="form-label" htmlFor="servicePincode">Base pincode</label>
            <input id="servicePincode" inputMode="numeric" maxLength={6} className={cls('servicePincode')} {...register('servicePincode', { required: 'Service pincode is required', pattern: { value: PATTERNS.pincode, message: 'Pincode must be 6 digits' } })} />
            <div className="invalid-feedback">{errors.servicePincode?.message}</div>
          </div>
          <div className="col-sm-6">
            <label className="form-label" htmlFor="emergencyContactName">Emergency contact <span className="text-muted-jc">(optional)</span></label>
            <input id="emergencyContactName" className={cls('emergencyContactName')} {...register('emergencyContactName', { maxLength: { value: 100, message: 'At most 100 characters' } })} />
          </div>
          <div className="col-sm-6">
            <label className="form-label" htmlFor="emergencyContactPhone">Their mobile</label>
            <input id="emergencyContactPhone" inputMode="numeric" maxLength={10} className={cls('emergencyContactPhone')} {...register('emergencyContactPhone', { validate: (v) => !v || PATTERNS.phone.test(v) || 'Enter a valid 10-digit mobile number' })} />
            <div className="invalid-feedback">{errors.emergencyContactPhone?.message}</div>
          </div>
        </div>
      </FormSection>
    </>
  )
}
