import { Link } from 'react-router-dom'
import useCart from '../hooks/useCart'
import useWishlist from '../hooks/useWishlist'
import { formatINR, metalLabel } from '../utils/format'
import { priceBeforeDiscount } from '../utils/pricing'
import JewelImage from './JewelImage'

export default function ProductCard({ product: p }) {
  const { addItem } = useCart()
  const { toggle, isWished } = useWishlist()
  const wished = isWished(p.id)
  const was = priceBeforeDiscount(p)
  const lowStock = p.inStock && p.stockQuantity <= (p.lowStockThreshold || 3)

  return (
    <article className="p-card">
      <div className="p-media">
        <Link to={`/products/${p.id}`} aria-label={p.name}>
          <JewelImage src={p.imageUrl} alt={p.name} metal={p.metalType} category={p.categoryName} name={p.name} />
        </Link>
        <div className="p-badges">
          {/* At most two badges, most important first, so the card stays calm. */}
          {[
            !p.inStock && <span key="out" className="p-badge dark">Sold out</span>,
            was && p.inStock && <span key="save" className="p-badge gold">Save {formatINR(was - p.finalPrice)}</span>,
            lowStock && <span key="low" className="p-badge">Only {p.stockQuantity} left</span>,
            p.featured && p.inStock && <span key="sig" className="p-badge">Signature</span>,
          ]
            .filter(Boolean)
            .slice(0, 2)}
        </div>
        <button
          type="button"
          className={`p-wish ${wished ? 'on' : ''}`}
          onClick={() => toggle(p.id)}
          aria-label={wished ? 'Remove from wishlist' : 'Add to wishlist'}
          aria-pressed={wished}
        >
          <i className={`bi ${wished ? 'bi-heart-fill' : 'bi-heart'}`} />
        </button>
        {p.inStock && (
          <div className="p-add">
            <button type="button" className="btn btn-jc btn-sm w-100" onClick={() => addItem(p.id, 1)}>
              Add to bag
            </button>
          </div>
        )}
      </div>
      <div className="p-body">
        <div className="p-cat">{p.categoryName}</div>
        <Link to={`/products/${p.id}`} className="p-name">{p.name}</Link>
        <div className="p-meta">
          {metalLabel(p.metalType)} · {p.purity} · {Number(p.weight)} g
        </div>
        <div className="p-price">
          {formatINR(p.finalPrice)}
          {was && <span className="was">{formatINR(was)}</span>}
        </div>
      </div>
    </article>
  )
}
