import {
  BarElement,
  CategoryScale,
  Chart as ChartJS,
  Filler,
  LinearScale,
  LineElement,
  PointElement,
  Tooltip,
} from 'chart.js'
import { useState } from 'react'
import { Bar, Line } from 'react-chartjs-2'

ChartJS.register(CategoryScale, LinearScale, BarElement, LineElement, PointElement, Filler, Tooltip)

// One brand hue for every single-series chart; ink tokens for text; recessive grid.
const GOLD = '#a8834a'
const GOLD_FILL = 'rgba(168, 131, 74, 0.12)'
const INK_MUTED = '#8c847b'
const GRID = '#efe9df'
const FONT = { family: 'Jost, sans-serif', size: 11 }

const baseOptions = (format, horizontal = false) => {
  const valueAxis = {
    beginAtZero: true,
    grid: { color: GRID, drawTicks: false },
    border: { display: false },
    ticks: { color: INK_MUTED, font: FONT, padding: 8, precision: 0, callback: (v) => (format ? format(v) : v) },
  }
  const labelAxis = {
    grid: { display: false },
    border: { color: GRID },
    ticks: { color: INK_MUTED, font: FONT, autoSkip: true, maxRotation: 0 },
  }
  return {
    responsive: true,
    maintainAspectRatio: false,
    indexAxis: horizontal ? 'y' : 'x',
    interaction: { mode: 'index', intersect: false },
    plugins: {
      legend: { display: false },
      tooltip: {
        backgroundColor: '#14110f',
        titleFont: { ...FONT, size: 12, weight: '500' },
        bodyFont: FONT,
        padding: 10,
        cornerRadius: 2,
        displayColors: false,
        callbacks: { label: (ctx) => ` ${ctx.dataset.label}: ${format ? format(ctx.parsed[horizontal ? 'x' : 'y']) : ctx.formattedValue}` },
      },
    },
    scales: horizontal ? { x: valueAxis, y: labelAxis } : { x: labelAxis, y: valueAxis },
  }
}

export function BarChart({ labels, values, label = 'Value', format, horizontal = false }) {
  return (
    <Bar
      options={baseOptions(format, horizontal)}
      data={{
        labels,
        datasets: [
          {
            label,
            data: values,
            backgroundColor: GOLD,
            hoverBackgroundColor: '#8f6c36',
            borderRadius: 4,
            borderSkipped: 'start',
            maxBarThickness: horizontal ? 18 : 28,
          },
        ],
      }}
    />
  )
}

export function LineChart({ labels, values, label = 'Value', format }) {
  return (
    <Line
      options={baseOptions(format)}
      data={{
        labels,
        datasets: [
          {
            label,
            data: values,
            borderColor: GOLD,
            backgroundColor: GOLD_FILL,
            fill: true,
            tension: 0.35,
            borderWidth: 2,
            pointRadius: 4,
            pointHoverRadius: 6,
            pointBackgroundColor: '#fff',
            pointBorderColor: GOLD,
            pointBorderWidth: 2,
          },
        ],
      }}
    />
  )
}

/**
 * Card wrapper for a chart with a Chart/Table toggle, so the numbers are always
 * available as text (screen readers, print, colour-blind readers).
 * `rows` = [{ label, value }] where value is already formatted.
 */
export function ChartCard({ title, subtitle, rows = [], valueLabel = 'Value', children, height = 270 }) {
  const [asTable, setAsTable] = useState(false)
  const empty = rows.length === 0

  return (
    <div className="card-jc h-100">
      <div className="card-jc-head">
        <div>
          <h5>{title}</h5>
          {subtitle && <div className="small text-muted-jc">{subtitle}</div>}
        </div>
        {!empty && (
          <button className="btn btn-outline-jc btn-sm" onClick={() => setAsTable((t) => !t)} aria-pressed={asTable}>
            <i className={`bi bi-${asTable ? 'bar-chart' : 'table'} me-1`} />
            {asTable ? 'Chart' : 'Table'}
          </button>
        )}
      </div>
      <div className="card-jc-body">
        {empty ? (
          <div className="d-flex align-items-center justify-content-center text-muted-jc small" style={{ height }}>
            No data for this period yet
          </div>
        ) : asTable ? (
          <div className="table-responsive" style={{ maxHeight: height, overflowY: 'auto' }}>
            <table className="table table-jc">
              <thead><tr><th>Label</th><th className="text-end">{valueLabel}</th></tr></thead>
              <tbody>
                {rows.map((r) => <tr key={r.label}><td>{r.label}</td><td className="text-end">{r.value}</td></tr>)}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="chart-box" style={{ height }}>{children}</div>
        )}
      </div>
    </div>
  )
}
