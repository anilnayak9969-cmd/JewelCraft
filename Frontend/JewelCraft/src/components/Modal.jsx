import { useEffect } from 'react'
import { createPortal } from 'react-dom'

/** React-controlled modal (no Bootstrap JS needed). Closes on Escape and backdrop click. */
export default function Modal({ show, title, onClose, children, footer, size = '' }) {
  useEffect(() => {
    if (!show) return
    const onKey = (e) => e.key === 'Escape' && onClose?.()
    document.addEventListener('keydown', onKey)
    document.body.style.overflow = 'hidden'
    return () => {
      document.removeEventListener('keydown', onKey)
      document.body.style.overflow = ''
    }
  }, [show, onClose])

  if (!show) return null

  return createPortal(
    <div className="jc-modal-backdrop" onMouseDown={(e) => e.target === e.currentTarget && onClose?.()}>
      <div className={`jc-modal ${size}`} role="dialog" aria-modal="true" aria-label={title}>
        <div className="jc-modal-head">
          <h3>{title}</h3>
          <button type="button" className="btn-close" onClick={onClose} aria-label="Close" />
        </div>
        <div className="jc-modal-body">{children}</div>
        {footer && <div className="jc-modal-foot">{footer}</div>}
      </div>
    </div>,
    document.body,
  )
}

/** Small yes/no dialog for destructive actions. */
export function ConfirmModal({ show, title = 'Are you sure?', message, confirmText = 'Confirm', busy, onConfirm, onClose }) {
  return (
    <Modal
      show={show}
      title={title}
      onClose={onClose}
      footer={
        <>
          <button className="btn btn-outline-jc btn-sm" onClick={onClose} disabled={busy}>Cancel</button>
          <button className="btn btn-jc btn-sm" onClick={onConfirm} disabled={busy}>
            {busy ? 'Please wait…' : confirmText}
          </button>
        </>
      }
    >
      <p className="mb-0 text-soft">{message}</p>
    </Modal>
  )
}
