import { useEffect, useState } from 'react'
import { Link, NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom'
import useAuth from '../../hooks/useAuth'
import { ROLE_LABELS } from '../../utils/constants'
import { initials } from '../../utils/format'

/** Sidebar + top bar shell shared by the admin, store, staff and delivery areas. */
export default function DashboardLayout({ title, links }) {
  const { user, logout } = useAuth()
  const [open, setOpen] = useState(false)
  const location = useLocation()
  const navigate = useNavigate()

  useEffect(() => setOpen(false), [location.pathname])

  const signOut = () => {
    logout()
    navigate('/login')
  }

  return (
    <div className="dash">
      <aside className={`dash-side ${open ? 'open' : ''}`}>
        <Link to="/" className="jc-brand">JewelCraft</Link>
        <span className="role">{title} Console</span>
        <nav>
          {links.map((l) => (
            <NavLink key={l.to} to={l.to} end={l.end}>
              <i className={`bi bi-${l.icon}`} />
              {l.label}
            </NavLink>
          ))}
        </nav>
        <div className="side-foot">
          <Link to="/"><i className="bi bi-shop me-2" />View storefront</Link>
        </div>
      </aside>

      <div className="dash-main">
        <header className="dash-top">
          <div className="d-flex align-items-center gap-2">
            <button className="nav-icon d-lg-none" onClick={() => setOpen(true)} aria-label="Open menu">
              <i className="bi bi-list" style={{ fontSize: '1.4rem' }} />
            </button>
            <span className="small-caps text-muted-jc d-none d-sm-inline">{title} · {ROLE_LABELS[user.role]}</span>
          </div>
          <div className="dropdown">
            <button className="d-flex align-items-center gap-2 bg-transparent border-0" data-bs-toggle="dropdown" aria-expanded="false">
              <span className="avatar">{initials(user.fullName)}</span>
              <span className="small d-none d-md-inline">{user.fullName}</span>
              <i className="bi bi-chevron-down small" />
            </button>
            <ul className="dropdown-menu dropdown-menu-end">
              <li className="px-3 py-2 small text-muted-jc">{user.email}</li>
              <li><hr className="dropdown-divider" /></li>
              <li><Link className="dropdown-item" to="/">Storefront</Link></li>
              <li><button className="dropdown-item" onClick={signOut}>Sign out</button></li>
            </ul>
          </div>
        </header>
        <main className="dash-content">
          <Outlet />
        </main>
      </div>
      {open && <div className="d-lg-none" style={{ position: 'fixed', inset: 0, zIndex: 1065 }} onClick={() => setOpen(false)} />}
    </div>
  )
}
