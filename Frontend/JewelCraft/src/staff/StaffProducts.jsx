import ProductManager from '../components/manage/ProductManager'

/** Staff can add and edit products and images; hiding/deleting is for managers. */
export default function StaffProducts() {
  return <ProductManager title="Products" />
}
