import { useCallback, useEffect, useState } from 'react'

/** Seconds left until a deadline; `restart(seconds)` starts a new countdown. */
export default function useCountdown(initialSeconds = 0) {
  const [endsAt, setEndsAt] = useState(() => Date.now() + initialSeconds * 1000)
  const [left, setLeft] = useState(initialSeconds)

  useEffect(() => {
    const tick = () => setLeft(Math.max(0, Math.ceil((endsAt - Date.now()) / 1000)))
    tick()
    const id = setInterval(tick, 1000)
    return () => clearInterval(id)
  }, [endsAt])

  const restart = useCallback((seconds) => setEndsAt(Date.now() + seconds * 1000), [])
  return [left, restart]
}

/** 125 -> "2:05" */
export const mmss = (s) => `${Math.floor(s / 60)}:${String(s % 60).padStart(2, '0')}`
