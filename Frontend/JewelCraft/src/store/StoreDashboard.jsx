import OperationsDashboard from '../components/dashboard/OperationsDashboard'
import { dashboardService } from '../services/dashboardService'

const QUICK_LINKS = [
  { to: '/store/products', label: 'Product management', icon: 'gem' },
  { to: '/store/pricing', label: 'Price management', icon: 'currency-rupee' },
  { to: '/store/inventory', label: 'Inventory', icon: 'boxes' },
  { to: '/store/orders', label: 'Orders', icon: 'receipt' },
  { to: '/store/requests', label: 'Customer requests', icon: 'chat-square-text' },
]

export default function StoreDashboard() {
  return <OperationsDashboard fetcher={dashboardService.store} basePath="/store" showSales quickLinks={QUICK_LINKS} />
}
