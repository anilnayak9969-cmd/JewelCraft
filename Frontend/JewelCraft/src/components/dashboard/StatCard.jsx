export default function StatCard({ label, value, icon, hint }) {
  return (
    <div className="stat-card">
      {icon && <span className="ic"><i className={`bi bi-${icon}`} /></span>}
      <div className="label">{label}</div>
      <div className="value">{value}</div>
      {hint && <div className="hint">{hint}</div>}
    </div>
  )
}

/** Page title row used at the top of every dashboard screen. */
export function DashHeader({ title, subtitle, children }) {
  return (
    <div className="d-flex flex-wrap justify-content-between align-items-end gap-3 mb-4">
      <div>
        <h1 className="dash-title">{title}</h1>
        {subtitle && <p className="text-soft small mb-0 mt-1">{subtitle}</p>}
      </div>
      {children && <div className="d-flex flex-wrap align-items-center gap-2">{children}</div>}
    </div>
  )
}
