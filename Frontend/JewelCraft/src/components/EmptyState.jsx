export default function EmptyState({ icon = 'gem', title, text, children }) {
  return (
    <div className="empty">
      <i className={`bi bi-${icon}`} />
      <h3>{title}</h3>
      {text && <p className="text-soft mx-auto" style={{ maxWidth: 420 }}>{text}</p>}
      {children && <div className="mt-4">{children}</div>}
    </div>
  )
}
