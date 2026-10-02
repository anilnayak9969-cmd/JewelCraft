import { Navigate, Outlet, useLocation } from 'react-router-dom'
import useAuth from '../hooks/useAuth'
import Loading from './Loading'

/**
 * Guards a group of routes. Not signed in -> /login (and back here afterwards).
 * Signed in with the wrong role -> /unauthorized. The backend enforces the same rules.
 */
export default function ProtectedRoute({ roles, children }) {
  const { user, loading } = useAuth()
  const location = useLocation()

  if (loading) return <Loading fullPage />
  if (!user) return <Navigate to="/login" replace state={{ from: location.pathname + location.search }} />
  if (roles && !roles.includes(user.role)) return <Navigate to="/unauthorized" replace />

  return children || <Outlet />
}
