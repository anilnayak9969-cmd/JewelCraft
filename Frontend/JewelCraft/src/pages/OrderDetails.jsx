import { useEffect, useRef, useState } from 'react'
import toast from 'react-hot-toast'
import { Link, useLocation, useParams } from 'react-router-dom'
import EmptyState from '../components/EmptyState'
import JewelImage from '../components/JewelImage'
import LiveBadge from '../components/LiveBadge'
import Loading from '../components/Loading'
import Modal from '../components/Modal'
import OrderTimeline from '../components/OrderTimeline'
import RequestForm from '../components/RequestForm'
import StatusBadge from '../components/StatusBadge'
import TrackingHistory from '../components/TrackingHistory'
import useApi from '../hooks/useApi'
import usePolling from '../hooks/usePolling'
import { errorMessage } from '../services/api'
import { orderService } from '../services/orderService'
import { formatDateTime, formatINR, humanize } from '../utils/format'
import { payWithRazorpay } from '../utils/razorpay'

const FINAL = ['DELIVERED', 'CANCELLED']
const LIVE_INTERVAL_MS = 10000

export default function OrderDetails() {
  const { id } = useParams()
  const justPlaced = useLocation().state?.justPlaced
  const { data: order, loading, error, setData } = useApi(() => orderService.get(id), [id])
  const [tracking, setTracking] = useState([])
  const [showCancel, setShowCancel] = useState(false)
  const [showHelp, setShowHelp] = useState(false)
  const [reason, setReason] = useState('')
  const [busy, setBusy] = useState(false)
  const lastStatus = useRef(null)

  const isLive = Boolean(order) && !FINAL.includes(order.status)

  // Live tracking: refresh the order and its history every few seconds until it is delivered or cancelled.
  const refresh = async () => {
    const [o, t] = await Promise.all([orderService.get(id), orderService.tracking(id)])
    setData(o)
    setTracking(t)
  }
  const { lastUpdated } = usePolling(refresh, LIVE_INTERVAL_MS, isLive)

  useEffect(() => {
    orderService.tracking(id).then(setTracking).catch(() => setTracking([]))
  }, [id])

  // Tell the customer when the status changes while they are watching.
  useEffect(() => {
    if (!order) return
    if (lastStatus.current && lastStatus.current !== order.status) {
      toast(`Your order is now ${humanize(order.status).toLowerCase()}`, { icon: '✨' })
    }
    lastStatus.current = order.status
  }, [order])

  if (loading && !order) return <Loading fullPage />
  if (error || !order) {
    return (
      <EmptyState icon="receipt" title="Order not found" text={error}>
        <Link to="/orders" className="btn btn-jc">Back to my orders</Link>
      </EmptyState>
    )
  }

  const payment = order.payment
  const paymentPending = payment?.method === 'RAZORPAY' && payment.status !== 'SUCCESS' && order.status !== 'CANCELLED'
  const a = order.shippingAddress

  const cancel = async () => {
    setBusy(true)
    try {
      await orderService.cancel(order.id, reason || null)
      await refresh()
      toast.success('Your order has been cancelled')
      setShowCancel(false)
    } catch (err) {
      toast.error(errorMessage(err))
    } finally {
      setBusy(false)
    }
  }

  const payNow = async () => {
    setBusy(true)
    try {
      const result = await payWithRazorpay(order.id)
      if (result === 'paid') toast.success('Payment received – thank you!')
      await refresh()
    } catch (err) {
      toast.error(err.response ? errorMessage(err) : err.message)
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="container py-5" style={{ maxWidth: 1080 }}>
      {justPlaced && order.status !== 'CANCELLED' && (
        <div className="text-center mb-5 fade-up">
          <i className="bi bi-stars text-gold" style={{ fontSize: '2rem' }} />
          <h1 className="mt-2" style={{ fontSize: '2.6rem' }}>Thank you, {order.customerName?.split(' ')[0]}</h1>
          <p className="text-soft">Your order {order.orderNumber} has been placed. We’ll keep you updated at every step.</p>
        </div>
      )}

      <div className="d-flex flex-wrap justify-content-between align-items-end gap-3 mb-3">
        <div>
          <div className="crumbs"><Link to="/orders">My orders</Link><span className="mx-2">/</span>{order.orderNumber}</div>
          <h2 className="mt-1 mb-0" style={{ fontSize: '2rem' }}>Order {order.orderNumber}</h2>
          <div className="small text-muted-jc">Placed on {formatDateTime(order.createdAt)}</div>
        </div>
        <div className="d-flex flex-wrap gap-2">
          {paymentPending && <button className="btn btn-gold btn-sm" onClick={payNow} disabled={busy}>Pay {formatINR(order.totalAmount)} now</button>}
          {order.cancellable && <button className="btn btn-outline-jc btn-sm" onClick={() => setShowCancel(true)}>Cancel order</button>}
          <button className="btn btn-outline-jc btn-sm" onClick={() => setShowHelp(true)}>Need help?</button>
        </div>
      </div>

      <div className="panel mb-4">
        <div className="d-flex justify-content-between align-items-center flex-wrap gap-2">
          <span className="small-caps">Order status</span>
          <div className="d-flex align-items-center gap-3">
            <LiveBadge lastUpdated={lastUpdated} active={isLive} finalLabel="Tracking complete" />
            <StatusBadge status={order.status} />
          </div>
        </div>
        <OrderTimeline status={order.status} />
        {order.cancelReason && <p className="small text-soft mb-0 mt-2">Reason: {order.cancelReason}</p>}
        {order.status === 'OUT_FOR_DELIVERY' && order.delivery && (
          <div className="notice mt-3">
            <i className="bi bi-geo-alt" />
            <div>
              <b>{order.delivery.partnerName}</b> is on the way with your order.
              {order.delivery.partnerPhone && <> Call <a href={`tel:${order.delivery.partnerPhone}`} className="text-gold">{order.delivery.partnerPhone}</a></>}
            </div>
          </div>
        )}
      </div>

      <div className="panel mb-4">
        <div className="d-flex justify-content-between align-items-center mb-3">
          <span className="small-caps">Tracking history</span>
          <span className="small text-muted-jc">{tracking.length} update{tracking.length === 1 ? '' : 's'}</span>
        </div>
        <TrackingHistory events={tracking} />
      </div>

      <div className="row g-4">
        <div className="col-lg-7">
          <div className="panel h-100">
            <h5 className="small-caps mb-3">Items</h5>
            {order.items.map((i) => (
              <div key={i.id} className="d-flex gap-3 align-items-center py-3 border-bottom">
                <Link to={`/products/${i.productId}`} className="mini-thumb" style={{ width: 72, height: 84 }}>
                  <JewelImage src={i.productImage} alt={i.productName} name={i.productName} />
                </Link>
                <div className="flex-grow-1">
                  <Link to={`/products/${i.productId}`} className="serif" style={{ fontSize: '1.2rem' }}>{i.productName}</Link>
                  <div className="small text-muted-jc">{i.metalDetails}</div>
                  <div className="small">Qty {i.quantity} × {formatINR(i.unitPrice)} <span className="text-muted-jc">+ {Number(i.taxRate)}% GST</span></div>
                  {order.status === 'DELIVERED' && (
                    <Link to={`/products/${i.productId}#reviews`} className="small text-gold">Write a review</Link>
                  )}
                </div>
                <div className="fw-500">{formatINR(i.lineTotal)}</div>
              </div>
            ))}
            <div className="mt-3" style={{ maxWidth: 360, marginLeft: 'auto' }}>
              <div className="d-flex justify-content-between small py-1"><span>Subtotal</span><span>{formatINR(order.subtotal)}</span></div>
              {Number(order.discount) > 0 && (
                <div className="d-flex justify-content-between small py-1" style={{ color: 'var(--jc-good)' }}>
                  <span>Discount{order.couponCode ? ` (${order.couponCode})` : ''}</span><span>− {formatINR(order.discount)}</span>
                </div>
              )}
              <div className="d-flex justify-content-between small py-1"><span>GST</span><span>{formatINR(order.tax)}</span></div>
              <div className="d-flex justify-content-between small py-1"><span>Delivery</span><span>{Number(order.deliveryCharge) > 0 ? formatINR(order.deliveryCharge) : 'Complimentary'}</span></div>
              <div className="d-flex justify-content-between fw-500 pt-2 mt-1 border-top"><span>Total</span><span>{formatINR(order.totalAmount)}</span></div>
            </div>
          </div>
        </div>

        <div className="col-lg-5 d-flex flex-column gap-4">
          <div className="panel">
            <h5 className="small-caps mb-3">Delivery address</h5>
            <div className="fw-500">{a.fullName}</div>
            <div className="small text-soft">
              {a.line1}{a.line2 ? `, ${a.line2}` : ''}<br />{a.city}, {a.state} – {a.pincode}<br />{a.phone}
            </div>
          </div>
          <div className="panel">
            <h5 className="small-caps mb-3">Payment</h5>
            <div className="d-flex justify-content-between align-items-center">
              <span>{payment?.method === 'COD' ? 'Cash on delivery' : 'Online (Razorpay)'}</span>
              {payment && <StatusBadge status={payment.status} />}
            </div>
            {payment?.transactionId && <div className="small text-muted-jc mt-2">Transaction {payment.transactionId}</div>}
            {payment?.paidAt && <div className="small text-muted-jc">Paid {formatDateTime(payment.paidAt)}</div>}
          </div>
          {order.delivery && (
            <div className="panel">
              <h5 className="small-caps mb-3">Delivery</h5>
              <div className="d-flex justify-content-between align-items-center">
                <span>{order.delivery.partnerName}</span>
                <StatusBadge status={order.delivery.status} />
              </div>
              {order.delivery.partnerPhone && <div className="small text-muted-jc mt-1">{order.delivery.partnerPhone}</div>}
              {order.delivery.deliveredAt && <div className="small text-muted-jc">Delivered {formatDateTime(order.delivery.deliveredAt)}</div>}
              {order.delivery.notes && <div className="small text-soft mt-2">{order.delivery.notes}</div>}
            </div>
          )}
        </div>
      </div>

      <Modal
        show={showCancel}
        title="Cancel this order?"
        onClose={() => setShowCancel(false)}
        footer={
          <>
            <button className="btn btn-outline-jc btn-sm" onClick={() => setShowCancel(false)} disabled={busy}>Keep order</button>
            <button className="btn btn-jc btn-sm" onClick={cancel} disabled={busy}>{busy ? 'Cancelling…' : 'Cancel order'}</button>
          </>
        }
      >
        <p className="text-soft">
          {payment?.status === 'SUCCESS' ? 'Your online payment will be refunded to the original method.' : `Order ${order.orderNumber} will be cancelled.`}
        </p>
        <label className="form-label" htmlFor="cancel-reason">Reason <span className="text-muted-jc">(optional)</span></label>
        <textarea id="cancel-reason" className="form-control" rows={3} maxLength={500} value={reason} onChange={(e) => setReason(e.target.value)} />
      </Modal>

      <Modal show={showHelp} title="How can we help?" size="lg" onClose={() => setShowHelp(false)}>
        <p className="small text-soft">About order {order.orderNumber} · status {humanize(order.status)}</p>
        <RequestForm orderId={order.id} onDone={() => setShowHelp(false)} onCancel={() => setShowHelp(false)} />
      </Modal>
    </div>
  )
}
