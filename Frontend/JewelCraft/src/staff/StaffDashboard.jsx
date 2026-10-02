import OperationsDashboard from '../components/dashboard/OperationsDashboard'
import { dashboardService } from '../services/dashboardService'

const QUICK_LINKS = [
  { to: '/staff/products', label: 'Products', icon: 'gem' },
  { to: '/staff/inventory', label: 'Update stock', icon: 'boxes' },
  { to: '/staff/orders', label: 'Orders', icon: 'receipt' },
  { to: '/staff/requests', label: 'Customer requests', icon: 'chat-square-text' },
]

/** Same operational view as the store dashboard, without sales figures. */
export default function StaffDashboard() {
  return <OperationsDashboard fetcher={dashboardService.staff} basePath="/staff" quickLinks={QUICK_LINKS} />
}
