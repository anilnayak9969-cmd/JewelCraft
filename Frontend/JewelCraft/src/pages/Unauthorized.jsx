import { Link } from 'react-router-dom'
import useAuth from '../hooks/useAuth'
import { ROLE_HOME, ROLE_LABELS } from '../utils/constants'

export default function Unauthorized() {
  const { user } = useAuth()
  return (
    <div className="container text-center" style={{ padding: '6rem 1rem' }}>
      <i className="bi bi-shield-lock text-gold" style={{ fontSize: '3rem' }} />
      <div className="eyebrow mt-3">Access restricted</div>
      <h1 className="mt-2" style={{ fontSize: '2.8rem' }}>This area is not available to you</h1>
      <p className="text-soft mx-auto" style={{ maxWidth: 460 }}>
        {user ? `Your ${ROLE_LABELS[user.role]} account does not have permission to open this page.` : 'Please sign in to continue.'}
      </p>
      <Link to={user ? ROLE_HOME[user.role] : '/login'} className="btn btn-jc mt-3">
        {user ? 'Go to my area' : 'Sign in'}
      </Link>
    </div>
  )
}
