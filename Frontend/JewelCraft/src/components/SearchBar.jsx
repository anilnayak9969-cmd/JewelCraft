import { useState } from 'react'
import { useNavigate } from 'react-router-dom'

/**
 * Search input. Without `onSearch` it opens the shop with ?search=…;
 * the product listing passes its own handler to keep the other filters.
 */
export default function SearchBar({ initial = '', onSearch, autoFocus = false, placeholder = 'Search rings, necklaces, diamonds…' }) {
  const [term, setTerm] = useState(initial)
  const navigate = useNavigate()

  const submit = (e) => {
    e.preventDefault()
    const q = term.trim()
    if (onSearch) onSearch(q)
    else navigate(q ? `/products?search=${encodeURIComponent(q)}` : '/products')
  }

  return (
    <form onSubmit={submit} role="search" className="position-relative">
      <i className="bi bi-search position-absolute text-muted-jc" style={{ left: 14, top: '50%', transform: 'translateY(-50%)' }} />
      <input
        type="search"
        className="form-control"
        style={{ paddingLeft: 40 }}
        value={term}
        onChange={(e) => setTerm(e.target.value)}
        placeholder={placeholder}
        aria-label="Search jewellery"
        autoFocus={autoFocus}
      />
    </form>
  )
}
