import axios from 'axios'

const TOKEN_KEY = 'jc_token'

/** Empty in development (Vite proxies /api); the backend URL in production. */
export const API_BASE = (import.meta.env.VITE_API_URL || '').replace(/\/$/, '')

const api = axios.create({ baseURL: `${API_BASE}/api` })

export const tokenStore = {
  get: () => localStorage.getItem(TOKEN_KEY),
  set: (token) => localStorage.setItem(TOKEN_KEY, token),
  clear: () => localStorage.removeItem(TOKEN_KEY),
}

api.interceptors.request.use((config) => {
  const token = tokenStore.get()
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// An expired or invalid token logs the user out everywhere (AuthContext listens for this event).
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && tokenStore.get()) {
      tokenStore.clear()
      window.dispatchEvent(new Event('jc:logout'))
    }
    return Promise.reject(error)
  },
)

/** Turns any API error into one readable sentence. */
export function errorMessage(error, fallback = 'Something went wrong. Please try again.') {
  if (!error?.response) return 'Cannot reach the server. Is the backend running?'
  const data = error.response.data
  // The Vite dev proxy answers 500/502/504 with an empty body when Spring Boot is not running.
  if (error.response.status >= 500 && !data?.message) {
    return 'The server is not responding. Make sure the Spring Boot backend is running on port 8080.'
  }
  // A bare 403 (e.g. "Invalid CORS request") has no JSON message of its own.
  if (error.response.status === 403 && !data?.message) {
    return 'The server refused this request (403). Check that this site’s address is allowed in the backend CORS settings.'
  }
  if (data?.errors) {
    const first = Object.values(data.errors)[0]
    if (first) return first
  }
  return data?.message || fallback
}

/** Copies field errors from a 400 validation response onto a React Hook Form. */
export function applyServerErrors(error, setError) {
  const errors = error?.response?.data?.errors
  if (!errors) return false
  Object.entries(errors).forEach(([field, message]) => setError(field, { type: 'server', message }))
  return true
}

/** Uploaded images come back as /uploads/...; prefix the backend URL when it lives elsewhere. */
export function imageUrl(url) {
  if (!url) return null
  if (/^(https?:|data:|blob:)/.test(url)) return url
  return `${API_BASE}${url}`
}

export default api
