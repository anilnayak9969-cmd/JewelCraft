import { Link } from 'react-router-dom'

export default function Footer() {
  return (
    <footer className="jc-footer">
      <div className="container">
        <div className="row g-5">
          <div className="col-lg-4">
            <div className="jc-brand mb-3" style={{ fontSize: '1.6rem' }}>JewelCraft</div>
            <p style={{ maxWidth: 320 }}>
              Fine jewellery made by hand, priced openly from the day&apos;s metal rate, and delivered fully insured.
            </p>
            <div className="socials mt-4">
              <a href="https://instagram.com" target="_blank" rel="noreferrer" aria-label="Instagram"><i className="bi bi-instagram" /></a>
              <a href="https://pinterest.com" target="_blank" rel="noreferrer" aria-label="Pinterest"><i className="bi bi-pinterest" /></a>
              <a href="https://facebook.com" target="_blank" rel="noreferrer" aria-label="Facebook"><i className="bi bi-facebook" /></a>
              <a href="https://youtube.com" target="_blank" rel="noreferrer" aria-label="YouTube"><i className="bi bi-youtube" /></a>
            </div>
          </div>
          <div className="col-6 col-lg-2">
            <h6>Shop</h6>
            <Link to="/products">All Jewellery</Link>
            <Link to="/products?featured=true">Signature Edit</Link>
            <Link to="/products?metalType=GOLD">Gold</Link>
            <Link to="/products?metalType=PLATINUM">Platinum</Link>
            <Link to="/products?metalType=SILVER">Silver</Link>
          </div>
          <div className="col-6 col-lg-2">
            <h6>Account</h6>
            <Link to="/profile">My Account</Link>
            <Link to="/orders">Track Orders</Link>
            <Link to="/wishlist">Wishlist</Link>
            <Link to="/cart">Shopping Bag</Link>
          </div>
          <div className="col-lg-4">
            <h6>Visit &amp; Contact</h6>
            <p className="mb-2"><i className="bi bi-geo-alt me-2 text-gold" />JewelCraft Atelier, MG Road, Bengaluru</p>
            <p className="mb-2"><i className="bi bi-telephone me-2 text-gold" />+91 80 4000 1234</p>
            <p className="mb-2"><i className="bi bi-envelope me-2 text-gold" />care@jewelcraft.com</p>
            <p className="mb-0"><i className="bi bi-clock me-2 text-gold" />Mon – Sat, 10:30 am – 8:00 pm</p>
          </div>
        </div>
        <div className="bottom d-flex flex-column flex-md-row justify-content-between gap-2">
          <span>© {new Date().getFullYear()} JewelCraft – Jewellery E-Commerce &amp; Store Management System</span>
          <span>BIS Hallmarked · Certified Diamonds · Secure Payments</span>
        </div>
      </div>
    </footer>
  )
}
