const inr0 = new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 })
const inr2 = new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', minimumFractionDigits: 2 })
const num = new Intl.NumberFormat('en-IN')

/** ₹58,234 – whole rupees for cards and totals. */
export const formatINR = (v) => inr0.format(Number(v || 0))
/** ₹58,234.50 – exact amounts for price breakdowns. */
export const formatINRExact = (v) => inr2.format(Number(v || 0))
export const formatNumber = (v) => num.format(Number(v || 0))

/** Short rupee figure for chart axes: ₹1.2L, ₹3.4Cr. */
export function formatINRShort(v) {
  const n = Number(v || 0)
  if (n >= 1e7) return `₹${(n / 1e7).toFixed(1)}Cr`
  if (n >= 1e5) return `₹${(n / 1e5).toFixed(1)}L`
  if (n >= 1e3) return `₹${(n / 1e3).toFixed(0)}K`
  return `₹${n}`
}

export const formatDate = (v) =>
  v ? new Date(v).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' }) : '—'

export const formatDateTime = (v) =>
  v
    ? new Date(v).toLocaleString('en-IN', {
        day: 'numeric', month: 'short', year: 'numeric', hour: 'numeric', minute: '2-digit',
      })
    : '—'

/** OUT_FOR_DELIVERY -> "Out for delivery" */
export function humanize(value) {
  if (!value) return ''
  const s = String(value).replace(/_/g, ' ').toLowerCase()
  return s.charAt(0).toUpperCase() + s.slice(1)
}

const METAL_LABELS = {
  GOLD: 'Yellow Gold', ROSE_GOLD: 'Rose Gold', WHITE_GOLD: 'White Gold', SILVER: 'Sterling Silver', PLATINUM: 'Platinum',
}
export const metalLabel = (m) => METAL_LABELS[m] || humanize(m)

export const initials = (name = '') =>
  name.split(' ').filter(Boolean).slice(0, 2).map((p) => p[0].toUpperCase()).join('')

/** Today as yyyy-mm-dd in local time (for date inputs). */
export function isoDate(d = new Date()) {
  const off = d.getTimezoneOffset()
  return new Date(d.getTime() - off * 60000).toISOString().slice(0, 10)
}
