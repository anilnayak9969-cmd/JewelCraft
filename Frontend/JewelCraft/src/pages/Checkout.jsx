import { useCallback, useEffect, useState } from 'react'
import toast from 'react-hot-toast'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import AddressForm from '../components/AddressForm'
import JewelImage from '../components/JewelImage'
import Loading from '../components/Loading'
import Modal from '../components/Modal'
import PageHeader from '../components/PageHeader'
import useAuth from '../hooks/useAuth'
import useCart from '../hooks/useCart'
import { errorMessage } from '../services/api'
import { orderService } from '../services/orderService'
import { addressService } from '../services/userService'
import { formatINR } from '../utils/format'
import { payWithRazorpay } from '../utils/razorpay'

const PAYMENT_OPTIONS = [
  { value: 'RAZORPAY', title: 'Pay online', text: 'UPI, cards, net banking and wallets via Razorpay', icon: 'credit-card-2-front' },
  { value: 'COD', title: 'Cash on delivery', text: 'Pay when your order is delivered', icon: 'cash-coin' },
]

export default function Checkout() {
  const { user } = useAuth()
  const { cart, loading: cartLoading, refresh } = useCart()
  const navigate = useNavigate()

  const [addresses, setAddresses] = useState(null)
  const [addressId, setAddressId] = useState(null)
  const [showAddressForm, setShowAddressForm] = useState(false)
  const [paymentMethod, setPaymentMethod] = useState('RAZORPAY')
  const [couponInput, setCouponInput] = useState('')
  const [coupon, setCoupon] = useState('')
  const [summary, setSummary] = useState(null)
  const [placing, setPlacing] = useState(false)

  useEffect(() => {
    addressService
      .list()
      .then((list) => {
        setAddresses(list)
        setAddressId((list.find((a) => a.defaultAddress) || list[0])?.id ?? null)
        if (list.length === 0) setShowAddressForm(true)
      })
      .catch(() => setAddresses([]))
  }, [])

  // Totals always come from the server (it applies coupon, tax and delivery charge).
  const loadSummary = useCallback((code) => {
    return orderService.preview(code).then((s) => {
      setSummary(s)
      return s
    })
  }, [])

  useEffect(() => {
    if (cart.items.length) loadSummary(coupon).catch(() => {})
  }, [cart, coupon, loadSummary])

  const applyCoupon = async (e) => {
    e.preventDefault()
    const code = couponInput.trim().toUpperCase()
    if (!code) return
    try {
      const s = await loadSummary(code)
      if (s.couponApplied) {
        setCoupon(code)
        toast.success(s.couponMessage || `Coupon ${code} applied`)
      } else {
        toast.error(s.couponMessage || 'This coupon cannot be applied')
        setCoupon('')
        loadSummary('')
      }
    } catch (err) {
      toast.error(errorMessage(err))
    }
  }

  const removeCoupon = () => {
    setCoupon('')
    setCouponInput('')
  }

  const saveAddress = async (values) => {
    const created = await addressService.add(values)
    const list = await addressService.list()
    setAddresses(list)
    setAddressId(created.id)
    setShowAddressForm(false)
    toast.success('Address saved')
  }

  const placeOrder = async () => {
    if (!addressId) {
      toast.error('Please select a delivery address')
      return
    }
    setPlacing(true)
    try {
      const order = await orderService.place({ addressId, paymentMethod, couponCode: coupon || null })
      await refresh()
      if (paymentMethod === 'RAZORPAY') {
        try {
          const result = await payWithRazorpay(order.id)
          if (result === 'paid') toast.success('Payment received – thank you!')
          else toast('Payment not completed. You can pay from your order page.', { icon: 'ℹ️' })
        } catch (err) {
          toast.error(err.response ? errorMessage(err) : err.message)
        }
      } else {
        toast.success('Order placed – thank you!')
      }
      navigate(`/orders/${order.id}`, { replace: true, state: { justPlaced: true } })
    } catch (err) {
      // Same id, so clicking again replaces the message instead of stacking copies.
      toast.error(errorMessage(err), { id: 'place-order' })
    } finally {
      setPlacing(false)
    }
  }

  if (cartLoading && cart.items.length === 0) return <Loading fullPage />
  if (!cartLoading && cart.items.length === 0 && !placing) return <Navigate to="/cart" replace />

  return (
    <>
      <PageHeader title="Checkout" crumbs={[{ label: 'Bag', to: '/cart' }, { label: 'Checkout' }]} />
      <div className="container py-5">
        <div className="row g-5">
          <div className="col-lg-7">
            {/* 1. contact */}
            <div className="step-title"><span className="num">1</span>Contact</div>
            <div className="panel mb-5 d-flex flex-wrap justify-content-between gap-2">
              <div>
                <div className="fw-500">{user.fullName}</div>
                <div className="small text-soft">{user.email} · {user.phone}</div>
              </div>
              <Link to="/profile" className="btn-link-jc align-self-center">Edit</Link>
            </div>

            {/* 2. address */}
            <div className="step-title"><span className="num">2</span>Delivery address</div>
            {addresses === null ? (
              <Loading />
            ) : (
              <div className="mb-5">
                <div className="row g-3">
                  {addresses.map((a) => (
                    <div key={a.id} className="col-md-6">
                      <button type="button" className={`select-card ${addressId === a.id ? 'active' : ''}`} onClick={() => setAddressId(a.id)}>
                        <div className="fw-500 mb-1">
                          {a.fullName}
                          {a.defaultAddress && <span className="status neutral ms-2" style={{ fontSize: '0.58rem' }}>Default</span>}
                        </div>
                        <div className="small text-soft">
                          {a.line1}{a.line2 ? `, ${a.line2}` : ''}<br />
                          {a.city}, {a.state} – {a.pincode}<br />
                          {a.phone}
                        </div>
                      </button>
                    </div>
                  ))}
                  <div className="col-md-6">
                    <button type="button" className="select-card text-center d-flex flex-column align-items-center justify-content-center" style={{ minHeight: 120 }} onClick={() => setShowAddressForm(true)}>
                      <i className="bi bi-plus-lg text-gold" style={{ fontSize: '1.4rem' }} />
                      <span className="small-caps mt-1">Add new address</span>
                    </button>
                  </div>
                </div>
              </div>
            )}

            {/* 3. payment */}
            <div className="step-title"><span className="num">3</span>Payment</div>
            <div className="row g-3 mb-4">
              {PAYMENT_OPTIONS.map((o) => (
                <div key={o.value} className="col-md-6">
                  <button type="button" className={`select-card ${paymentMethod === o.value ? 'active' : ''}`} onClick={() => setPaymentMethod(o.value)}>
                    <i className={`bi bi-${o.icon} text-gold`} style={{ fontSize: '1.4rem' }} />
                    <div className="fw-500 mt-2">{o.title}</div>
                    <div className="small text-soft">{o.text}</div>
                  </button>
                </div>
              ))}
            </div>
          </div>

          {/* summary */}
          <div className="col-lg-5">
            <div className="summary-card">
              <h4 className="mb-3" style={{ fontSize: '1.6rem' }}>Your order</h4>
              {cart.items.map((i) => (
                <div key={i.id} className="d-flex gap-3 align-items-center py-2">
                  <div className="mini-thumb position-relative">
                    <JewelImage src={i.imageUrl} alt={i.productName} name={i.productName} />
                  </div>
                  <div className="flex-grow-1 small">
                    <div className="fw-500">{i.productName}</div>
                    <div className="text-muted-jc">{i.metalDetails} · Qty {i.quantity}</div>
                  </div>
                  <div className="small fw-500">{formatINR(i.lineTotal)}</div>
                </div>
              ))}

              <form onSubmit={applyCoupon} className="d-flex gap-2 my-3 pt-3 border-top">
                <input
                  className="form-control form-control-sm"
                  placeholder="Coupon code"
                  value={couponInput}
                  onChange={(e) => setCouponInput(e.target.value)}
                  disabled={Boolean(coupon)}
                  aria-label="Coupon code"
                />
                {coupon ? (
                  <button type="button" className="btn btn-outline-jc btn-sm" onClick={removeCoupon}>Remove</button>
                ) : (
                  <button className="btn btn-outline-jc btn-sm">Apply</button>
                )}
              </form>

              {summary ? (
                <>
                  <div className="row-line"><span>Subtotal</span><span>{formatINR(summary.subtotal)}</span></div>
                  {Number(summary.discount) > 0 && (
                    <div className="row-line" style={{ color: 'var(--jc-good)' }}>
                      <span>Discount{summary.couponCode ? ` (${summary.couponCode})` : ''}</span>
                      <span>− {formatINR(summary.discount)}</span>
                    </div>
                  )}
                  <div className="row-line"><span>GST</span><span>{formatINR(summary.tax)}</span></div>
                  <div className="row-line">
                    <span>Delivery</span>
                    <span>{Number(summary.deliveryCharge) > 0 ? formatINR(summary.deliveryCharge) : 'Complimentary'}</span>
                  </div>
                  <div className="row-line grand"><span>Total</span><span>{formatINR(summary.total)}</span></div>
                </>
              ) : (
                <Loading text="Calculating" />
              )}

              <button className="btn btn-jc w-100 mt-4" onClick={placeOrder} disabled={placing || !summary || !addressId}>
                {placing ? 'Placing order…' : paymentMethod === 'RAZORPAY' ? `Pay ${summary ? formatINR(summary.total) : ''}` : 'Place order'}
              </button>
              <p className="small text-muted-jc text-center mt-3 mb-0">
                <i className="bi bi-lock me-1" />Prices are confirmed by our server when you place the order.
              </p>
            </div>
          </div>
        </div>
      </div>

      <Modal show={showAddressForm} title="New delivery address" size="lg" onClose={() => setShowAddressForm(false)}>
        <AddressForm
          initial={{ fullName: user.fullName, phone: user.phone, defaultAddress: (addresses || []).length === 0 }}
          onSubmit={saveAddress}
          onCancel={() => setShowAddressForm(false)}
          submitText="Use this address"
        />
      </Modal>
    </>
  )
}
