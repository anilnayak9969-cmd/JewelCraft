import { useEffect, useState } from 'react'
import { Link, NavLink, useLocation, useNavigate } from 'react-router-dom'
import useAuth from '../hooks/useAuth'
import useCart from '../hooks/useCart'
import useWishlist from '../hooks/useWishlist'
import { categoryService } from '../services/productService'
import { ROLE_HOME, ROLE_LABELS } from '../utils/constants'
import SearchBar from './SearchBar'

export default function Navbar() {
  const { user, logout } = useAuth()
  const { count: bagCount } = useCart()
  const { count: wishCount } = useWishlist()
  const [categories, setCategories] = useState([])
  const [megaOpen, setMegaOpen] = useState(false)
  const [searchOpen, setSearchOpen] = useState(false)
  const [mobileOpen, setMobileOpen] = useState(false)
  const location = useLocation()
  const navigate = useNavigate()
  const isCustomer = user?.role === 'CUSTOMER'

  useEffect(() => {
    categoryService.list().then(setCategories).catch(() => setCategories([]))
  }, [])

  // Close any open menu when the page changes.
  useEffect(() => {
    setMegaOpen(false)
    setSearchOpen(false)
    setMobileOpen(false)
  }, [location.pathname, location.search])

  const byName = (name) => categories.find((c) => c.name.toLowerCase() === name.toLowerCase())
  const catLink = (name) => {
    const c = byName(name)
    return c ? `/products?categoryId=${c.id}` : '/products'
  }

  const signOut = () => {
    logout()
    navigate('/')
  }

  return (
    <>
      <div className="announce">
        Complimentary insured shipping above ₹5,000 <span className="mx-2 text-gold">✦</span> BIS hallmarked gold
      </div>
      <nav className="jc-nav" onMouseLeave={() => setMegaOpen(false)}>
        <div className="container position-relative">
          <div className="nav-inner">
            {/* left */}
            <div className="d-flex align-items-center">
              <button className="nav-icon d-lg-none" onClick={() => setMobileOpen(true)} aria-label="Open menu">
                <i className="bi bi-list" style={{ fontSize: '1.5rem' }} />
              </button>
              <div className="nav-links d-none d-lg-flex align-items-center">
                <NavLink to="/products" end>Shop</NavLink>
                <button type="button" onMouseEnter={() => setMegaOpen(true)} onClick={() => setMegaOpen((o) => !o)} aria-expanded={megaOpen}>
                  Collections <i className="bi bi-chevron-down" style={{ fontSize: '0.6rem' }} />
                </button>
                <Link to={catLink('Wedding Jewellery')}>Bridal</Link>
                <Link to={catLink('Diamond Jewellery')}>Diamonds</Link>
              </div>
            </div>

            {/* centre */}
            <Link to="/" className="jc-brand" aria-label="JewelCraft home">
              JewelCraft
              <small>Fine Jewellery</small>
            </Link>

            {/* right */}
            <div className="nav-icons">
              <button className="nav-icon" onClick={() => setSearchOpen((o) => !o)} aria-label="Search">
                <i className="bi bi-search" />
              </button>

              <div className="dropdown">
                <button className="nav-icon" data-bs-toggle="dropdown" aria-expanded="false" aria-label="Account">
                  <i className={`bi ${user ? 'bi-person-check' : 'bi-person'}`} />
                </button>
                <ul className="dropdown-menu dropdown-menu-end">
                  {user ? (
                    <>
                      <li className="px-3 py-2">
                        <div className="fw-500">{user.fullName}</div>
                        <div className="small text-muted-jc">{ROLE_LABELS[user.role]}</div>
                      </li>
                      <li><hr className="dropdown-divider" /></li>
                      {isCustomer ? (
                        <>
                          <li><Link className="dropdown-item" to="/profile">My Account</Link></li>
                          <li><Link className="dropdown-item" to="/orders">My Orders</Link></li>
                          <li><Link className="dropdown-item" to="/wishlist">Wishlist</Link></li>
                        </>
                      ) : (
                        <>
                          <li><Link className="dropdown-item" to={ROLE_HOME[user.role]}>Dashboard</Link></li>
                          <li><Link className="dropdown-item" to="/profile">Profile</Link></li>
                        </>
                      )}
                      <li><hr className="dropdown-divider" /></li>
                      <li><button className="dropdown-item" onClick={signOut}>Sign out</button></li>
                    </>
                  ) : (
                    <>
                      <li><Link className="dropdown-item" to="/login">Sign in</Link></li>
                      <li><Link className="dropdown-item" to="/register">Create an account</Link></li>
                    </>
                  )}
                </ul>
              </div>

              <Link to="/wishlist" className="nav-icon d-none d-sm-inline-flex" aria-label="Wishlist">
                <i className="bi bi-heart" />
                {wishCount > 0 && <span className="nav-count">{wishCount}</span>}
              </Link>
              <Link to="/cart" className="nav-icon" aria-label="Shopping bag">
                <i className="bi bi-bag" />
                {bagCount > 0 && <span className="nav-count">{bagCount}</span>}
              </Link>
            </div>
          </div>
        </div>

        {searchOpen && (
          <div className="search-drawer">
            <div className="container" style={{ maxWidth: 680 }}>
              <SearchBar autoFocus />
            </div>
          </div>
        )}

        {megaOpen && categories.length > 0 && (
          <div className="mega-menu d-none d-lg-block">
            <div className="container">
              <div className="row">
                <div className="col-3">
                  <div className="eyebrow mb-3">Shop by category</div>
                  {categories.slice(0, 6).map((c) => (
                    <Link key={c.id} to={`/products?categoryId=${c.id}`}>{c.name}</Link>
                  ))}
                </div>
                <div className="col-3">
                  <div className="eyebrow mb-3">&nbsp;</div>
                  {categories.slice(6).map((c) => (
                    <Link key={c.id} to={`/products?categoryId=${c.id}`}>{c.name}</Link>
                  ))}
                </div>
                <div className="col-3">
                  <div className="eyebrow mb-3">Shop by metal</div>
                  <Link to="/products?metalType=GOLD">Yellow Gold</Link>
                  <Link to="/products?metalType=ROSE_GOLD">Rose Gold</Link>
                  <Link to="/products?metalType=WHITE_GOLD">White Gold</Link>
                  <Link to="/products?metalType=PLATINUM">Platinum</Link>
                  <Link to="/products?metalType=SILVER">Sterling Silver</Link>
                </div>
                <div className="col-3">
                  <div className="bg-noir p-4 h-100 d-flex flex-column justify-content-between">
                    <div>
                      <div className="eyebrow" style={{ color: 'var(--jc-gold-light)' }}>Featured</div>
                      <h4 className="mt-2" style={{ fontSize: '1.6rem' }}>The Signature Edit</h4>
                      <p className="small mb-3" style={{ color: '#b9ad9b' }}>Our most loved pieces, chosen by our artisans.</p>
                    </div>
                    <Link to="/products?featured=true" className="btn btn-outline-light-jc btn-sm align-self-start">Discover</Link>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}
      </nav>

      {mobileOpen && (
        <div className="mobile-menu">
          <div className="d-flex justify-content-between align-items-center mb-4">
            <span className="jc-brand" style={{ fontSize: '1.4rem' }}>JewelCraft</span>
            <button className="btn-close" onClick={() => setMobileOpen(false)} aria-label="Close menu" />
          </div>
          <SearchBar />
          <div className="mt-3">
            <Link to="/products">Shop All</Link>
            {categories.map((c) => (
              <Link key={c.id} to={`/products?categoryId=${c.id}`}>{c.name}</Link>
            ))}
            <Link to="/wishlist">Wishlist</Link>
            {user ? (
              <>
                <Link to={isCustomer ? '/profile' : ROLE_HOME[user.role]}>{isCustomer ? 'My Account' : 'Dashboard'}</Link>
                <button className="btn btn-outline-jc w-100 mt-4" onClick={signOut}>Sign out</button>
              </>
            ) : (
              <Link to="/login">Sign in</Link>
            )}
          </div>
        </div>
      )}
    </>
  )
}
