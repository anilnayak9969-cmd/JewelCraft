import { createContext, useCallback, useEffect, useMemo, useState } from 'react'
import toast from 'react-hot-toast'
import useAuth from '../hooks/useAuth'
import useCart from '../hooks/useCart'
import { errorMessage } from '../services/api'
import { wishlistService } from '../services/cartService'

export const WishlistContext = createContext(null)

export function WishlistProvider({ children }) {
  const { user } = useAuth()
  const { requireCustomer } = useCart()
  const isCustomer = user?.role === 'CUSTOMER'
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (!isCustomer) {
      setItems([])
      return
    }
    setLoading(true)
    wishlistService
      .get()
      .then(setItems)
      .catch(() => setItems([]))
      .finally(() => setLoading(false))
  }, [isCustomer, user?.id])

  const ids = useMemo(() => new Set(items.map((i) => i.product.id)), [items])

  const toggle = useCallback(
    async (productId) => {
      if (!requireCustomer()) return
      const saved = ids.has(productId)
      try {
        const updated = saved ? await wishlistService.remove(productId) : await wishlistService.add(productId)
        setItems(updated)
        toast.success(saved ? 'Removed from your wishlist' : 'Saved to your wishlist')
      } catch (err) {
        toast.error(errorMessage(err))
      }
    },
    [ids, requireCustomer],
  )

  const value = useMemo(
    () => ({ items, count: items.length, loading, toggle, isWished: (id) => ids.has(id) }),
    [items, loading, toggle, ids],
  )

  return <WishlistContext.Provider value={value}>{children}</WishlistContext.Provider>
}
