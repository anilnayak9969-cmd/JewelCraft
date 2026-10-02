import { DEPARTMENTS, STORE_BRANCHES } from '../../utils/constants'
import FormSection from './FormSection'

/** Employment details for store staff. */
export default function StaffFields({ register, errors, start }) {
  const cls = (name, base = 'form-control') => `${base} ${errors[name] ? 'is-invalid' : ''}`
  return (
    <FormSection number={start} title="Employment details">
      <label className="form-label d-block">Department</label>
      <div className={`choice-grid mb-1 ${errors.department ? 'is-invalid' : ''}`}>
        {DEPARTMENTS.map((d) => (
          <span key={d.value}>
            <input id={`d-${d.value}`} type="radio" value={d.value} {...register('department', { required: 'Please choose a department' })} />
            <label htmlFor={`d-${d.value}`}>
              {d.label}
              <small>{d.hint}</small>
            </label>
          </span>
        ))}
      </div>
      {errors.department && <div className="small" style={{ color: 'var(--bs-form-invalid-color)' }}>{errors.department.message}</div>}

      <div className="row g-3 mt-1">
        <div className="col-sm-6">
          <label className="form-label" htmlFor="designation">Designation</label>
          <input id="designation" placeholder="e.g. Jewellery Consultant" className={cls('designation')} {...register('designation', { required: 'Designation is required', maxLength: { value: 60, message: 'At most 60 characters' } })} />
          <div className="invalid-feedback">{errors.designation?.message}</div>
        </div>
        <div className="col-sm-6">
          <label className="form-label" htmlFor="storeBranch">Store branch</label>
          <select id="storeBranch" className={cls('storeBranch', 'form-select')} {...register('storeBranch', { required: 'Store branch is required' })}>
            <option value="">Select branch</option>
            {STORE_BRANCHES.map((b) => <option key={b}>{b}</option>)}
          </select>
          <div className="invalid-feedback">{errors.storeBranch?.message}</div>
        </div>
        <div className="col-sm-6">
          <label className="form-label" htmlFor="experienceYears">Experience (years)</label>
          <input id="experienceYears" type="number" min="0" max="50" className={cls('experienceYears')} {...register('experienceYears', { required: 'Experience is required', min: { value: 0, message: 'Cannot be negative' }, max: { value: 50, message: 'Looks too large' } })} />
          <div className="invalid-feedback">{errors.experienceYears?.message}</div>
        </div>
        <div className="col-sm-6">
          <label className="form-label" htmlFor="qualification">Qualification <span className="text-muted-jc">(optional)</span></label>
          <input id="qualification" placeholder="e.g. B.Com, GIA Diamond course" className={cls('qualification')} {...register('qualification', { maxLength: { value: 100, message: 'At most 100 characters' } })} />
          <div className="invalid-feedback">{errors.qualification?.message}</div>
        </div>
      </div>
    </FormSection>
  )
}
