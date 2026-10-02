import { useState } from 'react'
import useApi from '../../hooks/useApi'
import { dashboardService } from '../../services/dashboardService'
import { formatINR, formatINRShort, formatNumber, humanize, isoDate } from '../../utils/format'
import { BarChart, ChartCard, LineChart } from '../dashboard/Charts'
import StatCard, { DashHeader } from '../dashboard/StatCard'
import Loading from '../Loading'

const amountRows = (list = []) => list.map((x) => ({ label: x.label, value: formatINR(x.amount) }))
const countRows = (list = []) => list.map((x) => ({ label: humanize(x.label), value: formatNumber(x.count) }))

/** Turns the report into a CSV file the user can open in Excel. */
function downloadCsv(report, scope) {
  const lines = [['Section', 'Label', 'Count', 'Amount']]
  const add = (section, list = []) => list.forEach((x) => lines.push([section, x.label, x.count, x.amount ?? '']))
  lines.push(['Summary', 'Total orders', report.totalOrders, ''])
  lines.push(['Summary', 'Cancelled orders', report.cancelledOrders, ''])
  lines.push(['Summary', 'Total sales', '', report.totalSales])
  lines.push(['Summary', 'Average order value', '', report.averageOrderValue])
  if (scope === 'admin') lines.push(['Summary', 'Amount received', '', report.amountReceived])
  add('Monthly sales', report.monthlySales)
  add('Sales by category', report.salesByCategory)
  add('Top products', report.topProducts)
  add('Orders by status', report.ordersByStatus)
  add('Payments by method', report.paymentsByMethod)
  const csv = lines.map((l) => l.map((c) => `"${String(c ?? '').replace(/"/g, '""')}"`).join(',')).join('\n')
  const a = document.createElement('a')
  a.href = URL.createObjectURL(new Blob([csv], { type: 'text/csv' }))
  a.download = `jewelcraft-report-${report.from}-to-${report.to}.csv`
  a.click()
  URL.revokeObjectURL(a.href)
}

/** Sales report. scope="admin" adds payment figures; scope="store" is the store-level view. */
export default function ReportView({ scope }) {
  const today = new Date()
  const [range, setRange] = useState({
    from: isoDate(new Date(today.getFullYear(), today.getMonth() - 5, 1)),
    to: isoDate(today),
  })
  const [draft, setDraft] = useState(range)
  const { data, loading, error } = useApi(() => dashboardService.report(scope, range.from, range.to), [range.from, range.to])
  const LISTS = ['monthlySales', 'ordersByStatus', 'salesByCategory', 'topProducts']
  const r = data && { ...data, ...Object.fromEntries(LISTS.map((k) => [k, data[k] || []])) }

  return (
    <>
      <DashHeader title={scope === 'admin' ? 'Reports' : 'Store Reports'} subtitle="Sales performance for the selected period (cancelled orders excluded from sales).">
        <form
          className="filters-row"
          onSubmit={(e) => {
            e.preventDefault()
            setRange(draft)
          }}
        >
          <input type="date" className="form-control form-control-sm" value={draft.from} max={draft.to} onChange={(e) => setDraft({ ...draft, from: e.target.value })} aria-label="From" />
          <input type="date" className="form-control form-control-sm" value={draft.to} min={draft.from} max={isoDate()} onChange={(e) => setDraft({ ...draft, to: e.target.value })} aria-label="To" />
          <button className="btn btn-jc btn-sm">Apply</button>
          {r && <button type="button" className="btn btn-outline-jc btn-sm" onClick={() => downloadCsv(r, scope)}><i className="bi bi-download me-1" />CSV</button>}
        </form>
      </DashHeader>

      {loading && !r ? (
        <Loading />
      ) : error ? (
        <div className="card-jc p-4" style={{ color: 'var(--jc-bad)' }}>{error}</div>
      ) : (
        <>
          <div className="row g-3 mb-4">
            <div className="col-6 col-xl"><StatCard label="Total sales" value={formatINR(r.totalSales)} icon="currency-rupee" /></div>
            <div className="col-6 col-xl"><StatCard label="Orders" value={formatNumber(r.totalOrders)} icon="receipt" /></div>
            <div className="col-6 col-xl"><StatCard label="Avg. order value" value={formatINR(r.averageOrderValue)} icon="graph-up" /></div>
            <div className="col-6 col-xl"><StatCard label="Cancelled" value={formatNumber(r.cancelledOrders)} icon="x-circle" /></div>
            {scope === 'admin' && <div className="col-12 col-xl"><StatCard label="Amount received" value={formatINR(r.amountReceived)} icon="wallet2" /></div>}
          </div>

          <div className="row g-4">
            <div className="col-xl-8">
              <ChartCard title="Monthly sales" subtitle="Sales amount per month" rows={amountRows(r.monthlySales)} valueLabel="Sales">
                <LineChart labels={r.monthlySales.map((x) => x.label)} values={r.monthlySales.map((x) => Number(x.amount))} label="Sales" format={formatINRShort} />
              </ChartCard>
            </div>
            <div className="col-xl-4">
              <ChartCard title="Orders by status" rows={countRows(r.ordersByStatus)} valueLabel="Orders">
                <BarChart horizontal labels={r.ordersByStatus.map((x) => humanize(x.label))} values={r.ordersByStatus.map((x) => x.count)} label="Orders" />
              </ChartCard>
            </div>
            <div className="col-xl-6">
              <ChartCard title="Sales by category" rows={amountRows(r.salesByCategory)} valueLabel="Sales">
                <BarChart horizontal labels={r.salesByCategory.map((x) => x.label)} values={r.salesByCategory.map((x) => Number(x.amount))} label="Sales" format={formatINRShort} />
              </ChartCard>
            </div>
            <div className="col-xl-6">
              <ChartCard title="Top products" subtitle="By units sold" rows={r.topProducts.map((x) => ({ label: x.label, value: `${x.count} · ${formatINR(x.amount)}` }))} valueLabel="Units · Sales">
                <BarChart horizontal labels={r.topProducts.map((x) => x.label)} values={r.topProducts.map((x) => x.count)} label="Units sold" />
              </ChartCard>
            </div>
            {scope === 'admin' && r.paymentsByMethod && (
              <div className="col-xl-6">
                <ChartCard title="Payments by method" subtitle="Successful payments" rows={amountRows(r.paymentsByMethod)} valueLabel="Amount" height={200}>
                  <BarChart horizontal labels={r.paymentsByMethod.map((x) => x.label)} values={r.paymentsByMethod.map((x) => Number(x.amount))} label="Amount" format={formatINRShort} />
                </ChartCard>
              </div>
            )}
          </div>
        </>
      )}
    </>
  )
}
