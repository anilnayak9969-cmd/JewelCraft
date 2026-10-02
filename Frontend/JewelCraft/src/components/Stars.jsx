/** Read-only star rating. */
export default function Stars({ value = 0, size = '0.85rem' }) {
  const rounded = Math.round(Number(value) || 0)
  return (
    <span className="stars" style={{ fontSize: size }} aria-label={`${Number(value || 0).toFixed(1)} out of 5`}>
      {[1, 2, 3, 4, 5].map((i) => (
        <span key={i} className={i <= rounded ? '' : 'off'}>★</span>
      ))}
    </span>
  )
}

/** Clickable star picker for review forms. */
export function StarInput({ value, onChange }) {
  return (
    <div className="star-input" role="radiogroup" aria-label="Rating">
      {[1, 2, 3, 4, 5].map((i) => (
        <button
          key={i}
          type="button"
          className={i <= value ? 'on' : ''}
          onClick={() => onChange(i)}
          role="radio"
          aria-checked={i === value}
          aria-label={`${i} star${i > 1 ? 's' : ''}`}
        >
          ★
        </button>
      ))}
    </div>
  )
}
