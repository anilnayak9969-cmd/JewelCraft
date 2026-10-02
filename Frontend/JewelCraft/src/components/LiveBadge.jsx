import { useEffect, useState } from 'react'

/** "● LIVE · updated 8s ago" – or a grey "Final" when the order can no longer change. */
export default function LiveBadge({ lastUpdated, active = true, finalLabel = 'Final status' }) {
  const [now, setNow] = useState(() => Date.now())

  useEffect(() => {
    if (!active) return undefined
    const id = setInterval(() => setNow(Date.now()), 5000)
    return () => clearInterval(id)
  }, [active])

  if (!active) {
    return (
      <span className="live-badge paused">
        <span className="pulse" />
        {finalLabel}
      </span>
    )
  }
  const secs = Math.max(0, Math.round((now - lastUpdated) / 1000))
  return (
    <span className="live-badge" title="This page refreshes automatically">
      <span className="pulse" />
      Live · {secs < 5 ? 'just now' : `updated ${secs}s ago`}
    </span>
  )
}
