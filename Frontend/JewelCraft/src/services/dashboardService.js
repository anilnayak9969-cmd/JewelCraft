import api from './api'
import { cleanParams } from './productService'

const data = (r) => r.data

export const dashboardService = {
  admin: () => api.get('/admin/dashboard').then(data),
  store: () => api.get('/store/dashboard').then(data),
  staff: () => api.get('/staff/dashboard').then(data),
  /** Admins get the financial report; store managers the store-level one. */
  report: (scope, from, to) =>
    api.get(scope === 'admin' ? '/admin/reports' : '/store/reports', { params: cleanParams({ from, to }) }).then(data),
}

export const inventoryService = {
  list: (params) => api.get('/inventory', { params: cleanParams(params) }).then(data),
  lowStock: () => api.get('/inventory/low-stock').then(data),
  update: (productId, body) => api.put(`/inventory/${productId}`, body).then(data),
}

export default dashboardService
