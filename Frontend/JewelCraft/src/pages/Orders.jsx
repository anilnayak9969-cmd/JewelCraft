import { useState } from 'react'
import { Link } from 'react-router-dom'
import EmptyState from '../components/EmptyState'
import JewelImage from '../components/JewelImage'
import LiveBadge from '../components/LiveBadge'
import Loading from '../components/Loading'
import PageHeader from '../components/PageHeader'
import Pagination from '../components/Pagination'
import StatusBadge from '../components/StatusBadge'
import useApi from '../hooks/useApi'
import usePolling from '../hooks/usePolling'
import { orderService } from '../services/orderService'
import { formatDate, formatINR } from '../utils/format'

export default function Orders() {
  const [page, setPage] = useState(0)
  const { data, loading, error, reload } = useApi(() => orderService.mine(page, 8), [page])
  // Keep statuses live while any order is still on its way.
  const hasActive = Boolean(data?.content.some((o) => !['DELIVERED', 'CANCELLED'].includes(o.status)))
  const { lastUpdated } = usePolling(reload, 20000, hasActive)

  return (
    <>
      <PageHeader title="My Orders" subtitle="Track, manage and review your purchases" crumbs={[{ label: 'Orders' }]} />
      <div className="container py-5" style={{ maxWidth: 980 }}>
        {loading && !data ? (
          <Loading />
        ) : error ? (
          <EmptyState icon="exclamation-circle" title="We couldn’t load your orders" text={error} />
        ) : data.content.length === 0 ? (
          <EmptyState icon="receipt" title="No orders yet" text="When you place an order it will appear here.">
            <Link to="/products" className="btn btn-jc">Start shopping</Link>
          </EmptyState>
        ) : (
          <>
            {hasActive && (
              <div className="d-flex justify-content-end mb-3"><LiveBadge lastUpdated={lastUpdated} /></div>
            )}
            <div className="d-flex flex-column gap-4">
              {data.content.map((o) => (
                <div key={o.id} className="order-card">
                  <div className="oc-head">
                    <div><span>Order</span>{o.orderNumber}</div>
                    <div><span>Placed</span>{formatDate(o.createdAt)}</div>
                    <div><span>Total</span>{formatINR(o.totalAmount)}</div>
                    <div className="ms-md-auto align-self-center"><StatusBadge status={o.status} /></div>
                  </div>
                  <div className="p-3 p-md-4 d-flex flex-wrap align-items-center gap-3">
                    <div className="d-flex gap-2 flex-wrap">
                      {o.items.slice(0, 4).map((i) => (
                        <div key={i.id} className="mini-thumb" title={i.productName}>
                          <JewelImage src={i.productImage} alt={i.productName} name={i.productName} />
                        </div>
                      ))}
                    </div>
                    <div className="flex-grow-1 small">
                      <div className="fw-500">{o.items[0]?.productName}{o.items.length > 1 ? ` + ${o.items.length - 1} more` : ''}</div>
                      <div className="text-muted-jc">
                        {o.totalItems} item{o.totalItems > 1 ? 's' : ''} · {o.payment?.method === 'COD' ? 'Cash on delivery' : 'Paid online'}
                        {o.payment?.method === 'RAZORPAY' && o.payment.status === 'PENDING' && o.status !== 'CANCELLED' && (
                          <span style={{ color: 'var(--jc-warn)' }}> · payment pending</span>
                        )}
                      </div>
                    </div>
                    <Link to={`/orders/${o.id}`} className="btn btn-outline-jc btn-sm">View details</Link>
                  </div>
                </div>
              ))}
            </div>
            <Pagination className="mt-5" page={data.page} totalPages={data.totalPages} onChange={setPage} />
          </>
        )}
      </div>
    </>
  )
}
