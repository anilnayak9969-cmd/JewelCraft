import { useCallback, useEffect, useState } from 'react'
import { errorMessage } from '../services/api'

/**
 * Loads data from an API call and re-runs when `deps` change.
 *   const { data, loading, error, reload } = useApi(() => productService.get(id), [id])
 * Earlier data stays visible while a reload is in flight, so tables don't flicker.
 */
export default function useApi(fetcher, deps = []) {
  const [state, setState] = useState({ data: null, loading: true, error: null })
  const [tick, setTick] = useState(0)

  useEffect(() => {
    let alive = true
    setState((s) => ({ ...s, loading: true, error: null }))
    fetcher()
      .then((data) => alive && setState({ data, loading: false, error: null }))
      .catch((err) => alive && setState({ data: null, loading: false, error: errorMessage(err) }))
    return () => {
      alive = false
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [...deps, tick])

  const reload = useCallback(() => setTick((t) => t + 1), [])
  const setData = useCallback(
    (next) => setState((s) => ({ ...s, data: typeof next === 'function' ? next(s.data) : next })),
    [],
  )

  return { ...state, reload, setData }
}
