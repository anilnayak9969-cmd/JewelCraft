import { Link } from 'react-router-dom'
import StatCard, { DashHeader } from '../components/dashboard/StatCard'
import DeliveryCard from '../components/DeliveryCard'
import Loading from '../components/Loading'
import useApi from '../hooks/useApi'
import useAuth from '../hooks/useAuth'
import { deliveryService } from '../services/orderService'
import { formatNumber } from '../utils/format'

export default function DeliveryDashboard() {
  const { user } = useAuth()
  const stats = useApi(deliveryService.dashboard, [])
  const active = useApi(deliveryService.assigned, [])

  if (stats.loading && !stats.data) return <Loading />
  const s = stats.data || {}

  return (
    <>
      <DashHeader title={`Hello, ${user.firstName}`} subtitle="Your deliveries at a glance." />
      <div className="row g-3 mb-4">
        <div className="col-6 col-xl"><StatCard label="New assignments" value={formatNumber(s.newAssignments)} icon="bell" /></div>
        <div className="col-6 col-xl"><StatCard label="In progress" value={formatNumber(s.inProgress)} icon="truck" /></div>
        <div className="col-6 col-xl"><StatCard label="Delivered today" value={formatNumber(s.deliveredToday)} icon="check2-circle" /></div>
        <div className="col-6 col-xl"><StatCard label="Total delivered" value={formatNumber(s.totalDelivered)} icon="award" /></div>
        <div className="col-12 col-xl"><StatCard label="Failed" value={formatNumber(s.failed)} icon="exclamation-octagon" /></div>
      </div>

      <div className="d-flex justify-content-between align-items-center mb-3">
        <h4 className="mb-0" style={{ fontSize: '1.6rem' }}>Up next</h4>
        <Link to="/delivery/orders" className="btn-link-jc">All assigned orders</Link>
      </div>
      {active.data?.length ? (
        <div className="row g-4">
          {active.data.slice(0, 3).map((o) => (
            <div key={o.id} className="col-md-6 col-xxl-4">
              <DeliveryCard
                order={o}
                onUpdated={() => {
                  active.reload()
                  stats.reload()
                }}
              />
            </div>
          ))}
        </div>
      ) : (
        <div className="card-jc p-4 text-muted-jc">{active.loading ? 'Loading…' : 'No active deliveries right now.'}</div>
      )}
    </>
  )
}
