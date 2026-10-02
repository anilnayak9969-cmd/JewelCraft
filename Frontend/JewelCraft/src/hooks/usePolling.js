import { useCallback, useEffect, useRef, useState } from 'react'

/**
 * Calls `callback` every `intervalMs` while `active` – the "live" part of order tracking.
 * Pauses while the browser tab is hidden and refreshes immediately when it becomes visible again.
 * Returns the time of the last successful refresh.
 */
export default function usePolling(callback, intervalMs, active = true) {
  const saved = useRef(callback)
  const [lastUpdated, setLastUpdated] = useState(() => Date.now())

  useEffect(() => {
    saved.current = callback
  })

  useEffect(() => {
    if (!active) return undefined
    let stopped = false
    const run = async () => {
      if (document.visibilityState !== 'visible') return
      try {
        await saved.current()
        if (!stopped) setLastUpdated(Date.now())
      } catch {
        // a failed refresh is retried on the next tick
      }
    }
    const id = setInterval(run, intervalMs)
    const onVisible = () => document.visibilityState === 'visible' && run()
    document.addEventListener('visibilitychange', onVisible)
    return () => {
      stopped = true
      clearInterval(id)
      document.removeEventListener('visibilitychange', onVisible)
    }
  }, [intervalMs, active])

  const markUpdated = useCallback(() => setLastUpdated(Date.now()), [])
  return { lastUpdated, markUpdated }
}
