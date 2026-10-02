import { paymentService } from '../services/orderService'

const SCRIPT_URL = 'https://checkout.razorpay.com/v1/checkout.js'

function loadScript() {
  if (window.Razorpay) return Promise.resolve(true)
  return new Promise((resolve) => {
    const s = document.createElement('script')
    s.src = SCRIPT_URL
    s.onload = () => resolve(true)
    s.onerror = () => resolve(false)
    document.body.appendChild(s)
  })
}

/**
 * Opens the Razorpay popup for an order that was placed with RAZORPAY.
 * Resolves 'paid' (verified by the server), 'dismissed' (popup closed), or throws on failure.
 */
export async function payWithRazorpay(orderId) {
  const loaded = await loadScript()
  if (!loaded) throw new Error('Could not load Razorpay. Check your internet connection.')

  const rp = await paymentService.createRazorpayOrder(orderId)

  return new Promise((resolve, reject) => {
    const checkout = new window.Razorpay({
      key: rp.keyId,
      order_id: rp.razorpayOrderId,
      amount: rp.amount,
      currency: rp.currency,
      name: 'JewelCraft',
      description: `Order ${rp.orderNumber}`,
      prefill: { name: rp.customerName, email: rp.customerEmail, contact: rp.customerPhone },
      theme: { color: '#14110f' },
      handler: (response) => {
        // The server checks the signature – the payment only counts once this succeeds.
        paymentService
          .verifyRazorpay({
            orderId: rp.orderId,
            razorpayOrderId: response.razorpay_order_id,
            razorpayPaymentId: response.razorpay_payment_id,
            razorpaySignature: response.razorpay_signature,
          })
          .then(() => resolve('paid'))
          .catch(reject)
      },
      modal: { ondismiss: () => resolve('dismissed') },
    })
    checkout.on('payment.failed', (res) => reject(new Error(res.error?.description || 'Payment failed')))
    checkout.open()
  })
}
