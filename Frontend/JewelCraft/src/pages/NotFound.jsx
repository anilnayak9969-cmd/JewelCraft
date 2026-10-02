import { Link } from 'react-router-dom'
import { JewelGlyph } from '../components/JewelImage'

export default function NotFound() {
  return (
    <div className="container text-center" style={{ padding: '6rem 1rem' }}>
      <JewelGlyph type="gem" style={{ width: 110 }} />
      <div className="eyebrow mt-4">Error 404</div>
      <h1 className="mt-2" style={{ fontSize: '3rem' }}>This piece has slipped away</h1>
      <p className="text-soft mx-auto" style={{ maxWidth: 440 }}>The page you’re looking for doesn’t exist or has moved.</p>
      <div className="d-flex gap-2 justify-content-center mt-4">
        <Link to="/" className="btn btn-jc">Return home</Link>
        <Link to="/products" className="btn btn-outline-jc">Shop jewellery</Link>
      </div>
    </div>
  )
}
