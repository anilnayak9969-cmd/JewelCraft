/** Page numbers are 0-based (as Spring returns them); shown 1-based. */
export default function Pagination({ page, totalPages, onChange, className = '' }) {
  if (!totalPages || totalPages <= 1) return null

  const pages = []
  const start = Math.max(0, Math.min(page - 2, totalPages - 5))
  const end = Math.min(totalPages, start + 5)
  for (let i = start; i < end; i++) pages.push(i)

  const go = (p) => {
    if (p < 0 || p >= totalPages || p === page) return
    onChange(p)
  }

  return (
    <nav aria-label="Pagination" className={className}>
      <ul className="pagination justify-content-center mb-0">
        <li className={`page-item ${page === 0 ? 'disabled' : ''}`}>
          <button className="page-link" onClick={() => go(page - 1)} aria-label="Previous page">
            <i className="bi bi-chevron-left" />
          </button>
        </li>
        {pages.map((p) => (
          <li key={p} className={`page-item ${p === page ? 'active' : ''}`}>
            <button className="page-link" onClick={() => go(p)}>{p + 1}</button>
          </li>
        ))}
        <li className={`page-item ${page >= totalPages - 1 ? 'disabled' : ''}`}>
          <button className="page-link" onClick={() => go(page + 1)} aria-label="Next page">
            <i className="bi bi-chevron-right" />
          </button>
        </li>
      </ul>
    </nav>
  )
}
