import { BarChart, ChartCard, LineChart } from '../components/dashboard/Charts'
import RecentOrders from '../components/dashboard/RecentOrders'
import StatCard, { DashHeader } from '../components/dashboard/StatCard'
import { Link } from 'react-router-dom'
import Loading from '../components/Loading'
import useApi from '../hooks/useApi'
import useAuth from '../hooks/useAuth'
import { dashboardService } from '../services/dashboardService'
import { userService } from '../services/userService'
import { ROLE_LABELS } from '../utils/constants'
import { formatINR, formatINRShort, formatNumber, humanize } from '../utils/format'

export default function AdminDashboard() {
  const { user } = useAuth()
  const { data: d, loading, error } = useApi(dashboardService.admin, [])
  const { data: pending } = useApi(userService.pendingCount, [])

  if (loading && !d) return <Loading />
  if (error) return <div className="card-jc p-4" style={{ color: 'var(--jc-bad)' }}>{error}</div>

  const list = (k) => d[k] || []
  const monthly = list('monthlySales')

  return (
    <>
      <DashHeader title={`Good day, ${user.firstName}`} subtitle="Here’s how JewelCraft is performing." />

      {pending > 0 && (
        <div className="notice warn mb-4">
          <i className="bi bi-person-exclamation" />
          <div>
            {pending} new {pending === 1 ? 'application' : 'applications'} (staff, store manager, delivery partner or admin){pending === 1 ? 'is' : 'are'} waiting for approval.{' '}
            <Link to="/admin/users" className="text-gold fw-500">Review now</Link>
          </div>
        </div>
      )}

      <div className="row g-3 mb-4">
        <div className="col-6 col-xl-3"><StatCard label="Total revenue" value={formatINR(d.totalRevenue)} icon="currency-rupee" hint="Delivered & paid orders" /></div>
        <div className="col-6 col-xl-3"><StatCard label="Total orders" value={formatNumber(d.totalOrders)} icon="receipt" /></div>
        <div className="col-6 col-xl-3"><StatCard label="Total users" value={formatNumber(d.totalUsers)} icon="people" hint={`${formatNumber(d.totalCustomers)} customers`} /></div>
        <div className="col-6 col-xl-3"><StatCard label="Products" value={formatNumber(d.totalProducts)} icon="gem" /></div>
        <div className="col-6 col-xl-3"><StatCard label="Pending orders" value={formatNumber(d.pendingOrders)} icon="hourglass-split" /></div>
        <div className="col-6 col-xl-3"><StatCard label="Low stock" value={formatNumber(d.lowStockProducts)} icon="exclamation-triangle" /></div>
        <div className="col-6 col-xl-3"><StatCard label="Delivered" value={formatNumber(d.deliveredOrders)} icon="check2-circle" /></div>
        <div className="col-6 col-xl-3"><StatCard label="Cancelled" value={formatNumber(d.cancelledOrders)} icon="x-circle" hint={`${formatNumber(d.openRequests)} open customer requests`} /></div>
      </div>

      <div className="row g-4">
        <div className="col-xl-8">
          <ChartCard title="Monthly sales" subtitle="Sales amount, last months" rows={monthly.map((x) => ({ label: x.label, value: formatINR(x.amount) }))} valueLabel="Sales">
            <LineChart labels={monthly.map((x) => x.label)} values={monthly.map((x) => Number(x.amount))} label="Sales" format={formatINRShort} />
          </ChartCard>
        </div>
        <div className="col-xl-4">
          <ChartCard title="Orders by month" rows={monthly.map((x) => ({ label: x.label, value: formatNumber(x.count) }))} valueLabel="Orders">
            <BarChart labels={monthly.map((x) => x.label)} values={monthly.map((x) => x.count)} label="Orders" />
          </ChartCard>
        </div>
        <div className="col-lg-4">
          <ChartCard title="Order status" rows={list('ordersByStatus').map((x) => ({ label: humanize(x.label), value: formatNumber(x.count) }))} valueLabel="Orders">
            <BarChart horizontal labels={list('ordersByStatus').map((x) => humanize(x.label))} values={list('ordersByStatus').map((x) => x.count)} label="Orders" />
          </ChartCard>
        </div>
        <div className="col-lg-4">
          <ChartCard title="Products by category" rows={list('productsByCategory').map((x) => ({ label: x.label, value: formatNumber(x.count) }))} valueLabel="Products">
            <BarChart horizontal labels={list('productsByCategory').map((x) => x.label)} values={list('productsByCategory').map((x) => x.count)} label="Products" />
          </ChartCard>
        </div>
        <div className="col-lg-4">
          <ChartCard title="Users by role" rows={list('usersByRole').map((x) => ({ label: ROLE_LABELS[x.label] || humanize(x.label), value: formatNumber(x.count) }))} valueLabel="Users">
            <BarChart horizontal labels={list('usersByRole').map((x) => ROLE_LABELS[x.label] || humanize(x.label))} values={list('usersByRole').map((x) => x.count)} label="Users" />
          </ChartCard>
        </div>
        <div className="col-12">
          <RecentOrders orders={list('recentOrders')} manageLink="/admin/orders" />
        </div>
      </div>
    </>
  )
}
