import ReportView from '../components/manage/ReportView'

/** Store-level report: sales and products, without the payment breakdown. */
export default function StoreReports() {
  return <ReportView scope="store" />
}
