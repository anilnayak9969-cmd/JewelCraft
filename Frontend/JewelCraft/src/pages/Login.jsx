import { useState } from 'react'
import { useForm } from 'react-hook-form'
import toast from 'react-hot-toast'
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom'
import AuthArt from '../components/AuthArt'
import useAuth from '../hooks/useAuth'
import { errorMessage } from '../services/api'
import { PATTERNS, ROLE_HOME, SIGNUP_ROLES } from '../utils/constants'

// Accounts created by the backend's DataInitializer (app.seed.demo-data=true).
// Shown only in development, or when VITE_SHOW_DEMO_LOGINS=true for a portfolio demo.
const DEMO_ACCOUNTS = [
  { label: 'Customer', icon: 'gem', hint: 'Shop & track orders', email: 'customer@jewelcraft.com', password: 'Customer@123' },
  { label: 'Admin', icon: 'shield-lock', hint: 'Full control', email: 'admin@jewelcraft.com', password: 'Admin@123' },
  { label: 'Store Manager', icon: 'shop', hint: 'Prices & operations', email: 'manager@jewelcraft.com', password: 'Manager@123' },
  { label: 'Staff', icon: 'person-badge', hint: 'Products & orders', email: 'staff@jewelcraft.com', password: 'Staff@123' },
  { label: 'Delivery', icon: 'truck', hint: 'Deliver orders', email: 'delivery@jewelcraft.com', password: 'Delivery@123' },
]
const SHOW_DEMO = import.meta.env.DEV || import.meta.env.VITE_SHOW_DEMO_LOGINS === 'true'

/** Customers go back to where they were; staff roles go to their dashboard. */
function landingPath(user, from) {
  const home = ROLE_HOME[user.role] || '/'
  if (!from || from === '/login' || from === '/register') return home
  if (user.role === 'CUSTOMER') return from
  return from.startsWith(home) ? from : home
}

export default function Login() {
  const { login, user } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const from = location.state?.from
  const [showPassword, setShowPassword] = useState(false)
  // PENDING_APPROVAL / APPLICATION_REJECTED are shown as a notice above the form, not just a toast.
  const [accountNotice, setAccountNotice] = useState(null)
  const {
    register,
    handleSubmit,
    setValue,
    formState: { errors, isSubmitting },
  } = useForm({ defaultValues: { email: '', password: '' } })

  if (user) return <Navigate to={landingPath(user, from)} replace />

  const submit = async (values) => {
    setAccountNotice(null)
    try {
      const signedIn = await login(values)
      toast.success(`Welcome back, ${signedIn.firstName}`)
      navigate(landingPath(signedIn, from), { replace: true })
    } catch (err) {
      const data = err.response?.data
      if (data?.code === 'EMAIL_NOT_VERIFIED') {
        toast('Please verify your email first – we’re sending you a new code.', { icon: '✉️' })
        navigate('/verify-email', { state: { email: values.email.trim().toLowerCase(), autoResend: true, from } })
      } else if (data?.code === 'PENDING_APPROVAL' || data?.code === 'APPLICATION_REJECTED') {
        setAccountNotice({ tone: data.code === 'PENDING_APPROVAL' ? 'warn' : 'bad', text: data.message })
      } else {
        toast.error(errorMessage(err, 'Invalid email or password'))
      }
    }
  }

  const fillDemo = (email, password) => {
    setValue('email', email, { shouldValidate: true })
    setValue('password', password, { shouldValidate: true })
  }

  return (
    <div className="auth-wrap">
      <AuthArt
        quote="“Jewellery is the most transformative thing you can wear.”"
        caption="Welcome back"
        perks={[['geo-alt', 'Track your orders live'], ['heart', 'Your wishlist, saved'], ['shield-check', 'Secure sign-in']]}
      />
      <div className="auth-form">
        <div className="inner">
          <div className="eyebrow">My account</div>
          <h1 className="mt-2 mb-1" style={{ fontSize: '2.6rem' }}>Sign in</h1>
          <p className="text-soft mb-4">Customers, staff, managers and delivery partners all sign in here.</p>

          {accountNotice && (
            <div className={`notice ${accountNotice.tone} mb-4`} role="alert">
              <i className={`bi bi-${accountNotice.tone === 'warn' ? 'hourglass-split' : 'x-octagon'}`} />
              <div>{accountNotice.text}</div>
            </div>
          )}

          <form onSubmit={handleSubmit(submit)} noValidate>
            <div className="mb-3">
              <label className="form-label" htmlFor="email">Email address</label>
              <input
                id="email"
                type="email"
                autoComplete="email"
                className={`form-control ${errors.email ? 'is-invalid' : ''}`}
                {...register('email', {
                  required: 'Email is required',
                  pattern: { value: PATTERNS.email, message: 'Enter a valid email address' },
                })}
              />
              <div className="invalid-feedback">{errors.email?.message}</div>
            </div>
            <div className="mb-4">
              <label className="form-label" htmlFor="password">Password</label>
              <div className="input-group has-validation">
                <input
                  id="password"
                  type={showPassword ? 'text' : 'password'}
                  autoComplete="current-password"
                  className={`form-control ${errors.password ? 'is-invalid' : ''}`}
                  {...register('password', { required: 'Password is required' })}
                />
                <button type="button" className="input-group-text" onClick={() => setShowPassword((s) => !s)} aria-label={showPassword ? 'Hide password' : 'Show password'}>
                  <i className={`bi ${showPassword ? 'bi-eye-slash' : 'bi-eye'}`} />
                </button>
                <div className="invalid-feedback">{errors.password?.message}</div>
              </div>
            </div>
            <button className="btn btn-jc w-100" disabled={isSubmitting}>{isSubmitting ? 'Signing in…' : 'Sign in'}</button>
          </form>

          <p className="text-center mt-4 small mb-2">
            New to JewelCraft? <Link to="/register" state={{ from }} className="text-gold fw-500">Create an account</Link>
          </p>
          <div className="join-links text-center">
            <span className="small text-muted-jc me-2">Join as</span>
            {SIGNUP_ROLES.filter((r) => r.key !== 'customer').map((r) => (
              <Link key={r.key} to={`/register?as=${r.key}`}>{r.label}</Link>
            ))}
          </div>

          {SHOW_DEMO && (
            <div className="mt-4 pt-4 border-top">
              <div className="small-caps text-muted-jc mb-2">Demo accounts · click to fill</div>
              <div className="demo-grid">
                {DEMO_ACCOUNTS.map((d) => (
                  <button key={d.email} type="button" className="demo-card" onClick={() => fillDemo(d.email, d.password)}>
                    <i className={`bi bi-${d.icon}`} />
                    <b>{d.label}</b>
                    <small>{d.hint}</small>
                  </button>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
