import { useEffect, useRef, useState } from 'react'
import toast from 'react-hot-toast'
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom'
import OtpInput from '../components/OtpInput'
import useAuth from '../hooks/useAuth'
import useCountdown, { mmss } from '../hooks/useCountdown'
import { errorMessage } from '../services/api'
import authService from '../services/authService'
import { ROLE_HOME } from '../utils/constants'

/** an*********69@gmail.com */
function maskEmail(email = '') {
  const [name, domain] = email.split('@')
  if (!domain || name.length <= 2) return email
  const keep = name.length > 5 ? 2 : 0
  return `${name.slice(0, 2)}${'•'.repeat(name.length - 2 - keep)}${keep ? name.slice(-keep) : ''}@${domain}`
}

/**
 * Step 2 of sign-up: enter the 6-digit code from the email.
 * Customers are signed in straight away; staff-type accounts see "awaiting approval".
 */
export default function VerifyEmail() {
  const location = useLocation()
  const navigate = useNavigate()
  const { startSession } = useAuth()
  const state = location.state || {}
  const email = state.email || new URLSearchParams(location.search).get('email') || ''

  const [code, setCode] = useState('')
  const [error, setError] = useState(false)
  const [busy, setBusy] = useState(false)
  const [approvalPending, setApprovalPending] = useState(false)
  const [expiresLeft, restartExpiry] = useCountdown(state.expiresIn ?? 600)
  const [resendLeft, restartResend] = useCountdown(state.autoResend ? 0 : state.resendIn ?? 60)
  const autoSent = useRef(false)

  const resend = async () => {
    try {
      const res = await authService.resendOtp(email)
      toast.success(res.message)
      restartExpiry(res.otpExpiresInSeconds)
      restartResend(res.resendAvailableInSeconds)
      setCode('')
      setError(false)
    } catch (err) {
      toast.error(errorMessage(err))
    }
  }

  // Coming from sign-in with an unverified account: send a fresh code once.
  useEffect(() => {
    if (state.autoResend && email && !autoSent.current) {
      autoSent.current = true
      resend()
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  if (!email) return <Navigate to="/register" replace />

  const verify = async (otp = code) => {
    if (!/^\d{6}$/.test(otp)) {
      setError(true)
      return
    }
    setBusy(true)
    setError(false)
    try {
      const res = await authService.verifyEmail(email, otp)
      if (res.requiresApproval) {
        setApprovalPending(true)
      } else {
        const user = startSession(res.auth)
        toast.success(res.message)
        navigate(user.role === 'CUSTOMER' ? state.from || '/' : ROLE_HOME[user.role], { replace: true })
      }
    } catch (err) {
      setError(true)
      setCode('')
      toast.error(errorMessage(err))
    } finally {
      setBusy(false)
    }
  }

  if (approvalPending) {
    return (
      <div className="container text-center" style={{ padding: '5rem 1rem', maxWidth: 620 }}>
        <div className="verify-icon"><i className="bi bi-patch-check" /></div>
        <div className="eyebrow">Email verified</div>
        <h1 className="mt-2" style={{ fontSize: '2.6rem' }}>Application received</h1>
        <p className="text-soft">
          Thank you. Our admin team is now reviewing your details. You’ll receive an email at <b>{maskEmail(email)}</b> as soon as
          your account is approved – then you can sign in to your dashboard.
        </p>
        <div className="row g-2 my-4 text-start small">
          {[
            ['check2-circle', 'Application submitted', true],
            ['envelope-check', 'Email verified', true],
            ['hourglass-split', 'Admin review', false],
            ['box-arrow-in-right', 'Sign in', false],
          ].map(([icon, label, done]) => (
            <div key={label} className="col-6 col-md-3">
              <div className="panel text-center p-3" style={{ opacity: done ? 1 : 0.6 }}>
                <i className={`bi bi-${icon} ${done ? 'text-gold' : ''}`} style={{ fontSize: '1.3rem' }} />
                <div className="mt-1">{label}</div>
              </div>
            </div>
          ))}
        </div>
        <Link to="/" className="btn btn-jc">Back to the store</Link>
      </div>
    )
  }

  return (
    <div className="container" style={{ padding: '4.5rem 1rem', maxWidth: 520 }}>
      <div className="text-center">
        <div className="verify-icon"><i className="bi bi-envelope-paper-heart" /></div>
        <div className="eyebrow">Verify your email</div>
        <h1 className="mt-2 mb-2" style={{ fontSize: '2.5rem' }}>Enter your code</h1>
        <p className="text-soft mb-0">
          We sent a 6-digit code to <b>{maskEmail(email)}</b>.<br />It may take a minute to arrive – check spam too.
        </p>
      </div>

      <form
        onSubmit={(e) => {
          e.preventDefault()
          verify()
        }}
      >
        <OtpInput value={code} onChange={setCode} onComplete={verify} error={error} disabled={busy} />
        <div className="text-center small mb-4" style={{ color: expiresLeft > 0 ? 'var(--jc-muted)' : 'var(--jc-bad)' }}>
          {expiresLeft > 0 ? <>Code expires in <b>{mmss(expiresLeft)}</b></> : 'This code has expired – request a new one below.'}
        </div>
        <button className="btn btn-jc w-100" disabled={busy || code.length !== 6}>
          {busy ? 'Verifying…' : 'Verify email'}
        </button>
      </form>

      <div className="text-center mt-4 small">
        Didn’t get it?{' '}
        {resendLeft > 0 ? (
          <span className="text-muted-jc">Resend in {mmss(resendLeft)}</span>
        ) : (
          <button type="button" className="btn-link-jc" style={{ fontSize: '0.7rem' }} onClick={resend}>Resend code</button>
        )}
      </div>
      <div className="notice mt-4">
        <i className="bi bi-shield-lock" />
        <div>Never share this code. JewelCraft staff will never ask you for it.</div>
      </div>
      <p className="text-center small mt-4">
        Wrong email? <Link to="/register" className="text-gold">Start again</Link>
      </p>
    </div>
  )
}
