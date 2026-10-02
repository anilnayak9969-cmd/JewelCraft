import api from './api'
import { cleanParams } from './productService'

const data = (r) => r.data

export const orderService = {
  // customer
  preview: (couponCode) => api.post('/orders/preview', { couponCode: couponCode || null }).then(data),
  place: (body) => api.post('/orders', body).then(data),
  mine: (page = 0, size = 10) => api.get('/orders', { params: { page, size } }).then(data),
  get: (id) => api.get(`/orders/${id}`).then(data),
  /** Live tracking history (polled by the order page). */
  tracking: (id) => api.get(`/orders/${id}/tracking`).then(data),
  cancel: (id, reason) => api.post(`/orders/${id}/cancel`, { reason }).then(data),

  // store team
  search: (params) => api.get('/orders/manage', { params: cleanParams(params) }).then(data),
  updateStatus: (id, status, note) => api.put(`/orders/${id}/status`, { status, note }).then(data),
  assignDelivery: (id, deliveryPartnerId) =>
    api.put(`/orders/${id}/assign-delivery`, { deliveryPartnerId }).then(data),
  /** A store message in the tracking history that doesn't change the status. */
  postUpdate: (id, message) => api.post(`/orders/${id}/updates`, { message }).then(data),
}

export const paymentService = {
  createRazorpayOrder: (orderId) => api.post(`/payments/razorpay/order/${orderId}`).then(data),
  verifyRazorpay: (body) => api.post('/payments/razorpay/verify', body).then(data),
  search: (params) => api.get('/payments', { params: cleanParams(params) }).then(data),
  updateStatus: (id, status) => api.put(`/payments/${id}/status`, { status }).then(data),
}

export const deliveryService = {
  dashboard: () => api.get('/delivery/dashboard').then(data),
  assigned: () => api.get('/delivery/orders').then(data),
  history: () => api.get('/delivery/history').then(data),
  accept: (deliveryId) => api.put(`/delivery/${deliveryId}/accept`).then(data),
  updateStatus: (deliveryId, status, notes) =>
    api.put(`/delivery/${deliveryId}/status`, { status, notes }).then(data),
}

export default orderService
