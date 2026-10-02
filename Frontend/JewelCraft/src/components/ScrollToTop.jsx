import { useEffect } from 'react'
import { useLocation } from 'react-router-dom'

/** Scrolls to the top whenever the page (not just the query string) changes. */
export default function ScrollToTop() {
  const { pathname } = useLocation()
  useEffect(() => {
    window.scrollTo({ top: 0, behavior: 'instant' })
  }, [pathname])
  return null
}
