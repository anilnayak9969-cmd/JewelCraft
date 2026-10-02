import { useEffect, useState } from 'react'
import { useForm } from 'react-hook-form'
import toast from 'react-hot-toast'
import { Link } from 'react-router-dom'
import AddressForm from '../components/AddressForm'
import EmptyState from '../components/EmptyState'
import JewelImage from '../components/JewelImage'
import Loading from '../components/Loading'
import Modal, { ConfirmModal } from '../components/Modal'
import PageHeader from '../components/PageHeader'
import RequestForm from '../components/RequestForm'
import RoleDetails from '../components/RoleDetails'
import Stars from '../components/Stars'
import StatusBadge from '../components/StatusBadge'
import useAuth from '../hooks/useAuth'
import { applyServerErrors, errorMessage } from '../services/api'
import { orderService } from '../services/orderService'
import { reviewService } from '../services/productService'
import { addressService, requestService, userService } from '../services/userService'
import { PATTERNS, ROLE_LABELS } from '../utils/constants'
import { formatDate, humanize } from '../utils/format'

function DetailsTab() {
  const { user, setUser } = useAuth()
  const {
    register,
    handleSubmit,
    setError,
    formState: { errors, isSubmitting, isDirty },
    reset,
  } = useForm({ defaultValues: { firstName: user.firstName, lastName: user.lastName || '', phone: user.phone || '' } })

  const submit = async (values) => {
    try {
      const updated = await userService.updateProfile(values)
      setUser(updated)
      reset({ firstName: updated.firstName, lastName: updated.lastName || '', phone: updated.phone || '' })
      toast.success('Profile updated')
    } catch (err) {
      if (!applyServerErrors(err, setError)) toast.error(errorMessage(err))
    }
  }

  return (
    <form onSubmit={handleSubmit(submit)} noValidate style={{ maxWidth: 620 }}>
      <div className="row g-3">
        <div className="col-sm-6">
          <label className="form-label" htmlFor="p-first">First name</label>
          <input id="p-first" className={`form-control ${errors.firstName ? 'is-invalid' : ''}`} {...register('firstName', { required: 'First name is required', maxLength: { value: 50, message: 'At most 50 characters' } })} />
          <div className="invalid-feedback">{errors.firstName?.message}</div>
        </div>
        <div className="col-sm-6">
          <label className="form-label" htmlFor="p-last">Last name</label>
          <input id="p-last" className="form-control" {...register('lastName', { maxLength: 50 })} />
        </div>
        <div className="col-sm-6">
          <label className="form-label" htmlFor="p-email">Email</label>
          <input id="p-email" className="form-control" value={user.email} disabled readOnly />
        </div>
        <div className="col-sm-6">
          <label className="form-label" htmlFor="p-phone">Mobile number</label>
          <input id="p-phone" inputMode="numeric" maxLength={10} className={`form-control ${errors.phone ? 'is-invalid' : ''}`} {...register('phone', { required: 'Phone number is required', pattern: { value: PATTERNS.phone, message: 'Enter a valid 10-digit mobile number' } })} />
          <div className="invalid-feedback">{errors.phone?.message}</div>
        </div>
      </div>
      <div className="small text-muted-jc mt-3">
        {ROLE_LABELS[user.role]} account · member since {formatDate(user.createdAt)}
      </div>
      <button className="btn btn-jc mt-4" disabled={isSubmitting || !isDirty}>{isSubmitting ? 'Saving…' : 'Save changes'}</button>
      {user.profile && (
        <div className="mt-5" style={{ maxWidth: 520 }}>
          <RoleDetails
            role={user.role}
            profile={user.profile}
            title={user.role === 'CUSTOMER' ? 'Your preferences' : 'Your professional details'}
          />
          {user.role !== 'CUSTOMER' && <p className="small text-muted-jc mt-2">To change these details, please contact an administrator.</p>}
        </div>
      )}
    </form>
  )
}

function PasswordTab() {
  const {
    register,
    handleSubmit,
    watch,
    reset,
    setError,
    formState: { errors, isSubmitting },
  } = useForm({ defaultValues: { currentPassword: '', newPassword: '', confirm: '' } })

  const submit = async ({ currentPassword, newPassword }) => {
    try {
      await userService.changePassword({ currentPassword, newPassword })
      toast.success('Password changed')
      reset()
    } catch (err) {
      if (!applyServerErrors(err, setError)) toast.error(errorMessage(err))
    }
  }

  return (
    <form onSubmit={handleSubmit(submit)} noValidate style={{ maxWidth: 420 }}>
      <div className="mb-3">
        <label className="form-label" htmlFor="pw-cur">Current password</label>
        <input id="pw-cur" type="password" autoComplete="current-password" className={`form-control ${errors.currentPassword ? 'is-invalid' : ''}`} {...register('currentPassword', { required: 'Current password is required' })} />
        <div className="invalid-feedback">{errors.currentPassword?.message}</div>
      </div>
      <div className="mb-3">
        <label className="form-label" htmlFor="pw-new">New password</label>
        <input
          id="pw-new"
          type="password"
          autoComplete="new-password"
          className={`form-control ${errors.newPassword ? 'is-invalid' : ''}`}
          {...register('newPassword', {
            required: 'New password is required',
            minLength: { value: 8, message: 'Password must be 8-64 characters' },
            maxLength: { value: 64, message: 'Password must be 8-64 characters' },
            pattern: { value: PATTERNS.password, message: 'Use both letters and numbers' },
          })}
        />
        <div className="invalid-feedback">{errors.newPassword?.message}</div>
      </div>
      <div className="mb-3">
        <label className="form-label" htmlFor="pw-confirm">Confirm new password</label>
        <input id="pw-confirm" type="password" autoComplete="new-password" className={`form-control ${errors.confirm ? 'is-invalid' : ''}`} {...register('confirm', { validate: (v) => v === watch('newPassword') || 'Passwords do not match' })} />
        <div className="invalid-feedback">{errors.confirm?.message}</div>
      </div>
      <button className="btn btn-jc" disabled={isSubmitting}>{isSubmitting ? 'Updating…' : 'Update password'}</button>
    </form>
  )
}

function AddressesTab() {
  const [addresses, setAddresses] = useState(null)
  const [editing, setEditing] = useState(null) // null | 'new' | address
  const [deleting, setDeleting] = useState(null)

  const load = () => addressService.list().then(setAddresses).catch(() => setAddresses([]))
  useEffect(() => {
    load()
  }, [])

  const save = async (values) => {
    if (editing === 'new') await addressService.add(values)
    else await addressService.update(editing.id, values)
    toast.success('Address saved')
    setEditing(null)
    load()
  }

  const makeDefault = async (a) => {
    try {
      await addressService.setDefault(a.id)
      load()
    } catch (err) {
      toast.error(errorMessage(err))
    }
  }

  const remove = async () => {
    try {
      await addressService.remove(deleting.id)
      toast.success('Address removed')
      setDeleting(null)
      load()
    } catch (err) {
      toast.error(errorMessage(err))
    }
  }

  if (!addresses) return <Loading />

  return (
    <>
      <div className="row g-3">
        {addresses.map((a) => (
          <div key={a.id} className="col-md-6 col-xl-4">
            <div className="panel h-100 d-flex flex-column">
              <div className="fw-500 mb-1">
                {a.fullName}
                {a.defaultAddress && <span className="status gold ms-2" style={{ fontSize: '0.58rem' }}>Default</span>}
              </div>
              <div className="small text-soft flex-grow-1">
                {a.line1}{a.line2 ? `, ${a.line2}` : ''}<br />{a.city}, {a.state} – {a.pincode}<br />{a.phone}
              </div>
              <div className="d-flex gap-3 mt-3">
                <button className="btn-link-jc" style={{ fontSize: '0.65rem' }} onClick={() => setEditing(a)}>Edit</button>
                {!a.defaultAddress && <button className="btn-link-jc" style={{ fontSize: '0.65rem' }} onClick={() => makeDefault(a)}>Set default</button>}
                <button className="btn-link-jc" style={{ fontSize: '0.65rem' }} onClick={() => setDeleting(a)}>Remove</button>
              </div>
            </div>
          </div>
        ))}
        <div className="col-md-6 col-xl-4">
          <button className="select-card h-100 d-flex flex-column align-items-center justify-content-center" style={{ minHeight: 160 }} onClick={() => setEditing('new')}>
            <i className="bi bi-plus-lg text-gold" style={{ fontSize: '1.4rem' }} />
            <span className="small-caps mt-1">Add address</span>
          </button>
        </div>
      </div>

      <Modal show={Boolean(editing)} title={editing === 'new' ? 'New address' : 'Edit address'} size="lg" onClose={() => setEditing(null)}>
        <AddressForm initial={editing === 'new' ? {} : editing} onSubmit={save} onCancel={() => setEditing(null)} />
      </Modal>
      <ConfirmModal show={Boolean(deleting)} title="Remove address?" message="This address will be removed from your account." confirmText="Remove" onConfirm={remove} onClose={() => setDeleting(null)} />
    </>
  )
}

function ReviewsTab() {
  const [reviews, setReviews] = useState(null)
  useEffect(() => {
    reviewService.mine().then(setReviews).catch(() => setReviews([]))
  }, [])

  const remove = async (id) => {
    try {
      await reviewService.remove(id)
      setReviews((list) => list.filter((r) => r.id !== id))
      toast.success('Review deleted')
    } catch (err) {
      toast.error(errorMessage(err))
    }
  }

  if (!reviews) return <Loading />
  if (reviews.length === 0) return <EmptyState icon="star" title="No reviews yet" text="Reviews you write on delivered pieces will appear here." />

  return (
    <div className="d-flex flex-column gap-3">
      {reviews.map((r) => (
        <div key={r.id} className="panel d-flex gap-3">
          <Link to={`/products/${r.productId}`} className="mini-thumb"><JewelImage src={r.productImage} alt={r.productName} name={r.productName} /></Link>
          <div className="flex-grow-1">
            <div className="d-flex justify-content-between flex-wrap gap-2">
              <Link to={`/products/${r.productId}`} className="fw-500">{r.productName}</Link>
              <span className="small text-muted-jc">{formatDate(r.createdAt)}</span>
            </div>
            <Stars value={r.rating} />
            {r.title && <div className="fw-500 mt-1">{r.title}</div>}
            <p className="small text-soft mb-1">{r.comment}</p>
            <button className="btn-link-jc" style={{ fontSize: '0.62rem' }} onClick={() => remove(r.id)}>Delete</button>
          </div>
        </div>
      ))}
    </div>
  )
}

function RequestsTab() {
  const [requests, setRequests] = useState(null)
  const [orders, setOrders] = useState([])
  const [creating, setCreating] = useState(false)

  const load = () => requestService.mine().then(setRequests).catch(() => setRequests([]))
  useEffect(() => {
    load()
    orderService.mine(0, 50).then((r) => setOrders(r.content)).catch(() => {})
  }, [])

  if (!requests) return <Loading />

  return (
    <>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <p className="text-soft mb-0 small">Returns, exchanges, customisation and questions.</p>
        <button className="btn btn-jc btn-sm" onClick={() => setCreating(true)}>New request</button>
      </div>
      {requests.length === 0 ? (
        <EmptyState icon="chat-square-heart" title="No requests yet" text="Our concierge team is here whenever you need us." />
      ) : (
        <div className="d-flex flex-column gap-3">
          {requests.map((r) => (
            <div key={r.id} className="panel">
              <div className="d-flex justify-content-between flex-wrap gap-2">
                <div>
                  <span className="small-caps text-muted-jc">{humanize(r.type)}{r.orderNumber ? ` · ${r.orderNumber}` : ''}</span>
                  <div className="fw-500">{r.subject}</div>
                </div>
                <StatusBadge status={r.status} />
              </div>
              <p className="small text-soft mt-2 mb-0">{r.message}</p>
              {r.response && (
                <div className="mt-3 p-3 bg-cream small">
                  <div className="small-caps text-gold mb-1">Reply from {r.handledBy || 'JewelCraft'}</div>
                  {r.response}
                </div>
              )}
              <div className="small text-muted-jc mt-2">Raised {formatDate(r.createdAt)}</div>
            </div>
          ))}
        </div>
      )}
      <Modal show={creating} title="New request" size="lg" onClose={() => setCreating(false)}>
        <RequestForm
          orders={orders}
          onCancel={() => setCreating(false)}
          onDone={() => {
            setCreating(false)
            load()
          }}
        />
      </Modal>
    </>
  )
}

export default function Profile({ embedded = false }) {
  const { user } = useAuth()
  const isCustomer = user.role === 'CUSTOMER'
  const tabs = isCustomer
    ? [['details', 'Personal details'], ['addresses', 'Addresses'], ['password', 'Password'], ['reviews', 'My reviews'], ['requests', 'Support requests']]
    : [['details', 'Personal details'], ['password', 'Password']]
  const [tab, setTab] = useState('details')

  const body = (
    <>
      <div className="nav-tabs-jc mb-4">
        {tabs.map(([key, label]) => (
          <button key={key} className={tab === key ? 'active' : ''} onClick={() => setTab(key)}>{label}</button>
        ))}
      </div>
      {tab === 'details' && <DetailsTab />}
      {tab === 'addresses' && <AddressesTab />}
      {tab === 'password' && <PasswordTab />}
      {tab === 'reviews' && <ReviewsTab />}
      {tab === 'requests' && <RequestsTab />}
    </>
  )

  if (embedded) return <div className="card-jc"><div className="card-jc-body">{body}</div></div>

  return (
    <>
      <PageHeader title={`Hello, ${user.firstName}`} subtitle="Manage your account" crumbs={[{ label: 'My account' }]} />
      <div className="container py-5">{body}</div>
    </>
  )
}
