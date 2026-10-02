export default function Loading({ text = 'Loading', fullPage = false }) {
  return (
    <div className="loading-wrap" style={fullPage ? { minHeight: '70vh' } : undefined} role="status">
      <div className="loader-ring" />
      <span className="small-caps">{text}</span>
    </div>
  )
}
