import { Link } from 'react-router-dom'
import EmptyState from '../components/EmptyState'
import Loading from '../components/Loading'
import PageHeader from '../components/PageHeader'
import ProductCard from '../components/ProductCard'
import useWishlist from '../hooks/useWishlist'

export default function Wishlist() {
  const { items, loading } = useWishlist()

  return (
    <>
      <PageHeader title="Wishlist" subtitle="The pieces you’ve fallen for" crumbs={[{ label: 'Wishlist' }]} />
      <div className="container py-5">
        {loading && items.length === 0 ? (
          <Loading />
        ) : items.length === 0 ? (
          <EmptyState icon="heart" title="Your wishlist is empty" text="Tap the heart on any piece to save it here.">
            <Link to="/products" className="btn btn-jc">Discover jewellery</Link>
          </EmptyState>
        ) : (
          <div className="row g-4">
            {items.map((i) => (
              <div key={i.id} className="col-6 col-md-4 col-lg-3"><ProductCard product={i.product} /></div>
            ))}
          </div>
        )}
      </div>
    </>
  )
}
