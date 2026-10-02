import api from './api'

/** Sign-up URL for each role (see AuthController). */
const REGISTER_PATHS = {
  CUSTOMER: '/auth/register',
  STAFF: '/auth/register/staff',
  STORE_MANAGER: '/auth/register/store-manager',
  DELIVERY_PARTNER: '/auth/register/delivery-partner',
  ADMIN: '/auth/register/admin',
}

const authService = {
  /** Creates an unverified account and emails a 6-digit code. */
  register: (role, data) => api.post(REGISTER_PATHS[role], data).then((r) => r.data),
  /** Customers get { auth } back (signed in); staff roles get { requiresApproval: true }. */
  verifyEmail: (email, otp) => api.post('/auth/verify-email', { email, otp }).then((r) => r.data),
  resendOtp: (email) => api.post('/auth/resend-otp', { email }).then((r) => r.data),
  login: (data) => api.post('/auth/login', data).then((r) => r.data),
  logout: () => api.post('/auth/logout').catch(() => null),
  me: () => api.get('/auth/me').then((r) => r.data),
}

export default authService
