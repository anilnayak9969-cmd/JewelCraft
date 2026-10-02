import { useState } from 'react'
import { PATTERNS } from '../../utils/constants'
import FormSection from './FormSection'

/** Name, email, mobile and password – the part every sign-up form shares. */
export default function AccountFields({ register, errors, watch, start = 1 }) {
  const [show, setShow] = useState(false)
  const cls = (name) => `form-control ${errors[name] ? 'is-invalid' : ''}`

  return (
    <>
      <FormSection number={start} title="Your details">
        <div className="row g-3">
          <div className="col-sm-6">
            <label className="form-label" htmlFor="firstName">First name</label>
            <input id="firstName" autoComplete="given-name" className={cls('firstName')} {...register('firstName', { required: 'First name is required', maxLength: { value: 50, message: 'At most 50 characters' } })} />
            <div className="invalid-feedback">{errors.firstName?.message}</div>
          </div>
          <div className="col-sm-6">
            <label className="form-label" htmlFor="lastName">Last name</label>
            <input id="lastName" autoComplete="family-name" className={cls('lastName')} {...register('lastName', { maxLength: { value: 50, message: 'At most 50 characters' } })} />
            <div className="invalid-feedback">{errors.lastName?.message}</div>
          </div>
          <div className="col-12">
            <label className="form-label" htmlFor="email">Email address</label>
            <input id="email" type="email" autoComplete="email" className={cls('email')} {...register('email', { required: 'Email is required', pattern: { value: PATTERNS.email, message: 'Enter a valid email address' } })} />
            <div className="invalid-feedback">{errors.email?.message}</div>
            <div className="form-text small">We’ll send a 6-digit code here to verify it.</div>
          </div>
          <div className="col-12">
            <label className="form-label" htmlFor="phone">Mobile number</label>
            <div className="input-group has-validation">
              <span className="input-group-text">+91</span>
              <input id="phone" inputMode="numeric" maxLength={10} autoComplete="tel-national" className={cls('phone')} {...register('phone', { required: 'Phone number is required', pattern: { value: PATTERNS.phone, message: 'Enter a valid 10-digit mobile number' } })} />
              <div className="invalid-feedback">{errors.phone?.message}</div>
            </div>
          </div>
        </div>
      </FormSection>

      <FormSection number={start + 1} title="Account security">
        <div className="row g-3">
          <div className="col-sm-6">
            <label className="form-label" htmlFor="password">Password</label>
            <div className="input-group has-validation">
              <input
                id="password"
                type={show ? 'text' : 'password'}
                autoComplete="new-password"
                className={cls('password')}
                {...register('password', {
                  required: 'Password is required',
                  minLength: { value: 8, message: 'Password must be 8-64 characters' },
                  maxLength: { value: 64, message: 'Password must be 8-64 characters' },
                  pattern: { value: PATTERNS.password, message: 'Use both letters and numbers' },
                })}
              />
              <button type="button" className="input-group-text" onClick={() => setShow((s) => !s)} aria-label={show ? 'Hide password' : 'Show password'}>
                <i className={`bi ${show ? 'bi-eye-slash' : 'bi-eye'}`} />
              </button>
              <div className="invalid-feedback">{errors.password?.message}</div>
            </div>
          </div>
          <div className="col-sm-6">
            <label className="form-label" htmlFor="confirmPassword">Confirm password</label>
            <input
              id="confirmPassword"
              type={show ? 'text' : 'password'}
              autoComplete="new-password"
              className={cls('confirmPassword')}
              {...register('confirmPassword', {
                required: 'Please confirm your password',
                validate: (v) => v === watch('password') || 'Passwords do not match',
              })}
            />
            <div className="invalid-feedback">{errors.confirmPassword?.message}</div>
          </div>
        </div>
        <div className="small text-muted-jc mt-2">At least 8 characters, with letters and numbers. Stored encrypted – never readable by anyone.</div>
      </FormSection>
    </>
  )
}
