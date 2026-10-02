import { DashHeader } from '../components/dashboard/StatCard'
import DeliveryCard from '../components/DeliveryCard'
import EmptyState from '../components/EmptyState'
import LiveBadge from '../components/LiveBadge'
import Loading from '../components/Loading'
import useApi from '../hooks/useApi'
import usePolling from '../hooks/usePolling'
import { deliveryService } from '../services/orderService'

export default function AssignedOrders() {
  const { data, loading, error, setData, reload } = useApi(deliveryService.assigned, [])
  const { lastUpdated } = usePolling(reload, 20000)

  // Finished deliveries drop off this list; they appear in Delivery History.
  const onUpdated = (o) =>
    setData((list) =>
      ['DELIVERED', 'FAILED'].includes(o.delivery?.status) ? list.filter((x) => x.id !== o.id) : list.map((x) => (x.id === o.id ? o : x)),
    )

  return (
    <>
      <DashHeader title="Assigned Orders" subtitle="Accept new assignments and update each delivery as you go.">
        <LiveBadge lastUpdated={lastUpdated} />
        <button className="btn btn-outline-jc btn-sm" onClick={reload}><i className="bi bi-arrow-clockwise me-1" />Refresh</button>
      </DashHeader>
      {loading && !data ? (
        <Loading />
      ) : error ? (
        <div className="card-jc p-4" style={{ color: 'var(--jc-bad)' }}>{error}</div>
      ) : data.length === 0 ? (
        <EmptyState icon="truck" title="No active deliveries" text="New assignments from the store will appear here." />
      ) : (
        <div className="row g-4">
          {data.map((o) => (
            <div key={o.id} className="col-md-6 col-xxl-4"><DeliveryCard order={o} onUpdated={onUpdated} /></div>
          ))}
        </div>
      )}
    </>
  )
}
