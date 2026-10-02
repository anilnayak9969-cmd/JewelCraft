import { useRef } from 'react'

/**
 * Six single-digit boxes. Typing moves to the next box, Backspace goes back,
 * and pasting a whole code fills every box. `value` is a string of up to 6 digits.
 */
export default function OtpInput({ value, onChange, onComplete, error, disabled, length = 6 }) {
  const refs = useRef([])
  const digits = Array.from({ length }, (_, i) => value[i] || '')

  const setDigits = (next) => {
    const code = next.join('').slice(0, length)
    onChange(code)
    if (code.length === length && /^\d+$/.test(code)) onComplete?.(code)
  }

  const handleChange = (i, raw) => {
    const clean = raw.replace(/\D/g, '')
    if (!clean) return
    const next = [...digits]
    // Typing over a filled box or pasting several digits fills forward from here.
    clean.split('').forEach((d, k) => {
      if (i + k < length) next[i + k] = d
    })
    setDigits(next)
    refs.current[Math.min(i + clean.length, length - 1)]?.focus()
  }

  const handleKey = (i, e) => {
    if (e.key === 'Backspace') {
      e.preventDefault()
      const next = [...digits]
      if (next[i]) next[i] = ''
      else if (i > 0) {
        next[i - 1] = ''
        refs.current[i - 1]?.focus()
      }
      setDigits(next)
    } else if (e.key === 'ArrowLeft' && i > 0) refs.current[i - 1]?.focus()
    else if (e.key === 'ArrowRight' && i < length - 1) refs.current[i + 1]?.focus()
  }

  const handlePaste = (e) => {
    const pasted = e.clipboardData.getData('text').replace(/\D/g, '').slice(0, length)
    if (!pasted) return
    e.preventDefault()
    setDigits(pasted.split('').concat(Array(length).fill('')).slice(0, length))
    refs.current[Math.min(pasted.length, length - 1)]?.focus()
  }

  return (
    <div className={`otp-inputs ${error ? 'error' : ''}`} onPaste={handlePaste}>
      {digits.map((d, i) => (
        <input
          key={i}
          ref={(el) => (refs.current[i] = el)}
          type="text"
          inputMode="numeric"
          autoComplete={i === 0 ? 'one-time-code' : 'off'}
          maxLength={length}
          value={d}
          disabled={disabled}
          className={d ? 'filled' : ''}
          aria-label={`Digit ${i + 1} of ${length}`}
          autoFocus={i === 0}
          onChange={(e) => handleChange(i, e.target.value)}
          onKeyDown={(e) => handleKey(i, e)}
          onFocus={(e) => e.target.select()}
        />
      ))}
    </div>
  )
}
