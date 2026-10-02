import { useContext } from 'react'
import { WishlistContext } from '../context/WishlistContext'

export default function useWishlist() {
  return useContext(WishlistContext)
}
