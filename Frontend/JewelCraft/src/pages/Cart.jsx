import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import EmptyState from '../components/EmptyState'
import JewelImage from '../components/JewelImage'
import Loading from '../components/Loading'
import { ConfirmModal } from '../components/Modal'
import PageHeader from '../components/PageHeader'
import useAuth from '../hooks/useAuth'
import useCart from '../hooks/useCart'
import { formatINR } from '../utils/format'

export default function Cart() {
  const { user } = useAuth()
  const { cart, loading, updateItem, removeItem, clearCart } = useCart()
  const [busyId, setBusyId] = useState(null)
  const [confirmClear, setConfirmClear] = useState(false)
  const navigate = useNavigate()

  const header = <PageHeader title="Shopping Bag" crumbs={[{ label: 'Bag' }]} />

  if (!user) {
    return (
      <>
        {header}
        <EmptyState icon="bag" title="Your bag is waiting" text="Sign in to see the pieces you’ve added and check out securely.">
          <Link to="/login" state={{ from: '/cart' }} className="btn btn-jc">Sign in</Link>
        </EmptyState>
      </>
    )
  }
  if (user.role !== 'CUSTOMER') {
    return (
      <>
        {header}
        <EmptyState icon="person-badge" title="Staff accounts can’t shop" text="Sign in with a customer account to use the shopping bag." />
      </>
    )
  }
  if (loading && cart.items.length === 0) return <>{header}<Loading /></>

  const unavailable = cart.items.some((i) => !i.available)

  const change = async (item, quantity) => {
    setBusyId(item.id)
    await updateItem(item.id, quantity)
    setBusyId(null)
  }
  const remove = async (item) => {
    setBusyId(item.id)
    await removeItem(item.id)
    setBusyId(null)
  }

  return (
    <>
      {header}
      <div className="container py-5">
        {cart.items.length === 0 ? (
          <EmptyState icon="bag" title="Your bag is empty" text="Discover pieces crafted to be treasured.">
            <Link to="/products" className="btn btn-jc">Explore the collection</Link>
          </EmptyState>
        ) : (
          <div className="row g-5">
            <div className="col-lg-8">
              <div className="d-flex justify-content-between align-items-center border-bottom pb-3">
                <span className="small-caps">{cart.totalItems} item{cart.totalItems > 1 ? 's' : ''}</span>
                <button className="btn-link-jc" onClick={() => setConfirmClear(true)}>Clear bag</button>
              </div>

              {cart.items.map((item) => (
                <div key={item.id} className="line-item" style={{ opacity: busyId === item.id ? 0.5 : 1 }}>
                  <Link to={`/products/${item.productId}`} className="thumb">
                    <JewelImage src={item.imageUrl} alt={item.productName} name={item.productName} />
                  </Link>
                  <div>
                    <Link to={`/products/${item.productId}`} className="serif d-block" style={{ fontSize: '1.3rem', lineHeight: 1.2 }}>
                      {item.productName}
                    </Link>
                    <div className="small text-soft mt-1">{item.metalDetails}</div>
                    <div className="small mt-1">{formatINR(item.finalPrice)} each</div>
                    {!item.available && (
                      <div className="small mt-1" style={{ color: 'var(--jc-bad)' }}>
                        <i className="bi bi-exclamation-circle me-1" />
                        {item.stockQuantity > 0 ? `Only ${item.stockQuantity} in stock` : 'No longer available'}
                      </div>
                    )}
                  </div>
                  <div className="li-end text-end">
                    <div className="qty mb-2">
                      <button onClick={() => change(item, item.quantity - 1)} disabled={item.quantity <= 1 || busyId === item.id} aria-label="Decrease">−</button>
                      <span>{item.quantity}</span>
                      <button
                        onClick={() => change(item, item.quantity + 1)}
                        disabled={item.quantity >= Math.min(10, item.stockQuantity) || busyId === item.id}
                        aria-label="Increase"
                      >
                        +
                      </button>
                    </div>
                    <div className="fw-500">{formatINR(item.lineTotal)}</div>
                    <button className="btn-link-jc mt-2" style={{ fontSize: '0.65rem' }} onClick={() => remove(item)} disabled={busyId === item.id}>
                      Remove
                    </button>
                  </div>
                </div>
              ))}

              <Link to="/products" className="btn-link-jc d-inline-block mt-4">
                <i className="bi bi-arrow-left me-1" /> Continue shopping
              </Link>
            </div>

            <div className="col-lg-4">
              <div className="summary-card">
                <h4 className="mb-3" style={{ fontSize: '1.6rem' }}>Order summary</h4>
                <div className="row-line"><span>Subtotal</span><span>{formatINR(cart.subtotal)}</span></div>
                <div className="row-line"><span>GST</span><span>{formatINR(cart.tax)}</span></div>
                <div className="row-line text-muted-jc small"><span>Delivery &amp; offers</span><span>at checkout</span></div>
                <div className="row-line grand"><span>Total</span><span>{formatINR(cart.total)}</span></div>
                {unavailable && (
                  <p className="small mt-3 mb-0" style={{ color: 'var(--jc-bad)' }}>
                    Please update the highlighted items before checking out.
                  </p>
                )}
                <button className="btn btn-jc w-100 mt-4" disabled={unavailable} onClick={() => navigate('/checkout')}>
                  Proceed to checkout
                </button>
                <div className="assurance justify-content-center mt-3">
                  <span><i className="bi bi-shield-lock" />Secure checkout</span>
                  <span><i className="bi bi-truck" />Insured delivery</span>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>

      <ConfirmModal
        show={confirmClear}
        title="Clear your bag?"
        message="All items will be removed from your shopping bag."
        confirmText="Clear bag"
        onClose={() => setConfirmClear(false)}
        onConfirm={async () => {
          await clearCart()
          setConfirmClear(false)
        }}
      />
    </>
  )
}
