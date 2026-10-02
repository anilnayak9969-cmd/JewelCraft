import { useForm } from 'react-hook-form'
import toast from 'react-hot-toast'
import { Link, Navigate, useLocation, useNavigate, useSearchParams } from 'react-router-dom'
import AuthArt from '../components/AuthArt'
import AccountFields from '../components/register/AccountFields'
import AdminFields from '../components/register/AdminFields'
import CustomerFields from '../components/register/CustomerFields'
import DeliveryFields from '../components/register/DeliveryFields'
import FormSection from '../components/register/FormSection'
import ManagerFields from '../components/register/ManagerFields'
import StaffFields from '../components/register/StaffFields'
import useAuth from '../hooks/useAuth'
import { applyServerErrors, errorMessage } from '../services/api'
import authService from '../services/authService'
import { ROLE_HOME, SIGNUP_ROLES } from '../utils/constants'

const STAFF_JOURNEY = ['Apply', 'Verify email', 'Admin review', 'Sign in']

/** What the left panel says for each kind of account. */
const ROLE_ART = {
  customer: {
    quote: '“Every piece tells a story. Begin yours.”',
    caption: 'Join JewelCraft',
    glyph: 'ring',
    perks: [
      ['gift', 'Birthday & anniversary privileges'],
      ['heart', 'Save favourites to your wishlist'],
      ['geo-alt', 'Live tracking for every order'],
      ['shield-check', 'Secure, insured delivery'],
    ],
  },
  staff: {
    quote: '“Behind every heirloom is a team that cares.”',
    caption: 'Careers at JewelCraft',
    glyph: 'necklace',
    perks: [['person-badge', 'Your own employee code'], ['boxes', 'Manage products, stock and orders'], ['chat-heart', 'Help customers find their piece']],
    journey: STAFF_JOURNEY,
  },
  manager: {
    quote: '“Lead a boutique where craft meets care.”',
    caption: 'Store leadership',
    glyph: 'gem',
    perks: [['shop', 'Run your boutique’s daily operations'], ['currency-rupee', 'Set prices from live metal rates'], ['bar-chart', 'Store reports & team oversight']],
    journey: STAFF_JOURNEY,
  },
  delivery: {
    quote: '“Precious cargo, delivered with care.”',
    caption: 'Delivery partners',
    glyph: 'pendant',
    perks: [['geo', 'Deliveries in your own area'], ['phone', 'Simple step-by-step delivery updates'], ['shield-lock', 'Insured, tamper-proof packages']],
    journey: STAFF_JOURNEY,
  },
  admin: {
    quote: '“Guarding the house of JewelCraft.”',
    caption: 'Administration',
    glyph: 'gem',
    perks: [['people', 'Approve staff, managers and partners'], ['graph-up-arrow', 'Business-wide reports & settings'], ['shield-lock', 'Full control of products, orders and offers']],
    journey: ['Apply', 'Verify email', 'Approval by an existing admin', 'Sign in'],
  },
}

/** Empty strings become null so optional fields are simply left out. */
function toPayload(roleKey, values) {
  const { confirmPassword: _c, agree: _a, ...rest } = values
  const body = Object.fromEntries(Object.entries(rest).map(([k, v]) => [k, v === '' ? null : v]))
  if (body.experienceYears != null) body.experienceYears = Number(body.experienceYears)
  if (roleKey === 'customer') body.marketingOptIn = Boolean(body.marketingOptIn)
  if (roleKey === 'delivery' && body.vehicleType === 'BICYCLE') {
    body.vehicleNumber = null
    body.drivingLicenseNumber = null
  }
  return body
}

function RegisterForm({ option }) {
  const navigate = useNavigate()
  const from = useLocation().state?.from
  const {
    register,
    handleSubmit,
    watch,
    setError,
    formState: { errors, isSubmitting },
  } = useForm({ defaultValues: { marketingOptIn: true } })
  const isStaffType = option.key !== 'customer'

  const submit = async (values) => {
    try {
      const res = await authService.register(option.role, toPayload(option.key, values))
      toast.success('Check your inbox for the verification code')
      navigate('/verify-email', {
        state: {
          email: res.email,
          requiresApproval: res.requiresApproval,
          expiresIn: res.otpExpiresInSeconds,
          resendIn: res.resendAvailableInSeconds,
          message: res.message,
          from,
        },
      })
    } catch (err) {
      const code = err.response?.data?.code
      if (code === 'EMAIL_NOT_VERIFIED') {
        toast(err.response.data.message, { icon: '✉️' })
        navigate('/verify-email', { state: { email: values.email.trim().toLowerCase(), autoResend: true, from } })
        return
      }
      if (!applyServerErrors(err, setError)) toast.error(errorMessage(err))
    }
  }

  const props = { register, errors, watch }
  return (
    <form onSubmit={handleSubmit(submit)} noValidate>
      <AccountFields {...props} start={1} />
      {option.key === 'customer' && <CustomerFields {...props} start={3} />}
      {option.key === 'staff' && <StaffFields {...props} start={3} />}
      {option.key === 'manager' && <ManagerFields {...props} start={3} />}
      {option.key === 'delivery' && <DeliveryFields {...props} start={3} />}
      {option.key === 'admin' && <AdminFields {...props} start={3} />}

      {isStaffType && (
        <FormSection number={option.key === 'delivery' ? 5 : 4} title="Confirm">
          <div className="notice mb-3">
            <i className="bi bi-shield-check" />
            <div>
              {option.key === 'admin'
                ? 'Admin access is powerful, so after you verify your email an existing JewelCraft admin must approve your application. You can sign in as soon as it is approved – we’ll email you.'
                : 'After you verify your email, our admin team reviews your application. You can sign in as soon as it is approved – we’ll email you.'}
            </div>
          </div>
          <div className="form-check">
            <input id="agree" type="checkbox" className={`form-check-input ${errors.agree ? 'is-invalid' : ''}`} {...register('agree', { required: 'Please confirm your details are correct' })} />
            <label htmlFor="agree" className="form-check-label small">I confirm the details above are correct and may be verified by JewelCraft.</label>
            <div className="invalid-feedback">{errors.agree?.message}</div>
          </div>
        </FormSection>
      )}

      <button className="btn btn-jc w-100 mt-2" disabled={isSubmitting}>
        {isSubmitting ? 'Sending verification code…' : isStaffType ? 'Submit application' : 'Create account'}
      </button>
    </form>
  )
}

export default function Register() {
  const { user } = useAuth()
  const [params, setParams] = useSearchParams()
  const from = useLocation().state?.from
  const option = SIGNUP_ROLES.find((r) => r.key === params.get('as')) || SIGNUP_ROLES[0]
  const art = ROLE_ART[option.key]

  if (user) return <Navigate to={ROLE_HOME[user.role] || '/'} replace />

  return (
    <div className="auth-wrap">
      <AuthArt {...art} />
      <div className="auth-form align-items-start">
        <div className="inner" style={{ maxWidth: 560 }}>
          <div className="eyebrow">New here</div>
          <h1 className="mt-2 mb-1" style={{ fontSize: '2.6rem' }}>
            {option.key === 'customer' ? 'Create an account' : `Join as ${option.label}`}
          </h1>
          <p className="text-soft mb-4">{option.tagline}.</p>

          <div className="role-picker" role="tablist" aria-label="Account type">
            {SIGNUP_ROLES.map((r) => (
              <button
                key={r.key}
                type="button"
                role="tab"
                aria-selected={r.key === option.key}
                className={`role-option ${r.key === option.key ? 'active' : ''}`}
                onClick={() => setParams(r.key === 'customer' ? {} : { as: r.key }, { replace: true, state: { from } })}
              >
                <i className={`bi bi-${r.icon}`} />
                <span>{r.label}</span>
              </button>
            ))}
          </div>

          {/* key resets the form when switching account type */}
          <RegisterForm key={option.key} option={option} />

          <p className="text-center mt-4 small">
            Already have an account? <Link to="/login" state={{ from }} className="text-gold fw-500">Sign in</Link>
          </p>
        </div>
      </div>
    </div>
  )
}
