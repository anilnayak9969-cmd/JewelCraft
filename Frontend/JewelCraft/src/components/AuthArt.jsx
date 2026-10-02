import { JewelGlyph } from './JewelImage'

/**
 * Dark decorative panel beside the sign-in / sign-up forms.
 * Optional `perks` (list of [icon, text]) and `journey` (step labels) explain the chosen role.
 */
export default function AuthArt({ quote, caption, perks, journey, glyph = 'gem' }) {
  return (
    <div className="auth-art">
      <div className="eyebrow" style={{ color: 'var(--jc-gold-light)' }}>JewelCraft · Fine Jewellery</div>
      <JewelGlyph
        type={glyph}
        color="#c9a96e"
        strokeWidth={0.7}
        style={{ position: 'absolute', right: '-12%', top: '14%', width: '72%', opacity: 0.3 }}
      />
      <div>
        <blockquote>{quote}</blockquote>
        <div className="small-caps mt-3" style={{ color: '#a99d8c' }}>{caption}</div>
        {perks && (
          <ul className="perks">
            {perks.map(([icon, text]) => (
              <li key={text}><i className={`bi bi-${icon}`} />{text}</li>
            ))}
          </ul>
        )}
        {journey && (
          <div className="journey">
            {journey.map((step, i) => (
              <div key={step}><b>{String(i + 1).padStart(2, '0')}</b>{step}</div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
