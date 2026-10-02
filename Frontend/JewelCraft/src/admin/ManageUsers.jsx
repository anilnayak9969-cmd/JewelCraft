import { useEffect, useState } from 'react'
import { useForm } from 'react-hook-form'
import toast from 'react-hot-toast'
import { DashHeader } from '../components/dashboard/StatCard'
import Modal, { ConfirmModal } from '../components/Modal'
import Pagination from '../components/Pagination'
import RoleDetails from '../components/RoleDetails'
import StatusBadge from '../components/StatusBadge'
import useApi from '../hooks/useApi'
import useAuth from '../hooks/useAuth'
import { applyServerErrors, errorMessage } from '../services/api'
import { userService } from '../services/userService'
import { PATTERNS, ROLE_LABELS, ROLES } from '../utils/constants'
import { formatDate, initials } from '../utils/format'

function UserForm({ user, onSaved, onCancel }) {
  const isEdit = Boolean(user)
  const {
    register,
    handleSubmit,
    reset,
    setError,
    formState: { errors, isSubmitting },
  } = useForm()

  useEffect(() => {
    reset(
      isEdit
        ? { firstName: user.firstName, lastName: user.lastName || '', email: user.email, phone: user.phone || '', role: user.role, enabled: user.enabled, password: '' }
        : { firstName: '', lastName: '', email: '', phone: '', role: 'STAFF', enabled: true, password: '' },
    )
  }, [user, isEdit, reset])

  const submit = async (values) => {
    const body = { ...values, password: values.password || null, phone: values.phone || null }
    try {
      const saved = isEdit ? await userService.update(user.id, body) : await userService.create(body)
      toast.success(isEdit ? 'User updated' : 'User created')
      onSaved(saved)
    } catch (err) {
      if (!applyServerErrors(err, setError)) toast.error(errorMessage(err))
    }
  }

  const cls = (n) => `form-control ${errors[n] ? 'is-invalid' : ''}`

  return (
    <form onSubmit={handleSubmit(submit)} noValidate>
      <div className="row g-3">
        <div className="col-md-6">
          <label className="form-label" htmlFor="u-first">First name</label>
          <input id="u-first" className={cls('firstName')} {...register('firstName', { required: 'First name is required', maxLength: { value: 50, message: 'Too long' } })} />
          <div className="invalid-feedback">{errors.firstName?.message}</div>
        </div>
        <div className="col-md-6">
          <label className="form-label" htmlFor="u-last">Last name</label>
          <input id="u-last" className={cls('lastName')} {...register('lastName', { maxLength: 50 })} />
        </div>
        <div className="col-md-6">
          <label className="form-label" htmlFor="u-email">Email</label>
          <input id="u-email" type="email" className={cls('email')} {...register('email', { required: 'Email is required', pattern: { value: PATTERNS.email, message: 'Enter a valid email address' } })} />
          <div className="invalid-feedback">{errors.email?.message}</div>
        </div>
        <div className="col-md-6">
          <label className="form-label" htmlFor="u-phone">Mobile</label>
          <input id="u-phone" inputMode="numeric" maxLength={10} className={cls('phone')} {...register('phone', { pattern: { value: PATTERNS.phone, message: 'Enter a valid 10-digit mobile number' } })} />
          <div className="invalid-feedback">{errors.phone?.message}</div>
        </div>
        <div className="col-md-6">
          <label className="form-label" htmlFor="u-role">Role</label>
          <select id="u-role" className="form-select" {...register('role', { required: true })}>
            {Object.values(ROLES).map((r) => <option key={r} value={r}>{ROLE_LABELS[r]}</option>)}
          </select>
        </div>
        <div className="col-md-6">
          <label className="form-label" htmlFor="u-pass">{isEdit ? 'New password (optional)' : 'Password'}</label>
          <input
            id="u-pass"
            type="password"
            autoComplete="new-password"
            className={cls('password')}
            {...register('password', {
              required: isEdit ? false : 'Password is required',
              validate: (v) => !v || (v.length >= 8 && v.length <= 64) || 'Password must be 8-64 characters',
            })}
          />
          <div className="invalid-feedback">{errors.password?.message}</div>
        </div>
        <div className="col-12">
          <div className="form-check form-switch">
            <input id="u-enabled" type="checkbox" className="form-check-input" {...register('enabled')} />
            <label htmlFor="u-enabled" className="form-check-label small">Account enabled</label>
          </div>
        </div>
      </div>
      <div className="d-flex justify-content-end gap-2 mt-4">
        <button type="button" className="btn btn-outline-jc btn-sm" onClick={onCancel}>Cancel</button>
        <button className="btn btn-jc btn-sm" disabled={isSubmitting}>{isSubmitting ? 'Saving…' : isEdit ? 'Save changes' : 'Create user'}</button>
      </div>
    </form>
  )
}

/** Combined account state: approval first, then email verification, then enabled/disabled. */
function AccountStatus({ u }) {
  if (u.approvalStatus === 'PENDING') return <StatusBadge status="PENDING" label="Awaiting approval" />
  if (u.approvalStatus === 'REJECTED') return <StatusBadge status="CANCELLED" label="Rejected" />
  if (!u.emailVerified) return <StatusBadge status="LOW" label="Email unverified" />
  return <StatusBadge status={u.enabled ? 'ACTIVE' : 'INACTIVE'} label={u.enabled ? 'Enabled' : 'Disabled'} />
}

/** Full user details incl. sign-up information, with Approve / Reject for applications. */
function UserReview({ userId, onClose, onDecided }) {
  const [u, setU] = useState(null)
  const [note, setNote] = useState('')
  const [busy, setBusy] = useState(false)

  useEffect(() => {
    if (!userId) return
    setU(null)
    setNote('')
    userService.get(userId).then(setU).catch((err) => toast.error(errorMessage(err)))
  }, [userId])

  const decide = async (status) => {
    setBusy(true)
    try {
      const updated = await userService.decideApproval(u.id, status, note.trim() || null)
      toast.success(status === 'APPROVED' ? `${updated.fullName} approved – they’ve been emailed` : 'Application rejected')
      onDecided(updated)
    } catch (err) {
      toast.error(errorMessage(err))
    } finally {
      setBusy(false)
    }
  }

  const reviewable = u && (u.approvalStatus === 'PENDING' || u.approvalStatus === 'REJECTED')

  return (
    <Modal
      show={Boolean(userId)}
      title={u ? u.fullName : 'User'}
      size="lg"
      onClose={onClose}
      footer={
        reviewable ? (
          <>
            {u.approvalStatus === 'PENDING' && (
              <button className="btn btn-outline-jc btn-sm" disabled={busy} onClick={() => decide('REJECTED')}>Reject</button>
            )}
            <button className="btn btn-gold btn-sm" disabled={busy || !u.emailVerified} onClick={() => decide('APPROVED')}>
              <i className="bi bi-check2 me-1" />Approve
            </button>
          </>
        ) : null
      }
    >
      {!u ? (
        <div className="text-center text-muted-jc py-4">Loading…</div>
      ) : (
        <div className="row g-4">
          <div className="col-md-5">
            <div className="d-flex align-items-center gap-3 mb-3">
              <span className="avatar" style={{ width: 48, height: 48, fontSize: '1rem' }}>{initials(u.fullName)}</span>
              <div>
                <div className="fw-500">{u.fullName}</div>
                <div className="small text-muted-jc">{ROLE_LABELS[u.role]}</div>
              </div>
            </div>
            <table className="w-100 spec-table">
              <tbody>
                <tr><td>Email</td><td className="text-break">{u.email}</td></tr>
                <tr><td>Mobile</td><td>{u.phone || '—'}</td></tr>
                <tr><td>Email verified</td><td>{u.emailVerified ? <span style={{ color: 'var(--jc-good)' }}><i className="bi bi-check-circle me-1" />Yes</span> : 'Not yet'}</td></tr>
                <tr><td>Status</td><td><AccountStatus u={u} /></td></tr>
                <tr><td>Joined</td><td>{formatDate(u.createdAt)}</td></tr>
              </tbody>
            </table>
          </div>
          <div className="col-md-7">
            <RoleDetails role={u.role} profile={u.profile} title="Sign-up details" />
            {!u.profile && <p className="small text-muted-jc">No sign-up details – this account was created by an admin.</p>}
            {reviewable && (
              <div className="mt-3">
                {!u.emailVerified && (
                  <div className="notice warn mb-3"><i className="bi bi-envelope-exclamation" /><div>The applicant has not verified their email yet, so they can’t be approved.</div></div>
                )}
                <label className="form-label" htmlFor="decision-note">Message to the applicant <span className="text-muted-jc">(optional, emailed)</span></label>
                <textarea id="decision-note" className="form-control" rows={3} maxLength={500} value={note} onChange={(e) => setNote(e.target.value)} placeholder="e.g. Welcome aboard! Please report to the MG Road boutique on Monday." />
              </div>
            )}
          </div>
        </div>
      )}
    </Modal>
  )
}

export default function ManageUsers() {
  const { user: me } = useAuth()
  const [filters, setFilters] = useState({ role: '', approval: '', search: '', page: 0 })
  const [searchInput, setSearchInput] = useState('')
  const [editing, setEditing] = useState(null)
  const [deleting, setDeleting] = useState(null)
  const [reviewing, setReviewing] = useState(null)
  const [pending, setPending] = useState(0)
  const { data, loading, error, reload, setData } = useApi(() => userService.search({ ...filters, size: 15 }), [JSON.stringify(filters)])
  const set = (patch) => setFilters((f) => ({ ...f, ...patch, page: 'page' in patch ? patch.page : 0 }))
  const loadPending = () => userService.pendingCount().then(setPending).catch(() => {})

  useEffect(() => {
    loadPending()
  }, [])

  const toggle = async (u) => {
    try {
      const updated = await userService.setEnabled(u.id, !u.enabled)
      setData((d) => ({ ...d, content: d.content.map((x) => (x.id === u.id ? updated : x)) }))
      toast.success(updated.enabled ? 'Account enabled' : 'Account disabled')
    } catch (err) {
      toast.error(errorMessage(err))
    }
  }

  const remove = async () => {
    try {
      await userService.remove(deleting.id)
      toast.success('User deleted')
      setDeleting(null)
      reload()
    } catch (err) {
      toast.error(errorMessage(err))
    }
  }

  return (
    <>
      <DashHeader title="Users" subtitle="Customers, staff, store managers, delivery partners and administrators.">
        <button className="btn btn-jc btn-sm" onClick={() => setEditing('new')}><i className="bi bi-person-plus me-1" />Add user</button>
      </DashHeader>

      <div className="nav-tabs-jc mb-3">
        {['', ...Object.values(ROLES)].map((r) => (
          <button key={r || 'all'} className={!filters.approval && filters.role === r ? 'active' : ''} onClick={() => set({ role: r, approval: '' })}>
            {r ? ROLE_LABELS[r] : 'All'}
          </button>
        ))}
        <button className={filters.approval === 'PENDING' ? 'active' : ''} onClick={() => set({ role: '', approval: 'PENDING' })}>
          Pending approval
          {pending > 0 && <span className="nav-count position-static ms-2 d-inline-block">{pending}</span>}
        </button>
      </div>

      {pending > 0 && filters.approval !== 'PENDING' && (
        <div className="notice warn mb-3">
          <i className="bi bi-hourglass-split" />
          <div>
            {pending} {pending === 1 ? 'application is' : 'applications are'} waiting for your review.{' '}
            <button className="btn-link-jc" style={{ fontSize: '0.65rem' }} onClick={() => set({ role: '', approval: 'PENDING' })}>Review now</button>
          </div>
        </div>
      )}

      <div className="card-jc">
        <div className="card-jc-head">
          <form className="filters-row" onSubmit={(e) => { e.preventDefault(); set({ search: searchInput.trim() }) }}>
            <input className="form-control form-control-sm" placeholder="Name, email or phone" value={searchInput} onChange={(e) => setSearchInput(e.target.value)} />
            <button className="btn btn-outline-jc btn-sm">Search</button>
          </form>
          <span className="small text-muted-jc">{data ? `${data.totalElements} users` : ''}</span>
        </div>
        <div className="table-responsive">
          <table className="table table-jc">
            <thead><tr><th>User</th><th>Phone</th><th>Role</th><th>Joined</th><th>Status</th><th className="text-end">Actions</th></tr></thead>
            <tbody>
              {loading && !data ? (
                <tr><td colSpan={6} className="text-center text-muted-jc py-5">Loading…</td></tr>
              ) : error ? (
                <tr><td colSpan={6} className="text-center py-5" style={{ color: 'var(--jc-bad)' }}>{error}</td></tr>
              ) : data.content.length === 0 ? (
                <tr><td colSpan={6} className="text-center text-muted-jc py-5">No users found.</td></tr>
              ) : (
                data.content.map((u) => (
                  <tr key={u.id}>
                    <td>
                      <div className="d-flex align-items-center gap-2">
                        <span className="avatar">{initials(u.fullName)}</span>
                        <div><div className="fw-500">{u.fullName}</div><div className="small text-muted-jc">{u.email}</div></div>
                      </div>
                    </td>
                    <td>{u.phone || '—'}</td>
                    <td>{ROLE_LABELS[u.role]}</td>
                    <td className="small">{formatDate(u.createdAt)}</td>
                    <td><AccountStatus u={u} /></td>
                    <td className="text-end text-nowrap">
                      {u.id === me.id ? (
                        <span className="small text-muted-jc">You</span>
                      ) : (
                        <>
                          {u.approvalStatus === 'PENDING' ? (
                            <button className="btn btn-gold btn-sm me-1" onClick={() => setReviewing(u.id)}>Review</button>
                          ) : (
                            <button className="btn btn-outline-jc btn-sm btn-icon me-1" title="View details" aria-label="View details" onClick={() => setReviewing(u.id)}><i className="bi bi-eye" /></button>
                          )}
                          <button className="btn btn-outline-jc btn-sm btn-icon me-1" title="Edit" aria-label="Edit" onClick={() => setEditing(u)}><i className="bi bi-pencil" /></button>
                          <button className="btn btn-outline-jc btn-sm btn-icon me-1" title={u.enabled ? 'Disable' : 'Enable'} aria-label={u.enabled ? 'Disable' : 'Enable'} onClick={() => toggle(u)}>
                            <i className={`bi bi-${u.enabled ? 'slash-circle' : 'check-circle'}`} />
                          </button>
                          <button className="btn btn-outline-jc btn-sm btn-icon" title="Delete" aria-label="Delete" onClick={() => setDeleting(u)}><i className="bi bi-trash" /></button>
                        </>
                      )}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
        {data && <div className="p-3"><Pagination page={data.page} totalPages={data.totalPages} onChange={(page) => set({ page })} /></div>}
      </div>

      <Modal show={Boolean(editing)} title={editing === 'new' ? 'New user' : 'Edit user'} size="lg" onClose={() => setEditing(null)}>
        {editing && (
          <UserForm
            user={editing === 'new' ? null : editing}
            onCancel={() => setEditing(null)}
            onSaved={() => {
              setEditing(null)
              reload()
            }}
          />
        )}
      </Modal>
      <UserReview
        userId={reviewing}
        onClose={() => setReviewing(null)}
        onDecided={() => {
          setReviewing(null)
          reload()
          loadPending()
        }}
      />
      <ConfirmModal
        show={Boolean(deleting)}
        title="Delete user?"
        message={`${deleting?.fullName} (${deleting?.email}) will be deleted. Users with orders may only be disabled.`}
        confirmText="Delete"
        onConfirm={remove}
        onClose={() => setDeleting(null)}
      />
    </>
  )
}
