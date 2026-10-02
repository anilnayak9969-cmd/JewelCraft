import { useEffect, useMemo, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import EmptyState from '../components/EmptyState'
import Loading from '../components/Loading'
import Modal from '../components/Modal'
import PageHeader from '../components/PageHeader'
import Pagination from '../components/Pagination'
import ProductCard from '../components/ProductCard'
import ProductFilter from '../components/ProductFilter'
import SearchBar from '../components/SearchBar'
import useApi from '../hooks/useApi'
import { categoryService, productService } from '../services/productService'
import { PRICE_RANGES, SORT_OPTIONS } from '../utils/constants'
import { metalLabel } from '../utils/format'

const FILTER_KEYS = ['search', 'categoryId', 'metalType', 'purity', 'minPrice', 'maxPrice', 'inStock', 'featured', 'sort', 'page']
const PAGE_SIZE = 12

export default function Products() {
  const [params, setParams] = useSearchParams()
  const [categories, setCategories] = useState([])
  const [purities, setPurities] = useState([])
  const [showFilters, setShowFilters] = useState(false)

  // The URL is the single source of truth for filters, so results can be shared and survive refresh.
  const filters = useMemo(() => Object.fromEntries(FILTER_KEYS.map((k) => [k, params.get(k) || ''])), [params])
  const page = Number(filters.page || 0)

  useEffect(() => {
    categoryService.list().then(setCategories).catch(() => {})
    productService.purities().then(setPurities).catch(() => {})
  }, [])

  const { data, loading, error } = useApi(
    () => productService.search({ ...filters, sort: filters.sort || 'newest', page, size: PAGE_SIZE }),
    [params.toString()],
  )

  const update = (patch) => {
    const next = new URLSearchParams(params)
    Object.entries(patch).forEach(([k, v]) => (v ? next.set(k, v) : next.delete(k)))
    if (!('page' in patch)) next.delete('page')
    setParams(next)
  }
  const clearAll = () => setParams(filters.search ? { search: filters.search } : {})

  const category = categories.find((c) => String(c.id) === filters.categoryId)
  const title = filters.search
    ? `“${filters.search}”`
    : category?.name || (filters.metalType ? metalLabel(filters.metalType) : filters.featured ? 'The Signature Edit' : 'All Jewellery')

  // Removable chips for the active filters.
  const chips = []
  if (category) chips.push({ label: category.name, clear: { categoryId: '' } })
  if (filters.metalType) chips.push({ label: metalLabel(filters.metalType), clear: { metalType: '' } })
  if (filters.purity) chips.push({ label: filters.purity, clear: { purity: '' } })
  if (filters.minPrice || filters.maxPrice) {
    const r = PRICE_RANGES.find((x) => x.min === filters.minPrice && x.max === filters.maxPrice)
    chips.push({ label: r?.label || 'Price', clear: { minPrice: '', maxPrice: '' } })
  }
  if (filters.inStock) chips.push({ label: 'In stock', clear: { inStock: '' } })
  if (filters.featured) chips.push({ label: 'Signature', clear: { featured: '' } })
  if (filters.search) chips.push({ label: `Search: ${filters.search}`, clear: { search: '' } })

  const filterPanel = (
    <ProductFilter filters={filters} categories={categories} purities={purities} onChange={update} onClear={clearAll} />
  )

  return (
    <>
      <PageHeader
        title={title}
        subtitle={category?.description || (filters.search ? 'Search results' : 'Fine jewellery, priced from today’s metal rate')}
        crumbs={[{ label: 'Shop', to: '/products' }, ...(category ? [{ label: category.name }] : [])]}
      />
      <div className="container py-5">
        <div className="row g-5">
          <aside className="col-lg-3 d-none d-lg-block">{filterPanel}</aside>

          <div className="col-lg-9">
            <div className="toolbar">
              <div className="row g-3 align-items-center">
                <div className="col-md-6">
                  <SearchBar key={filters.search} initial={filters.search} onSearch={(q) => update({ search: q })} />
                </div>
                <div className="col-md-6 d-flex gap-2 justify-content-md-end align-items-center">
                  <button className="btn btn-outline-jc btn-sm d-lg-none" onClick={() => setShowFilters(true)}>
                    <i className="bi bi-sliders me-1" /> Filters
                  </button>
                  <label htmlFor="sort" className="small text-muted-jc d-none d-sm-inline text-nowrap">Sort by</label>
                  <select id="sort" className="form-select form-select-sm" style={{ maxWidth: 210 }} value={filters.sort || 'newest'} onChange={(e) => update({ sort: e.target.value })}>
                    {SORT_OPTIONS.map((o) => <option key={o.value} value={o.value}>{o.label}</option>)}
                  </select>
                </div>
              </div>
              <div className="d-flex flex-wrap align-items-center mt-3">
                <span className="small text-muted-jc me-3 mb-2">
                  {data ? `${data.totalElements} ${data.totalElements === 1 ? 'piece' : 'pieces'}` : ' '}
                </span>
                {chips.map((c) => (
                  <span key={c.label} className="chip">
                    {c.label}
                    <button onClick={() => update(c.clear)} aria-label={`Remove ${c.label}`}><i className="bi bi-x" /></button>
                  </span>
                ))}
              </div>
            </div>

            {loading && !data ? (
              <Loading text="Curating pieces" />
            ) : error ? (
              <EmptyState icon="exclamation-circle" title="We couldn’t load the collection" text={error} />
            ) : data.content.length === 0 ? (
              <EmptyState icon="search" title="No pieces match" text="Try removing a filter or searching for something else.">
                <button className="btn btn-outline-jc" onClick={clearAll}>Clear filters</button>
              </EmptyState>
            ) : (
              <>
                <div className="row g-4" style={{ opacity: loading ? 0.5 : 1, transition: 'opacity .2s' }}>
                  {data.content.map((p) => (
                    <div key={p.id} className="col-6 col-md-4"><ProductCard product={p} /></div>
                  ))}
                </div>
                <Pagination
                  className="mt-5"
                  page={data.page}
                  totalPages={data.totalPages}
                  onChange={(p) => {
                    update({ page: String(p) })
                    window.scrollTo({ top: 0, behavior: 'smooth' })
                  }}
                />
              </>
            )}
          </div>
        </div>
      </div>

      <Modal show={showFilters} title="Filters" onClose={() => setShowFilters(false)}>
        {filterPanel}
      </Modal>
    </>
  )
}
