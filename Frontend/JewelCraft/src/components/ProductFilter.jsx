import { METAL_TONES, METAL_TYPES, PRICE_RANGES } from '../utils/constants'
import { metalLabel } from '../utils/format'

/**
 * Sidebar filters. `filters` holds the current URL values as strings;
 * `onChange(patch)` updates them (an empty string removes a filter).
 */
export default function ProductFilter({ filters, categories = [], purities = [], onChange, onClear }) {
  const toggle = (key, value) => onChange({ [key]: filters[key] === String(value) ? '' : String(value) })
  const activePrice = (r) => (filters.minPrice || '') === r.min && (filters.maxPrice || '') === r.max

  return (
    <div className="filter-panel">
      <h6>Category</h6>
      <button className={`filter-link ${!filters.categoryId ? 'active' : ''}`} onClick={() => onChange({ categoryId: '' })}>
        All jewellery
      </button>
      {categories.map((c) => (
        <button
          key={c.id}
          className={`filter-link ${filters.categoryId === String(c.id) ? 'active' : ''}`}
          onClick={() => toggle('categoryId', c.id)}
        >
          <span>{c.name}</span>
          <span className="text-muted-jc small">{c.productCount}</span>
        </button>
      ))}

      <h6>Metal</h6>
      {METAL_TYPES.map((m) => (
        <button key={m} className={`filter-link ${filters.metalType === m ? 'active' : ''}`} onClick={() => toggle('metalType', m)}>
          <span>
            <span className="metal-swatch" style={{ background: METAL_TONES[m].swatch }} />
            {metalLabel(m)}
          </span>
          {filters.metalType === m && <i className="bi bi-check2" />}
        </button>
      ))}

      {purities.length > 0 && (
        <>
          <h6>Purity</h6>
          <div className="d-flex flex-wrap gap-2">
            {purities.map((p) => (
              <button
                key={p}
                type="button"
                className={`btn btn-sm ${filters.purity === p ? 'btn-jc' : 'btn-outline-jc'}`}
                style={{ letterSpacing: '0.06em' }}
                onClick={() => toggle('purity', p)}
              >
                {p}
              </button>
            ))}
          </div>
        </>
      )}

      <h6>Price</h6>
      {PRICE_RANGES.map((r) => (
        <button
          key={r.label}
          className={`filter-link ${activePrice(r) ? 'active' : ''}`}
          onClick={() => (activePrice(r) ? onChange({ minPrice: '', maxPrice: '' }) : onChange({ minPrice: r.min, maxPrice: r.max }))}
        >
          {r.label}
          {activePrice(r) && <i className="bi bi-check2" />}
        </button>
      ))}

      <h6>Availability</h6>
      <div className="form-check">
        <input
          id="f-instock"
          type="checkbox"
          className="form-check-input"
          checked={filters.inStock === 'true'}
          onChange={(e) => onChange({ inStock: e.target.checked ? 'true' : '' })}
        />
        <label htmlFor="f-instock" className="form-check-label small">In stock only</label>
      </div>
      <div className="form-check mt-1">
        <input
          id="f-featured"
          type="checkbox"
          className="form-check-input"
          checked={filters.featured === 'true'}
          onChange={(e) => onChange({ featured: e.target.checked ? 'true' : '' })}
        />
        <label htmlFor="f-featured" className="form-check-label small">Signature pieces</label>
      </div>

      <button className="btn btn-outline-jc btn-sm w-100 mt-4" onClick={onClear}>Clear all filters</button>
    </div>
  )
}
