import OrderManager from '../components/manage/OrderManager'

/** Staff can move orders to Confirmed, Processing and Packed. */
export default function StaffOrders() {
  return <OrderManager title="Orders" subtitle="Prepare customer orders. Staff can mark orders Confirmed, Processing or Packed." />
}
