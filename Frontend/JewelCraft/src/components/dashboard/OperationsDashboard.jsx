import { Link } from 'react-router-dom'
import useApi from '../../hooks/useApi'
import useAuth from '../../hooks/useAuth'
import { formatINR, formatNumber, humanize } from '../../utils/format'
import JewelImage from '../JewelImage'
import Loading from '../Loading'
import { BarChart, ChartCard } from './Charts'
import RecentOrders from './RecentOrders'
import StatCard, { DashHeader } from './StatCard'

/**
 * Day-to-day store dashboard. Store managers see sales (showSales);
 * staff get the same operational numbers without financial figures.
 */
export default function OperationsDashboard({ fetcher, basePath, showSales, quickLinks = [] }) {
  const { user } = useAuth()
  const { data: d, loading, error } = useApi(fetcher, [])

  if (loading && !d) return <Loading />
  if (error) return <div className="card-jc p-4" style={{ color: 'var(--jc-bad)' }}>{error}</div>

  const byStatus = d.ordersByStatus || []
  const lowStock = d.lowStockItems || []

  return (
    <>
      <DashHeader title={`Welcome, ${user.firstName}`} subtitle="Today at the JewelCraft store." />

      <div className="row g-3 mb-4">
        <div className="col-6 col-xl-2"><StatCard label="Today's orders" value={formatNumber(d.todaysOrders)} icon="calendar-check" /></div>
        <div className="col-6 col-xl-2"><StatCard label="Pending" value={formatNumber(d.pendingOrders)} icon="hourglass-split" hint={`${formatNumber(d.processingOrders)} in preparation`} /></div>
        <div className="col-6 col-xl-2"><StatCard label="Products" value={formatNumber(d.totalProducts)} icon="gem" /></div>
        <div className="col-6 col-xl-2"><StatCard label="Units in stock" value={formatNumber(d.availableStock)} icon="boxes" /></div>
        <div className="col-6 col-xl-2"><StatCard label="Low stock" value={formatNumber(d.lowStockCount)} icon="exclamation-triangle" /></div>
        {showSales ? (
          <div className="col-6 col-xl-2"><StatCard label="Store sales" value={formatINR(d.totalStoreSales)} icon="currency-rupee" /></div>
        ) : (
          <div className="col-6 col-xl-2"><StatCard label="Open requests" value={formatNumber(d.openRequests)} icon="chat-square-text" /></div>
        )}
      </div>

      {quickLinks.length > 0 && (
        <div className="d-flex flex-wrap gap-2 mb-4">
          {quickLinks.map((q) => (
            <Link key={q.to} to={q.to} className="btn btn-outline-jc btn-sm"><i className={`bi bi-${q.icon} me-1`} />{q.label}</Link>
          ))}
        </div>
      )}

      <div className="row g-4">
        <div className="col-xl-5">
          <ChartCard title="Orders by status" rows={byStatus.map((x) => ({ label: humanize(x.label), value: formatNumber(x.count) }))} valueLabel="Orders">
            <BarChart horizontal labels={byStatus.map((x) => humanize(x.label))} values={byStatus.map((x) => x.count)} label="Orders" />
          </ChartCard>
        </div>
        <div className="col-xl-7">
          <div className="card-jc h-100">
            <div className="card-jc-head">
              <h5>Low stock</h5>
              <Link to={`${basePath}/inventory`} className="btn-link-jc">Manage inventory</Link>
            </div>
            <div className="table-responsive">
              <table className="table table-jc">
                <thead><tr><th>Product</th><th>SKU</th><th className="text-end">In stock</th><th className="text-end">Alert at</th></tr></thead>
                <tbody>
                  {lowStock.length === 0 ? (
                    <tr><td colSpan={4} className="text-center text-muted-jc py-4"><i className="bi bi-check2-circle me-1" />All products are well stocked.</td></tr>
                  ) : (
                    lowStock.map((i) => (
                      <tr key={i.productId}>
                        <td>
                          <div className="d-flex align-items-center gap-2">
                            <div className="thumb-sm" style={{ width: 36, height: 40 }}><JewelImage src={i.imageUrl} alt="" category={i.categoryName} name={i.productName} /></div>
                            {i.productName}
                          </div>
                        </td>
                        <td className="small text-muted-jc">{i.sku}</td>
                        <td className="text-end fw-500" style={{ color: i.quantity === 0 ? 'var(--jc-bad)' : 'var(--jc-warn)' }}>{i.quantity}</td>
                        <td className="text-end">{i.lowStockThreshold}</td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>
        <div className="col-12">
          <RecentOrders orders={d.recentOrders || []} manageLink={`${basePath}/orders`} />
        </div>
      </div>
    </>
  )
}
