import { useEffect, useRef, useState } from 'react'
import toast from 'react-hot-toast'
import { errorMessage } from '../../services/api'
import { productService } from '../../services/productService'
import { PATTERNS } from '../../utils/constants'
import JewelImage from '../JewelImage'
import Modal from '../Modal'

/** Product gallery: upload, add by URL, replace, choose main image, delete (managers only). */
export default function ImageManager({ show, productId, canDelete, onClose, onChanged }) {
  const [product, setProduct] = useState(null)
  const [busy, setBusy] = useState(false)
  const [url, setUrl] = useState('')
  const uploadRef = useRef(null)
  const replaceRef = useRef(null)
  const replacingId = useRef(null)

  useEffect(() => {
    if (!show || !productId) return
    setProduct(null)
    productService.getForManagement(productId).then(setProduct).catch((err) => toast.error(errorMessage(err)))
  }, [show, productId])

  const run = async (action, success) => {
    setBusy(true)
    try {
      const updated = await action()
      setProduct(updated)
      onChanged?.(updated)
      if (success) toast.success(success)
    } catch (err) {
      toast.error(errorMessage(err))
    } finally {
      setBusy(false)
    }
  }

  const upload = (e) => {
    const files = e.target.files
    if (files?.length) run(() => productService.uploadImages(productId, files), 'Images uploaded')
    e.target.value = ''
  }
  const replace = (e) => {
    const file = e.target.files?.[0]
    if (file) run(() => productService.replaceImage(productId, replacingId.current, file), 'Image replaced')
    e.target.value = ''
  }
  const addUrl = (e) => {
    e.preventDefault()
    if (!PATTERNS.url.test(url)) {
      toast.error('Image URL must start with http:// or https://')
      return
    }
    run(() => productService.addImageUrl(productId, url), 'Image added').then(() => setUrl(''))
  }

  const images = product?.images || []

  return (
    <Modal show={show} title={product ? `Images · ${product.name}` : 'Images'} size="lg" onClose={onClose}>
      {!product ? (
        <div className="text-center text-muted-jc py-4">Loading…</div>
      ) : (
        <>
          <div className="d-flex flex-wrap gap-2 mb-3">
            <button className="btn btn-jc btn-sm" onClick={() => uploadRef.current.click()} disabled={busy}>
              <i className="bi bi-upload me-1" /> Upload images
            </button>
            <input ref={uploadRef} type="file" accept="image/*" multiple hidden onChange={upload} />
            <input ref={replaceRef} type="file" accept="image/*" hidden onChange={replace} />
            <form onSubmit={addUrl} className="d-flex gap-2 flex-grow-1">
              <input className="form-control form-control-sm" placeholder="…or paste an image URL (https://)" value={url} onChange={(e) => setUrl(e.target.value)} />
              <button className="btn btn-outline-jc btn-sm" disabled={busy || !url}>Add</button>
            </form>
          </div>
          <p className="small text-muted-jc">JPG, PNG or WebP up to 10 MB. The main image is shown on product cards.</p>

          {images.length === 0 ? (
            <div className="text-center text-muted-jc py-4 border">No images yet – the store shows an illustration until you add one.</div>
          ) : (
            <div className="img-grid" style={{ opacity: busy ? 0.6 : 1 }}>
              {images.map((img) => (
                <div key={img.id} className={`img-cell ${img.mainImage ? 'main' : ''}`}>
                  <div className="media"><JewelImage src={img.imageUrl} alt="" /></div>
                  <div className="p-2 d-flex flex-wrap gap-1 justify-content-between align-items-center">
                    {img.mainImage ? (
                      <span className="status gold" style={{ fontSize: '0.58rem' }}>Main</span>
                    ) : (
                      <button className="btn btn-outline-jc btn-sm px-2 py-1" style={{ fontSize: '0.6rem' }} disabled={busy} onClick={() => run(() => productService.setMainImage(productId, img.id), 'Main image updated')}>
                        Set main
                      </button>
                    )}
                    <div className="d-flex gap-1">
                      <button
                        className="btn btn-outline-jc btn-sm px-2 py-1"
                        title="Replace"
                        aria-label="Replace image"
                        disabled={busy}
                        onClick={() => {
                          replacingId.current = img.id
                          replaceRef.current.click()
                        }}
                      >
                        <i className="bi bi-arrow-repeat" />
                      </button>
                      {canDelete && (
                        <button className="btn btn-outline-jc btn-sm px-2 py-1" title="Delete" aria-label="Delete image" disabled={busy} onClick={() => run(() => productService.deleteImage(productId, img.id), 'Image deleted')}>
                          <i className="bi bi-trash" />
                        </button>
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </>
      )}
    </Modal>
  )
}
