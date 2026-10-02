import { Link } from 'react-router-dom'

/** Cream band at the top of shop pages: breadcrumb, serif title, optional subtitle. */
export default function PageHeader({ title, subtitle, crumbs = [] }) {
  return (
    <header className="page-head">
      <div className="container">
        <div className="crumbs">
          <Link to="/">Home</Link>
          {crumbs.map((c) => (
            <span key={c.label}>
              <span className="mx-2">/</span>
              {c.to ? <Link to={c.to}>{c.label}</Link> : c.label}
            </span>
          ))}
        </div>
        <h1>{title}</h1>
        {subtitle && <p className="text-soft mt-2 mb-0">{subtitle}</p>}
      </div>
    </header>
  )
}
