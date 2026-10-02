import { GENDERS, METAL_TONES, METAL_TYPES } from '../../utils/constants'
import { isoDate, metalLabel } from '../../utils/format'
import FormSection from './FormSection'

/** Optional extras for customers – used for birthday / anniversary offers and recommendations. */
export default function CustomerFields({ register, errors, start }) {
  const today = isoDate()
  return (
    <FormSection number={start} title="Your preferences (optional)">
      <label className="form-label d-block">Gender</label>
      <div className="pill-group mb-3">
        {GENDERS.map((g) => (
          <span key={g.value}>
            <input id={`g-${g.value}`} type="radio" value={g.value} {...register('gender')} />
            <label htmlFor={`g-${g.value}`}>{g.label}</label>
          </span>
        ))}
      </div>

      <div className="row g-3">
        <div className="col-sm-6">
          <label className="form-label" htmlFor="dateOfBirth">Birthday</label>
          <input id="dateOfBirth" type="date" max={today} className={`form-control ${errors.dateOfBirth ? 'is-invalid' : ''}`} {...register('dateOfBirth', { validate: (v) => !v || v < today || 'Birthday must be in the past' })} />
          <div className="invalid-feedback">{errors.dateOfBirth?.message}</div>
        </div>
        <div className="col-sm-6">
          <label className="form-label" htmlFor="anniversaryDate">Anniversary</label>
          <input id="anniversaryDate" type="date" max={today} className={`form-control ${errors.anniversaryDate ? 'is-invalid' : ''}`} {...register('anniversaryDate', { validate: (v) => !v || v <= today || 'Anniversary cannot be in the future' })} />
          <div className="invalid-feedback">{errors.anniversaryDate?.message}</div>
        </div>
      </div>

      <label className="form-label d-block mt-3">Favourite metal</label>
      <div className="pill-group">
        {METAL_TYPES.map((m) => (
          <span key={m}>
            <input id={`m-${m}`} type="radio" value={m} {...register('preferredMetal')} />
            <label htmlFor={`m-${m}`}>
              <span className="metal-swatch me-0" style={{ background: METAL_TONES[m].swatch }} />
              {metalLabel(m)}
            </label>
          </span>
        ))}
      </div>

      <div className="form-check mt-3">
        <input id="marketingOptIn" type="checkbox" className="form-check-input" {...register('marketingOptIn')} />
        <label htmlFor="marketingOptIn" className="form-check-label small">
          Send me birthday &amp; anniversary privileges and new-collection previews
        </label>
      </div>
    </FormSection>
  )
}
