import { useState } from 'react'
import { imageUrl } from '../services/api'
import { METAL_TONES } from '../utils/constants'

/** Line-art drawings used when a product has no photo yet (viewBox 0 0 100 100). */
const GLYPHS = {
  ring: (
    <>
      <circle cx="50" cy="62" r="25" />
      <circle cx="50" cy="62" r="20" />
      <path d="M41 32h18l6 8-15 14-15-14z" />
      <path d="M35 40h30M45 32l5 22 5-22" />
    </>
  ),
  rings: (
    <>
      <circle cx="40" cy="58" r="21" />
      <circle cx="40" cy="58" r="17" />
      <circle cx="62" cy="52" r="21" />
      <circle cx="62" cy="52" r="17" />
      <path d="M57 25h10l3.5 4.5-8.5 8-8.5-8z" />
    </>
  ),
  necklace: (
    <>
      <path d="M16 14c4 34 18 52 34 52s30-18 34-52" />
      <path d="M22 14c4 28 15 44 28 44s24-16 28-44" />
      <path d="M50 66v4" />
      <path d="M50 70l7 9-7 10-7-10z" />
      <circle cx="31" cy="49" r="1.6" />
      <circle cx="69" cy="49" r="1.6" />
      <circle cx="40" cy="57" r="1.6" />
      <circle cx="60" cy="57" r="1.6" />
    </>
  ),
  pendant: (
    <>
      <path d="M26 10l24 34 24-34" />
      <circle cx="50" cy="47" r="3" />
      <path d="M50 50c-12 10-16 18-16 25a16 16 0 0032 0c0-7-4-15-16-25z" />
      <path d="M50 60c-6 5-8 9-8 14a8 8 0 0016 0c0-5-2-9-8-14z" />
    </>
  ),
  earring: (
    <>
      <path d="M30 12c-6 0-8 8-2 11" />
      <circle cx="30" cy="27" r="3" />
      <path d="M30 30v6" />
      <path d="M30 36c-9 8-12 15-12 21a12 12 0 0024 0c0-6-3-13-12-21z" />
      <path d="M70 12c-6 0-8 8-2 11" />
      <circle cx="70" cy="27" r="3" />
      <path d="M70 30v6" />
      <path d="M70 36c-9 8-12 15-12 21a12 12 0 0024 0c0-6-3-13-12-21z" />
    </>
  ),
  bracelet: (
    <>
      <ellipse cx="50" cy="55" rx="34" ry="20" />
      <ellipse cx="50" cy="55" rx="28" ry="15" />
      <circle cx="22" cy="49" r="2" />
      <circle cx="34" cy="38" r="2" />
      <circle cx="50" cy="35" r="2" />
      <circle cx="66" cy="38" r="2" />
      <circle cx="78" cy="49" r="2" />
    </>
  ),
  bangle: (
    <>
      <ellipse cx="44" cy="54" rx="30" ry="18" />
      <ellipse cx="44" cy="54" rx="25" ry="14" />
      <ellipse cx="58" cy="48" rx="30" ry="18" />
      <ellipse cx="58" cy="48" rx="25" ry="14" />
    </>
  ),
  chain: (
    <>
      <path d="M14 22c10 40 26 56 36 56s26-16 36-56" strokeDasharray="5 3.5" />
      <path d="M18 22c9 36 23 50 32 50s23-14 32-50" strokeDasharray="5 3.5" />
    </>
  ),
  gem: (
    <>
      <path d="M28 30h44l14 16-36 36-36-36z" />
      <path d="M14 46h72M40 30l-6 16 16 36 16-36-6-16M50 30l-16 16M50 30l16 16" />
    </>
  ),
}

export function glyphFor(category = '', name = '') {
  const s = `${category} ${name}`.toLowerCase()
  if (s.includes('wedding') || s.includes('couple')) return 'rings'
  if (s.includes('earring') || s.includes('jhumka') || s.includes('stud')) return 'earring'
  if (s.includes('necklace') || s.includes('bridal') || s.includes('set')) return 'necklace'
  if (s.includes('pendant')) return 'pendant'
  if (s.includes('bangle')) return 'bangle'
  if (s.includes('bracelet') || s.includes('anklet')) return 'bracelet'
  if (s.includes('chain')) return 'chain'
  if (s.includes('ring') || s.includes('band')) return 'ring'
  if (s.includes('gold')) return 'necklace'
  if (s.includes('silver')) return 'pendant'
  return 'gem'
}

export function JewelGlyph({ type = 'gem', color = '#a8834a', strokeWidth = 1.4, className, style }) {
  return (
    <svg
      viewBox="0 0 100 100"
      fill="none"
      stroke={color}
      strokeWidth={strokeWidth}
      strokeLinecap="round"
      strokeLinejoin="round"
      className={className}
      style={style}
      aria-hidden="true"
    >
      {GLYPHS[type] || GLYPHS.gem}
    </svg>
  )
}

/**
 * Product photo with a graceful fallback: a metal-tinted backdrop with a line drawing
 * of the piece, used when there is no image or the image fails to load.
 */
export default function JewelImage({ src, alt = '', metal, category, name, className = '' }) {
  const [failed, setFailed] = useState(false)
  const url = imageUrl(src)

  if (url && !failed) {
    return (
      <div className={`jewel-img ${className}`}>
        <img src={url} alt={alt} loading="lazy" onError={() => setFailed(true)} />
      </div>
    )
  }

  const tone = METAL_TONES[metal] || METAL_TONES.GOLD
  return (
    <div className={`jewel-img ${className}`} role="img" aria-label={alt || name || 'Jewellery'}>
      <div
        className="jewel-ph"
        style={{ background: `radial-gradient(circle at 50% 42%, #fffdf9 0%, ${tone.from} 45%, ${tone.to} 100%)` }}
      >
        <JewelGlyph type={glyphFor(category, name)} color={tone.stroke} />
      </div>
    </div>
  )
}
