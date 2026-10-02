import { useEffect, useState } from 'react'
import toast from 'react-hot-toast'
import { Link } from 'react-router-dom'
import { glyphFor, JewelGlyph } from '../components/JewelImage'
import ProductCard from '../components/ProductCard'
import Stars from '../components/Stars'
import { categoryService, metalRateService, offerService, productService, reviewService } from '../services/productService'
import { METAL_TONES } from '../utils/constants'
import { formatDate, formatINR, metalLabel } from '../utils/format'

/** Loads one home-page section; a failing section simply stays empty. */
function useSection(fetcher) {
  const [data, setData] = useState([])
  useEffect(() => {
    fetcher().then(setData).catch(() => setData([]))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])
  return data
}

const CATEGORY_TONES = ['GOLD', 'ROSE_GOLD', 'WHITE_GOLD', 'GOLD', 'PLATINUM', 'ROSE_GOLD']

function HeroArt() {
  return (
    <svg className="hero-art" viewBox="0 0 600 600" fill="none" aria-hidden="true">
      <defs>
        <radialGradient id="glow" cx="50%" cy="50%" r="50%">
          <stop offset="0%" stopColor="#c9a96e" stopOpacity="0.22" />
          <stop offset="100%" stopColor="#c9a96e" stopOpacity="0" />
        </radialGradient>
        <linearGradient id="goldline" x1="0" y1="0" x2="1" y2="1">
          <stop offset="0%" stopColor="#e9d3a4" />
          <stop offset="50%" stopColor="#c9a96e" />
          <stop offset="100%" stopColor="#8f6c36" />
        </linearGradient>
      </defs>
      <circle cx="300" cy="300" r="280" fill="url(#glow)" />
      <circle cx="300" cy="300" r="250" stroke="#c9a96e" strokeOpacity="0.12" />
      <circle cx="300" cy="300" r="205" stroke="#c9a96e" strokeOpacity="0.08" />
      {/* band */}
      <ellipse cx="300" cy="360" rx="132" ry="132" stroke="url(#goldline)" strokeWidth="3" />
      <ellipse cx="300" cy="360" rx="112" ry="112" stroke="url(#goldline)" strokeWidth="1.4" />
      {/* setting */}
      <path d="M262 236l-12 -10M338 236l12 -10" stroke="url(#goldline)" strokeWidth="2" />
      {/* diamond */}
      <g stroke="url(#goldline)" strokeWidth="1.6" strokeLinejoin="round">
        <path d="M240 160h120l34 40-94 96-94-96z" />
        <path d="M206 200h188" />
        <path d="M270 160l-18 40 48 96 48-96-18-40" />
        <path d="M300 160l-48 40M300 160l48 40" />
        <path d="M240 160l12 40M360 160l-12 40" />
      </g>
      {/* sparkles */}
      <path className="sparkle" d="M420 130l5 15 15 5-15 5-5 15-5-15-15-5 15-5z" fill="#e9d3a4" />
      <path className="sparkle s2" d="M175 120l4 11 11 4-11 4-4 11-4-11-11-4 11-4z" fill="#e9d3a4" />
      <path className="sparkle s3" d="M455 330l3 9 9 3-9 3-3 9-3-9-9-3 9-3z" fill="#e9d3a4" />
    </svg>
  )
}

export default function Home() {
  const featured = useSection(productService.featured)
  const latest = useSection(productService.latest)
  const categories = useSection(() => categoryService.list())
  const offers = useSection(offerService.active)
  const reviews = useSection(reviewService.latest)
  const rates = useSection(metalRateService.list)

  const topCategories = [...categories].sort((a, b) => b.productCount - a.productCount).slice(0, 6)
  const bridal = categories.find((c) => c.name.toLowerCase().includes('wedding'))

  const copyCode = (code) => {
    navigator.clipboard?.writeText(code).then(
      () => toast.success(`Code ${code} copied`),
      () => toast(`Use code ${code} at checkout`),
    )
  }

  return (
    <>
      {/* ---------------- hero ---------------- */}
      <section className="hero">
        <HeroArt />
        <div className="container position-relative py-5">
          <div className="row">
            <div className="col-lg-7 fade-up">
              <div className="eyebrow" style={{ color: 'var(--jc-gold-light)' }}>The Signature Collection</div>
              <h1 className="mt-3 mb-4">
                Crafted to be <em>treasured</em>,<br />for generations.
              </h1>
              <p className="lead mb-5">
                Hallmarked gold, certified diamonds and heirloom craftsmanship – each piece priced openly
                from today&apos;s metal rate.
              </p>
              <div className="d-flex flex-wrap gap-3">
                <Link to="/products" className="btn btn-gold">Explore the collection</Link>
                <Link to={bridal ? `/products?categoryId=${bridal.id}` : '/products'} className="btn btn-outline-light-jc">
                  The bridal edit
                </Link>
              </div>
              <div className="hero-meta row g-4">
                <div className="col-4"><strong>BIS</strong>Hallmarked gold</div>
                <div className="col-4"><strong>Live</strong>Metal-rate pricing</div>
                <div className="col-4"><strong>Insured</strong>Doorstep delivery</div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* ---------------- today's metal rates ---------------- */}
      {rates.length > 0 && (
        <div className="rate-strip" aria-label="Today's metal rates">
          <div className="rate-track">
            {[...rates, ...rates].map((r, i) => (
              <span key={`${r.id}-${i}`}>
                <span className="text-gold me-2">✦</span>
                {metalLabel(r.metalType)} {r.purity} <strong>{formatINR(r.pricePerGram)}/g</strong>
              </span>
            ))}
            <span>Rates updated {formatDate(rates[0]?.updatedAt)}</span>
          </div>
        </div>
      )}

      {/* ---------------- categories ---------------- */}
      {topCategories.length > 0 && (
        <section className="section">
          <div className="container">
            <div className="text-center mb-5">
              <div className="eyebrow">Discover</div>
              <h2 className="section-title mt-2">Shop by Category</h2>
              <hr className="gold-rule" />
            </div>
            <div className="row g-4">
              {topCategories.map((c, i) => {
                const tone = METAL_TONES[CATEGORY_TONES[i % CATEGORY_TONES.length]]
                return (
                  <div key={c.id} className="col-6 col-md-4 col-lg-2">
                    <Link to={`/products?categoryId=${c.id}`} className="cat-tile">
                      <div
                        className="arch d-flex align-items-center justify-content-center"
                        style={{ background: `radial-gradient(circle at 50% 40%, #fffdf9, ${tone.from} 50%, ${tone.to})` }}
                      >
                        <JewelGlyph type={glyphFor(c.name)} color={tone.stroke} style={{ width: '58%' }} />
                      </div>
                      <div className="name">{c.name}</div>
                      <div className="count">{c.productCount} {c.productCount === 1 ? 'piece' : 'pieces'}</div>
                    </Link>
                  </div>
                )
              })}
            </div>
          </div>
        </section>
      )}

      {/* ---------------- featured ---------------- */}
      {featured.length > 0 && (
        <section className="section bg-cream">
          <div className="container">
            <div className="d-flex flex-wrap justify-content-between align-items-end gap-3 mb-5">
              <div>
                <div className="eyebrow">Most coveted</div>
                <h2 className="section-title mt-2 mb-0">The Signature Edit</h2>
              </div>
              <Link to="/products?featured=true" className="btn-link-jc">View all</Link>
            </div>
            <div className="row g-4">
              {featured.slice(0, 8).map((p) => (
                <div key={p.id} className="col-6 col-lg-3"><ProductCard product={p} /></div>
              ))}
            </div>
          </div>
        </section>
      )}

      {/* ---------------- editorial ---------------- */}
      <section className="section">
        <div className="container">
          <div className="row g-0 align-items-stretch">
            <div className="col-lg-6 bg-noir d-flex align-items-center">
              <div className="p-4 p-md-5">
                <div className="eyebrow" style={{ color: 'var(--jc-gold-light)' }}>The Bridal Story</div>
                <h2 className="section-title mt-3">For the moments that become memories</h2>
                <p className="mt-3 mb-4" style={{ color: '#b9ad9b', maxWidth: 440 }}>
                  Kundan, polki and diamond sets, matched platinum bands and temple gold – our bridal atelier
                  brings every heirloom to life with you.
                </p>
                <Link to={bridal ? `/products?categoryId=${bridal.id}` : '/products'} className="btn btn-outline-light-jc">
                  Discover bridal
                </Link>
              </div>
            </div>
            <div
              className="col-lg-6 d-flex align-items-center justify-content-center"
              style={{ minHeight: 380, background: 'radial-gradient(circle at 50% 45%, #fffdf9, #f2e6cf 55%, #e5d0a8)' }}
            >
              <JewelGlyph type="rings" color="#a8834a" strokeWidth={1} style={{ width: '58%', maxWidth: 340 }} />
            </div>
          </div>
        </div>
      </section>

      {/* ---------------- offers ---------------- */}
      {offers.length > 0 && (
        <section id="offers" className="section bg-noir">
          <div className="container">
            <div className="text-center mb-5">
              <div className="eyebrow" style={{ color: 'var(--jc-gold-light)' }}>Privileges</div>
              <h2 className="section-title mt-2">Current Offers</h2>
              <hr className="gold-rule" />
            </div>
            <div className="row g-4 justify-content-center">
              {offers.map((o) => (
                <div key={o.id} className="col-md-6 col-lg-4">
                  <div className="offer-card">
                    <div className="display-serif" style={{ fontSize: '3rem', color: 'var(--jc-gold-light)' }}>
                      {Number(o.discountPercent)}% <span style={{ fontSize: '1.4rem' }}>off</span>
                    </div>
                    <h4 className="mt-2" style={{ fontSize: '1.5rem' }}>{o.title}</h4>
                    {o.description && <p className="small mb-0" style={{ color: '#b9ad9b' }}>{o.description}</p>}
                    <button type="button" className="code" onClick={() => copyCode(o.code)} title="Copy code">
                      {o.code} <i className="bi bi-copy ms-1" />
                    </button>
                    <div className="small mt-3" style={{ color: '#8f8577' }}>Valid until {formatDate(o.endDate)}</div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </section>
      )}

      {/* ---------------- latest ---------------- */}
      {latest.length > 0 && (
        <section className="section">
          <div className="container">
            <div className="d-flex flex-wrap justify-content-between align-items-end gap-3 mb-5">
              <div>
                <div className="eyebrow">Just arrived</div>
                <h2 className="section-title mt-2 mb-0">New Arrivals</h2>
              </div>
              <Link to="/products?sort=newest" className="btn-link-jc">Shop new in</Link>
            </div>
            <div className="row g-4">
              {latest.slice(0, 4).map((p) => (
                <div key={p.id} className="col-6 col-lg-3"><ProductCard product={p} /></div>
              ))}
            </div>
          </div>
        </section>
      )}

      {/* ---------------- why choose us ---------------- */}
      <section className="section bg-cream">
        <div className="container">
          <div className="text-center mb-5">
            <div className="eyebrow">Why JewelCraft</div>
            <h2 className="section-title mt-2">The JewelCraft Promise</h2>
            <hr className="gold-rule" />
          </div>
          <div className="row g-4">
            {[
              ['patch-check', 'Certified Purity', 'Every gold piece is BIS hallmarked and every diamond comes certified.'],
              ['calculator', 'Transparent Pricing', 'See metal value, making and stone charges, discount and tax – line by line.'],
              ['shield-lock', 'Secure Payments', 'Pay online through Razorpay or choose cash on delivery.'],
              ['truck', 'Insured Delivery', 'Fully insured, tamper-proof packaging, tracked to your door.'],
            ].map(([icon, title, text]) => (
              <div key={title} className="col-6 col-lg-3">
                <div className="promise">
                  <i className={`bi bi-${icon}`} />
                  <h5>{title}</h5>
                  <p>{text}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ---------------- reviews ---------------- */}
      <section className="section">
        <div className="container">
          <div className="text-center mb-5">
            <div className="eyebrow">Kind words</div>
            <h2 className="section-title mt-2">Loved by our Customers</h2>
            <hr className="gold-rule" />
          </div>
          {reviews.length > 0 ? (
            <div className="row g-4">
              {reviews.slice(0, 3).map((r) => (
                <div key={r.id} className="col-md-4">
                  <div className="review-card">
                    <Stars value={r.rating} />
                    <blockquote className="mt-3 mb-4">“{r.comment}”</blockquote>
                    <div className="fw-500">{r.userName}</div>
                    <Link to={`/products/${r.productId}`} className="small text-muted-jc">on {r.productName}</Link>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <p className="text-center text-soft">
              Reviews from our customers will appear here. Bought a piece? <Link to="/orders" className="text-gold">Share your story.</Link>
            </p>
          )}
        </div>
      </section>
    </>
  )
}
