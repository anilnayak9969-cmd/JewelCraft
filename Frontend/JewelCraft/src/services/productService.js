import api from './api'

const data = (r) => r.data

/** Drops empty filter values so the URL stays clean (?search=&metalType= ...). */
export function cleanParams(params = {}) {
  return Object.fromEntries(
    Object.entries(params).filter(([, v]) => v !== undefined && v !== null && v !== ''),
  )
}

export const productService = {
  // shop
  search: (params) => api.get('/products', { params: cleanParams(params) }).then(data),
  get: (id) => api.get(`/products/${id}`).then(data),
  featured: () => api.get('/products/featured').then(data),
  latest: () => api.get('/products/latest').then(data),
  purities: () => api.get('/products/purities').then(data),

  // management (staff / store manager / admin)
  searchAll: (params) => api.get('/products/manage', { params: cleanParams(params) }).then(data),
  getForManagement: (id) => api.get(`/products/manage/${id}`).then(data),
  create: (body) => api.post('/products', body).then(data),
  update: (id, body) => api.put(`/products/${id}`, body).then(data),
  updatePricing: (id, body) => api.put(`/products/${id}/pricing`, body).then(data),
  setAvailability: (id, active) => api.patch(`/products/${id}/availability`, { active }).then(data),
  remove: (id) => api.delete(`/products/${id}`),

  // images
  uploadImages: (id, files) => {
    const form = new FormData()
    Array.from(files).forEach((f) => form.append('files', f))
    return api.post(`/products/${id}/images`, form).then(data)
  },
  addImageUrl: (id, imageUrl) => api.post(`/products/${id}/images/url`, { imageUrl }).then(data),
  replaceImage: (id, imageId, file) => {
    const form = new FormData()
    form.append('file', file)
    return api.put(`/products/${id}/images/${imageId}`, form).then(data)
  },
  setMainImage: (id, imageId) => api.put(`/products/${id}/images/${imageId}/main`).then(data),
  deleteImage: (id, imageId) => api.delete(`/products/${id}/images/${imageId}`).then(data),
}

export const categoryService = {
  list: (includeInactive = false) => api.get('/categories', { params: { includeInactive } }).then(data),
  create: (body) => api.post('/categories', body).then(data),
  update: (id, body) => api.put(`/categories/${id}`, body).then(data),
  remove: (id) => api.delete(`/categories/${id}`),
}

export const reviewService = {
  forProduct: (productId, page = 0, size = 10) =>
    api.get(`/products/${productId}/reviews`, { params: { page, size } }).then(data),
  add: (productId, body) => api.post(`/products/${productId}/reviews`, body).then(data),
  latest: () => api.get('/reviews/latest').then(data),
  mine: () => api.get('/reviews/my').then(data),
  update: (id, body) => api.put(`/reviews/${id}`, body).then(data),
  remove: (id) => api.delete(`/reviews/${id}`),
}

export const metalRateService = {
  list: () => api.get('/metal-rates').then(data),
  create: (body) => api.post('/metal-rates', body).then(data),
  update: (id, pricePerGram) => api.put(`/metal-rates/${id}`, { pricePerGram }).then(data),
  remove: (id) => api.delete(`/metal-rates/${id}`),
}

export const offerService = {
  active: () => api.get('/offers/active').then(data),
  all: () => api.get('/offers').then(data),
  create: (body) => api.post('/offers', body).then(data),
  update: (id, body) => api.put(`/offers/${id}`, body).then(data),
  remove: (id) => api.delete(`/offers/${id}`),
}

export default productService
