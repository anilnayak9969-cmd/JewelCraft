/** Numbered section heading inside the sign-up forms: "01 — YOUR DETAILS". */
export default function FormSection({ number, title, children }) {
  return (
    <section className="form-section">
      <div className="form-section-title">
        <span className="n">{String(number).padStart(2, '0')}</span>
        <h6>{title}</h6>
      </div>
      {children}
    </section>
  )
}
