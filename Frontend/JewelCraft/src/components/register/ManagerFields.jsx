import { STORE_BRANCHES } from '../../utils/constants'
import FormSection from './FormSection'

/** Background details for a store manager application. */
export default function ManagerFields({ register, errors, start }) {
  const cls = (name, base = 'form-control') => `${base} ${errors[name] ? 'is-invalid' : ''}`
  return (
    <FormSection number={start} title="Professional background">
      <div className="row g-3">
        <div className="col-sm-6">
          <label className="form-label" htmlFor="storeBranch">Boutique you will manage</label>
          <select id="storeBranch" className={cls('storeBranch', 'form-select')} {...register('storeBranch', { required: 'Store branch is required' })}>
            <option value="">Select branch</option>
            {STORE_BRANCHES.map((b) => <option key={b}>{b}</option>)}
          </select>
          <div className="invalid-feedback">{errors.storeBranch?.message}</div>
        </div>
        <div className="col-sm-6">
          <label className="form-label" htmlFor="experienceYears">Retail experience (years)</label>
          <input id="experienceYears" type="number" min="1" max="50" className={cls('experienceYears')} {...register('experienceYears', { required: 'Experience is required', min: { value: 1, message: 'Store managers need at least 1 year of experience' }, max: { value: 50, message: 'Looks too large' } })} />
          <div className="invalid-feedback">{errors.experienceYears?.message}</div>
        </div>
        <div className="col-sm-6">
          <label className="form-label" htmlFor="qualification">Highest qualification</label>
          <input id="qualification" placeholder="e.g. MBA, Retail Management" className={cls('qualification')} {...register('qualification', { required: 'Qualification is required', maxLength: { value: 100, message: 'At most 100 characters' } })} />
          <div className="invalid-feedback">{errors.qualification?.message}</div>
        </div>
        <div className="col-sm-6">
          <label className="form-label" htmlFor="previousEmployer">Previous employer <span className="text-muted-jc">(optional)</span></label>
          <input id="previousEmployer" className={cls('previousEmployer')} {...register('previousEmployer', { maxLength: { value: 100, message: 'At most 100 characters' } })} />
          <div className="invalid-feedback">{errors.previousEmployer?.message}</div>
        </div>
      </div>
    </FormSection>
  )
}
