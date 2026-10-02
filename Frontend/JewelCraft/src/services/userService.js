import api from './api'
import { cleanParams } from './productService'

const data = (r) => r.data

export const userService = {
  // whoever is logged in
  profile: () => api.get('/users/me').then(data),
  updateProfile: (body) => api.put('/users/me', body).then(data),
  changePassword: (body) => api.put('/users/me/password', body).then(data),

  // admin: every kind of user
  search: (params) => api.get('/admin/users', { params: cleanParams(params) }).then(data),
  get: (id) => api.get(`/admin/users/${id}`).then(data),
  create: (body) => api.post('/admin/users', body).then(data),
  update: (id, body) => api.put(`/admin/users/${id}`, body).then(data),
  setEnabled: (id, active) => api.patch(`/admin/users/${id}/status`, { active }).then(data),
  remove: (id) => api.delete(`/admin/users/${id}`),
  pendingCount: () => api.get('/admin/users/pending-count').then((r) => r.data.count),
  /** status: APPROVED | REJECTED; note is emailed to the applicant. */
  decideApproval: (id, status, note) => api.put(`/admin/users/${id}/approval`, { status, note }).then(data),

  // store: drop-down when assigning a delivery
  deliveryPartners: () => api.get('/store/delivery-partners').then(data),
}

export const addressService = {
  list: () => api.get('/addresses').then(data),
  add: (body) => api.post('/addresses', body).then(data),
  update: (id, body) => api.put(`/addresses/${id}`, body).then(data),
  setDefault: (id) => api.put(`/addresses/${id}/default`).then(data),
  remove: (id) => api.delete(`/addresses/${id}`),
}

/** Customer support tickets (returns, exchanges, customisation, queries). */
export const requestService = {
  create: (body) => api.post('/requests', body).then(data),
  mine: () => api.get('/requests/my').then(data),
  search: (params) => api.get('/requests', { params: cleanParams(params) }).then(data),
  update: (id, body) => api.put(`/requests/${id}`, body).then(data),
}

export default userService
