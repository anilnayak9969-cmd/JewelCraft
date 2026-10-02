import { createContext, useCallback, useEffect, useMemo, useState } from 'react'
import toast from 'react-hot-toast'
import { useLocation, useNavigate } from 'react-router-dom'
import useAuth from '../hooks/useAuth'
import { errorMessage } from '../services/api'
import { cartService } from '../services/cartService'

export const CartContext = createContext(null)

const EMPTY_CART = { items: [], totalItems: 0, subtotal: 0, tax: 0, total: 0 }

export function CartProvider({ children }) {
  const { user } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const isCustomer = user?.role === 'CUSTOMER'
  const [cart, setCart] = useState(EMPTY_CART)
  const [loading, setLoading] = useState(false)

  const refresh = useCallback(() => {
    if (!isCustomer) return Promise.resolve()
    setLoading(true)
    return cartService
      .get()
      .then(setCart)
      .catch(() => setCart(EMPTY_CART))
      .finally(() => setLoading(false))
  }, [isCustomer])

  useEffect(() => {
    if (isCustomer) refresh()
    else setCart(EMPTY_CART)
  }, [isCustomer, user?.id, refresh])

  /** Guests are sent to sign in; staff accounts cannot shop. */
  const requireCustomer = useCallback(() => {
    if (!user) {
      toast('Please sign in to continue', { icon: '✦' })
      navigate('/login', { state: { from: location.pathname } })
      return false
    }
    if (!isCustomer) {
      toast.error('Shopping is available for customer accounts only')
      return false
    }
    return true
  }, [user, isCustomer, navigate, location.pathname])

  const run = useCallback(async (action, success) => {
    try {
      const updated = await action()
      setCart(updated)
      if (success) toast.success(success)
      return true
    } catch (err) {
      toast.error(errorMessage(err))
      return false
    }
  }, [])

  const addItem = useCallback(
    (productId, quantity = 1) =>
      requireCustomer() ? run(() => cartService.add(productId, quantity), 'Added to your bag') : Promise.resolve(false),
    [requireCustomer, run],
  )
  const updateItem = useCallback((itemId, quantity) => run(() => cartService.update(itemId, quantity)), [run])
  const removeItem = useCallback((itemId) => run(() => cartService.remove(itemId), 'Removed from your bag'), [run])
  const clearCart = useCallback(() => run(() => cartService.clear()), [run])

  const value = useMemo(
    () => ({
      cart,
      count: cart.totalItems || 0,
      loading,
      refresh,
      addItem,
      updateItem,
      removeItem,
      clearCart,
      requireCustomer,
    }),
    [cart, loading, refresh, addItem, updateItem, removeItem, clearCart, requireCustomer],
  )

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>
}
