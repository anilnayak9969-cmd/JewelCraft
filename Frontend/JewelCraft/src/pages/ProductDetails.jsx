import { useEffect, useState } from 'react'
import { Controller, useForm } from 'react-hook-form'
import toast from 'react-hot-toast'
import { Link, useParams } from 'react-router-dom'
import EmptyState from '../components/EmptyState'
import JewelImage from '../components/JewelImage'
import Loading from '../components/Loading'
import ProductCard from '../components/ProductCard'
import Stars, { StarInput } from '../components/Stars'
import useApi from '../hooks/useApi'
import useAuth from '../hooks/useAuth'
import useCart from '../hooks/useCart'
import useWishlist from '../hooks/useWishlist'
import { applyServerErrors, errorMessage } from '../services/api'
import { productService, reviewService } from '../services/productService'
import { formatDate, formatINR, formatINRExact, metalLabel } from '../utils/format'
import { priceBeforeDiscount } from '../utils/pricing'

function ReviewForm({ productId, onAdded }) {
  const {
    register,
    control,
    handleSubmit,
    reset,
    setError,
    formState: { errors, isSubmitting },
  } = useForm({ defaultValues: { rating: 5, title: '', comment: '' } })

  const submit = async (values) => {
    try {
      const review = await reviewService.add(productId, values)
      toast.success('Thank you for your review')
      reset()
      onAdded(review)
    } catch (err) {
      if (!applyServerErrors(err, setError)) toast.error(errorMessage(err))
    }
  }

  return (
    <form onSubmit={handleSubmit(submit)} className="panel" noValidate>
      <h4 className="mb-3" style={{ fontSize: '1.5rem' }}>Write a review</h4>
      <label className="form-label d-block">Your rating</label>
      <Controller
        name="rating"
        control={control}
        rules={{ min: { value: 1, message: 'Please choose a rating' } }}
        render={({ field }) => <StarInput value={field.value} onChange={field.onChange} />}
      />
      <div className="mt-3">
        <label className="form-label" htmlFor="r-title">Title <span className="text-muted-jc">(optional)</span></label>
        <input id="r-title" className={`form-control ${errors.title ? 'is-invalid' : ''}`} {...register('title', { maxLength: { value: 100, message: 'Title must be at most 100 characters' } })} />
        <div className="invalid-feedback">{errors.title?.message}</div>
      </div>
      <div className="mt-3">
        <label className="form-label" htmlFor="r-comment">Your review</label>
        <textarea
          id="r-comment"
          rows={4}
          className={`form-control ${errors.comment ? 'is-invalid' : ''}`}
          {...register('comment', {
            required: 'Please write a short review',
            minLength: { value: 5, message: 'Review must be 5-1000 characters' },
            maxLength: { value: 1000, message: 'Review must be 5-1000 characters' },
          })}
        />
        <div className="invalid-feedback">{errors.comment?.message}</div>
      </div>
      <button className="btn btn-jc mt-3" disabled={isSubmitting}>{isSubmitting ? 'Submitting…' : 'Submit review'}</button>
    </form>
  )
}

export default function ProductDetails() {
  const { id } = useParams()
  const { user } = useAuth()
  const { addItem } = useCart()
  const { toggle, isWished } = useWishlist()
  const { data: p, loading, error } = useApi(() => productService.get(id), [id])
  const [activeImage, setActiveImage] = useState(0)
  const [qty, setQty] = useState(1)
  const [reviews, setReviews] = useState({ content: [], page: 0, last: true, totalElements: 0 })
  const [related, setRelated] = useState([])
  const [adding, setAdding] = useState(false)

  useEffect(() => {
    setActiveImage(0)
    setQty(1)
    reviewService.forProduct(id).then(setReviews).catch(() => {})
  }, [id])

  useEffect(() => {
    if (!p?.categoryId) return
    productService
      .search({ categoryId: p.categoryId, size: 5 })
      .then((res) => setRelated(res.content.filter((x) => x.id !== p.id).slice(0, 4)))
      .catch(() => setRelated([]))
  }, [p?.categoryId, p?.id])

  if (loading) return <Loading fullPage text="Unveiling" />
  if (error || !p) {
    return (
      <EmptyState icon="gem" title="This piece could not be found" text={error}>
        <Link to="/products" className="btn btn-jc">Back to the collection</Link>
      </EmptyState>
    )
  }

  const images = p.images?.length ? [...p.images].sort((a, b) => Number(b.mainImage) - Number(a.mainImage)) : []
  const current = images[activeImage]?.imageUrl || p.imageUrl
  const was = priceBeforeDiscount(p)
  const maxQty = Math.min(10, p.stockQuantity)
  const wished = isWished(p.id)
  const isCustomer = user?.role === 'CUSTOMER'

  const add = async () => {
    setAdding(true)
    await addItem(p.id, qty)
    setAdding(false)
  }

  const loadMoreReviews = () =>
    reviewService.forProduct(id, reviews.page + 1).then((next) =>
      setReviews({ ...next, content: [...reviews.content, ...next.content] }),
    )

  return (
    <>
      <div className="container pt-4">
        <div className="crumbs">
          <Link to="/">Home</Link><span className="mx-2">/</span>
          <Link to="/products">Shop</Link><span className="mx-2">/</span>
          <Link to={`/products?categoryId=${p.categoryId}`}>{p.categoryName}</Link><span className="mx-2">/</span>
          {p.name}
        </div>
      </div>

      <section className="container py-4 py-lg-5">
        <div className="row g-5">
          {/* gallery */}
          <div className="col-lg-7">
            <div className="d-flex flex-column-reverse flex-md-row gap-3">
              {images.length > 1 && (
                <div className="gallery-thumbs d-flex flex-md-column gap-2">
                  {images.map((img, i) => (
                    <button key={img.id} className={i === activeImage ? 'active' : ''} onClick={() => setActiveImage(i)} aria-label={`Image ${i + 1}`}>
                      <JewelImage src={img.imageUrl} alt="" metal={p.metalType} category={p.categoryName} name={p.name} />
                    </button>
                  ))}
                </div>
              )}
              <div className="gallery-main flex-grow-1">
                <JewelImage src={current} alt={p.name} metal={p.metalType} category={p.categoryName} name={p.name} />
              </div>
            </div>
          </div>

          {/* info */}
          <div className="col-lg-5">
            <div className="eyebrow">{p.categoryName}</div>
            <h1 className="mt-2 mb-2" style={{ fontSize: 'clamp(2rem, 3.4vw, 2.9rem)' }}>{p.name}</h1>
            <div className="d-flex align-items-center gap-2 small text-soft mb-3">
              <Stars value={p.averageRating || 0} />
              <a href="#reviews" className="text-soft">
                {p.reviewCount ? `${Number(p.averageRating).toFixed(1)} · ${p.reviewCount} review${p.reviewCount > 1 ? 's' : ''}` : 'No reviews yet'}
              </a>
              {p.sku && <span className="ms-auto text-muted-jc">SKU {p.sku}</span>}
            </div>

            <div className="d-flex align-items-baseline gap-3">
              <span className="price-lg">{formatINR(p.finalPrice)}</span>
              {was && <span className="text-muted-jc text-decoration-line-through">{formatINR(was)}</span>}
            </div>
            <div className="small text-muted-jc mb-4">Inclusive of {Number(p.tax)}% GST · price based on today&apos;s {metalLabel(p.metalType).toLowerCase()} rate</div>

            <div className="d-flex flex-wrap gap-2 mb-4">
              <span className="chip mb-0">{metalLabel(p.metalType)}</span>
              <span className="chip mb-0">{p.purity}</span>
              <span className="chip mb-0">{Number(p.weight)} g</span>
            </div>

            {p.inStock ? (
              <p className="small mb-3" style={{ color: 'var(--jc-good)' }}>
                <i className="bi bi-check-circle me-1" />
                {p.stockQuantity <= (p.lowStockThreshold || 3) ? `Only ${p.stockQuantity} left – order soon` : 'In stock, ready to ship'}
              </p>
            ) : (
              <p className="small mb-3" style={{ color: 'var(--jc-bad)' }}><i className="bi bi-x-circle me-1" />Currently sold out</p>
            )}

            <div className="d-flex flex-wrap gap-2 mb-4">
              {p.inStock && (
                <div className="qty" aria-label="Quantity">
                  <button onClick={() => setQty((q) => Math.max(1, q - 1))} disabled={qty <= 1} aria-label="Decrease">−</button>
                  <span>{qty}</span>
                  <button onClick={() => setQty((q) => Math.min(maxQty, q + 1))} disabled={qty >= maxQty} aria-label="Increase">+</button>
                </div>
              )}
              <button className="btn btn-jc flex-grow-1" onClick={add} disabled={!p.inStock || adding}>
                {p.inStock ? (adding ? 'Adding…' : 'Add to bag') : 'Sold out'}
              </button>
              <button className="btn btn-outline-jc" onClick={() => toggle(p.id)} aria-pressed={wished} aria-label="Wishlist">
                <i className={`bi ${wished ? 'bi-heart-fill text-danger' : 'bi-heart'}`} />
              </button>
            </div>

            <div className="assurance mb-4">
              <span><i className="bi bi-patch-check" />Hallmarked</span>
              <span><i className="bi bi-truck" />Insured shipping</span>
              <span><i className="bi bi-arrow-repeat" />Easy exchange</span>
            </div>

            <h6 className="small-caps mb-2">Price breakdown</h6>
            <div className="breakdown mb-4">
              <div className="row-line"><span>{metalLabel(p.metalType)} {p.purity} · {Number(p.weight)} g × {formatINRExact(p.metalPrice)}/g</span><span>{formatINRExact(p.metalValue)}</span></div>
              <div className="row-line"><span>Making charges</span><span>{formatINRExact(p.makingCharges)}</span></div>
              {Number(p.stoneCharges) > 0 && <div className="row-line"><span>Stone charges</span><span>{formatINRExact(p.stoneCharges)}</span></div>}
              {Number(p.discount) > 0 && <div className="row-line" style={{ color: 'var(--jc-good)' }}><span>Discount</span><span>− {formatINRExact(p.discount)}</span></div>}
              <div className="row-line"><span>GST ({Number(p.tax)}%)</span><span>{formatINRExact(p.taxAmount)}</span></div>
              <div className="row-line total"><span>Final price</span><span>{formatINRExact(p.finalPrice)}</span></div>
            </div>

            <h6 className="small-caps mb-2">Details</h6>
            {p.description && <p className="text-soft">{p.description}</p>}
            <table className="w-100 spec-table">
              <tbody>
                <tr><td>Metal</td><td>{metalLabel(p.metalType)}</td></tr>
                <tr><td>Purity</td><td>{p.purity}</td></tr>
                <tr><td>Gross weight</td><td>{Number(p.weight)} g</td></tr>
                <tr><td>Category</td><td>{p.categoryName}</td></tr>
                {p.sku && <tr><td>Product code</td><td>{p.sku}</td></tr>}
              </tbody>
            </table>
          </div>
        </div>
      </section>

      {/* reviews */}
      <section id="reviews" className="section-sm bg-cream">
        <div className="container">
          <div className="row g-5">
            <div className="col-lg-4">
              <div className="eyebrow">Reviews</div>
              <h2 className="section-title mt-2">What our customers say</h2>
              <div className="d-flex align-items-center gap-3 mt-3">
                <span className="display-serif" style={{ fontSize: '3.2rem' }}>{p.reviewCount ? Number(p.averageRating).toFixed(1) : '—'}</span>
                <div>
                  <Stars value={p.averageRating || 0} size="1.1rem" />
                  <div className="small text-muted-jc">{reviews.totalElements} review{reviews.totalElements === 1 ? '' : 's'}</div>
                </div>
              </div>
              <div className="mt-4">
                {isCustomer ? (
                  <ReviewForm
                    productId={p.id}
                    onAdded={(r) => setReviews((prev) => ({ ...prev, content: [r, ...prev.content], totalElements: prev.totalElements + 1 }))}
                  />
                ) : !user ? (
                  <p className="small text-soft"><Link to="/login" state={{ from: `/products/${p.id}` }} className="text-gold">Sign in</Link> to write a review.</p>
                ) : null}
              </div>
            </div>
            <div className="col-lg-8">
              {reviews.content.length === 0 ? (
                <p className="text-soft">No reviews yet – be the first to share your thoughts.</p>
              ) : (
                <>
                  {reviews.content.map((r) => (
                    <div key={r.id} className="py-4 border-bottom">
                      <div className="d-flex justify-content-between">
                        <Stars value={r.rating} />
                        <span className="small text-muted-jc">{formatDate(r.createdAt)}</span>
                      </div>
                      {r.title && <h5 className="serif mt-2 mb-1" style={{ fontSize: '1.25rem' }}>{r.title}</h5>}
                      <p className="mb-1 text-soft">{r.comment}</p>
                      <div className="small fw-500">{r.userName}</div>
                    </div>
                  ))}
                  {!reviews.last && <button className="btn btn-outline-jc btn-sm mt-4" onClick={loadMoreReviews}>Load more reviews</button>}
                </>
              )}
            </div>
          </div>
        </div>
      </section>

      {related.length > 0 && (
        <section className="section">
          <div className="container">
            <div className="text-center mb-5">
              <div className="eyebrow">You may also like</div>
              <h2 className="section-title mt-2">From the same collection</h2>
            </div>
            <div className="row g-4">
              {related.map((r) => <div key={r.id} className="col-6 col-lg-3"><ProductCard product={r} /></div>)}
            </div>
          </div>
        </section>
      )}
    </>
  )
}
