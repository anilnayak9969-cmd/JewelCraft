import api from './api'

const data = (r) => r.data

export const cartService = {
  get: () => api.get('/cart').then(data),
  add: (productId, quantity = 1) => api.post('/cart/items', { productId, quantity }).then(data),
  update: (itemId, quantity) => api.put(`/cart/items/${itemId}`, { quantity }).then(data),
  remove: (itemId) => api.delete(`/cart/items/${itemId}`).then(data),
  clear: () => api.delete('/cart/clear').then(data),
}

export const wishlistService = {
  get: () => api.get('/wishlist').then(data),
  add: (productId) => api.post(`/wishlist/${productId}`).then(data),
  remove: (productId) => api.delete(`/wishlist/${productId}`).then(data),
}

export default cartService
