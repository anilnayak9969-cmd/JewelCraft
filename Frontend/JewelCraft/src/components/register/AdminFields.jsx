import { ADMIN_DESIGNATIONS, ADMIN_OFFICES } from '../../utils/constants'
import FormSection from './FormSection'

/** Background details for an administrator application. */
export default function AdminFields({ register, errors, start }) {
  const cls = (name, base = 'form-control') => `${base} ${errors[name] ? 'is-invalid' : ''}`
  return (
    <FormSection number={start} title="Professional background">
      <div className="row g-3">
        <div className="col-sm-6">
          <label className="form-label" htmlFor="designation">Designation</label>
          <select id="designation" className={cls('designation', 'form-select')} {...register('designation', { required: 'Designation is required' })}>
            <option value="">Select designation</option>
            {ADMIN_DESIGNATIONS.map((d) => <option key={d}>{d}</option>)}
          </select>
          <div className="invalid-feedback">{errors.designation?.message}</div>
        </div>
        <div className="col-sm-6">
          <label className="form-label" htmlFor="officeLocation">Office location</label>
          <select id="officeLocation" className={cls('officeLocation', 'form-select')} {...register('officeLocation', { required: 'Office location is required' })}>
            <option value="">Select office</option>
            {ADMIN_OFFICES.map((o) => <option key={o}>{o}</option>)}
          </select>
          <div className="invalid-feedback">{errors.officeLocation?.message}</div>
        </div>
        <div className="col-sm-6">
          <label className="form-label" htmlFor="experienceYears">Management experience (years)</label>
          <input id="experienceYears" type="number" min="2" max="50" className={cls('experienceYears')} {...register('experienceYears', { required: 'Experience is required', min: { value: 2, message: 'Administrators need at least 2 years of experience' }, max: { value: 50, message: 'Looks too large' } })} />
          <div className="invalid-feedback">{errors.experienceYears?.message}</div>
        </div>
        <div className="col-sm-6">
          <label className="form-label" htmlFor="qualification">Highest qualification</label>
          <input id="qualification" placeholder="e.g. MBA, B.Com, B.Tech" className={cls('qualification')} {...register('qualification', { required: 'Qualification is required', maxLength: { value: 100, message: 'At most 100 characters' } })} />
          <div className="invalid-feedback">{errors.qualification?.message}</div>
        </div>
        <div className="col-12">
          <label className="form-label" htmlFor="previousEmployer">Previous employer <span className="text-muted-jc">(optional)</span></label>
          <input id="previousEmployer" className={cls('previousEmployer')} {...register('previousEmployer', { maxLength: { value: 100, message: 'At most 100 characters' } })} />
          <div className="invalid-feedback">{errors.previousEmployer?.message}</div>
        </div>
      </div>
    </FormSection>
  )
}
