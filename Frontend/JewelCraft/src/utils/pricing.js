/** Price before the discount was taken off (tax included), shown struck through as a "was" price. */
export function priceBeforeDiscount(p) {
  if (!p?.discount || Number(p.discount) <= 0) return null
  return Number(p.subtotal) * (1 + Number(p.tax || 0) / 100)
}

/**
 * Same formula as the backend's PricingService – used ONLY to preview a price while a form
 * is being filled in. The server always recalculates the real price.
 */
export function estimatePrice({ weight, metalPrice, makingCharges, stoneCharges, discount, tax }) {
  const n = (v) => Number(v) || 0
  const metalValue = n(weight) * n(metalPrice)
  const subtotal = metalValue + n(makingCharges) + n(stoneCharges)
  const discounted = Math.max(0, subtotal - n(discount))
  const taxAmount = (discounted * n(tax)) / 100
  return { metalValue, subtotal, discounted, taxAmount, finalPrice: discounted + taxAmount }
}
