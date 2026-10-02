import { lazy, Suspense } from 'react'
import { Toaster } from 'react-hot-toast'
import { Route, Routes } from 'react-router-dom'
import Loading from './components/Loading'
import ProtectedRoute from './components/ProtectedRoute'
import ScrollToTop from './components/ScrollToTop'
import ShopLayout from './components/ShopLayout'
import DashboardLayout from './components/dashboard/DashboardLayout'
import { ROLES } from './utils/constants'

import Home from './pages/Home'
import Login from './pages/Login'
import Register from './pages/Register'
import VerifyEmail from './pages/VerifyEmail'
import Products from './pages/Products'
import ProductDetails from './pages/ProductDetails'
import Cart from './pages/Cart'
import Wishlist from './pages/Wishlist'
import Checkout from './pages/Checkout'
import Orders from './pages/Orders'
import OrderDetails from './pages/OrderDetails'
import Profile from './pages/Profile'
import NotFound from './pages/NotFound'
import Unauthorized from './pages/Unauthorized'

// Back-office screens are loaded on demand so shoppers never download them.
const AdminDashboard = lazy(() => import('./admin/AdminDashboard'))
const ManageUsers = lazy(() => import('./admin/ManageUsers'))
const ManageProducts = lazy(() => import('./admin/ManageProducts'))
const ManageCategories = lazy(() => import('./admin/ManageCategories'))
const ManageOrders = lazy(() => import('./admin/ManageOrders'))
const ManageInventory = lazy(() => import('./admin/ManageInventory'))
const ManagePayments = lazy(() => import('./admin/ManagePayments'))
const ManageOffers = lazy(() => import('./admin/ManageOffers'))
const Reports = lazy(() => import('./admin/Reports'))

const StoreDashboard = lazy(() => import('./store/StoreDashboard'))
const StoreProducts = lazy(() => import('./store/StoreProducts'))
const PriceManagement = lazy(() => import('./store/PriceManagement'))
const Inventory = lazy(() => import('./store/Inventory'))
const StoreOrders = lazy(() => import('./store/StoreOrders'))
const CustomerRequests = lazy(() => import('./store/CustomerRequests'))
const StoreReports = lazy(() => import('./store/StoreReports'))

const StaffDashboard = lazy(() => import('./staff/StaffDashboard'))
const StaffProducts = lazy(() => import('./staff/StaffProducts'))
const StaffOrders = lazy(() => import('./staff/StaffOrders'))

const DeliveryDashboard = lazy(() => import('./delivery/DeliveryDashboard'))
const AssignedOrders = lazy(() => import('./delivery/AssignedOrders'))
const DeliveryHistory = lazy(() => import('./delivery/DeliveryHistory'))

const { CUSTOMER, STAFF, STORE_MANAGER, DELIVERY_PARTNER, ADMIN } = ROLES

const NAV = {
  admin: [
    { to: '/admin', label: 'Dashboard', icon: 'grid-1x2', end: true },
    { to: '/admin/users', label: 'Users', icon: 'people' },
    { to: '/admin/products', label: 'Products', icon: 'gem' },
    { to: '/admin/categories', label: 'Categories', icon: 'tags' },
    { to: '/admin/pricing', label: 'Metal Rates & Pricing', icon: 'currency-rupee' },
    { to: '/admin/inventory', label: 'Inventory', icon: 'boxes' },
    { to: '/admin/orders', label: 'Orders', icon: 'receipt' },
    { to: '/admin/payments', label: 'Payments', icon: 'credit-card' },
    { to: '/admin/offers', label: 'Offers', icon: 'percent' },
    { to: '/admin/requests', label: 'Customer Requests', icon: 'chat-square-text' },
    { to: '/admin/reports', label: 'Reports', icon: 'bar-chart' },
    { to: '/admin/profile', label: 'My Profile', icon: 'person-circle' },
  ],
  store: [
    { to: '/store', label: 'Dashboard', icon: 'grid-1x2', end: true },
    { to: '/store/products', label: 'Products', icon: 'gem' },
    { to: '/store/categories', label: 'Categories', icon: 'tags' },
    { to: '/store/pricing', label: 'Price Management', icon: 'currency-rupee' },
    { to: '/store/inventory', label: 'Inventory', icon: 'boxes' },
    { to: '/store/orders', label: 'Orders', icon: 'receipt' },
    { to: '/store/requests', label: 'Customer Requests', icon: 'chat-square-text' },
    { to: '/store/reports', label: 'Reports', icon: 'bar-chart' },
    { to: '/store/profile', label: 'My Profile', icon: 'person-circle' },
  ],
  staff: [
    { to: '/staff', label: 'Dashboard', icon: 'grid-1x2', end: true },
    { to: '/staff/products', label: 'Products', icon: 'gem' },
    { to: '/staff/inventory', label: 'Inventory', icon: 'boxes' },
    { to: '/staff/orders', label: 'Orders', icon: 'receipt' },
    { to: '/staff/requests', label: 'Customer Requests', icon: 'chat-square-text' },
    { to: '/staff/profile', label: 'My Profile', icon: 'person-circle' },
  ],
  delivery: [
    { to: '/delivery', label: 'Dashboard', icon: 'grid-1x2', end: true },
    { to: '/delivery/orders', label: 'Assigned Orders', icon: 'truck' },
    { to: '/delivery/history', label: 'Delivery History', icon: 'clock-history' },
    { to: '/delivery/profile', label: 'My Profile', icon: 'person-circle' },
  ],
}

export default function App() {
  return (
    <>
      <ScrollToTop />
      <Toaster
        position="top-center"
        toastOptions={{
          style: { borderRadius: 0, background: '#14110f', color: '#f4ecdf', fontSize: '0.88rem', letterSpacing: '0.02em' },
          success: { iconTheme: { primary: '#c9a96e', secondary: '#14110f' } },
        }}
      />
      <Suspense fallback={<Loading fullPage />}>
        <Routes>
          {/* ---------------- storefront ---------------- */}
          <Route element={<ShopLayout />}>
            <Route index element={<Home />} />
            <Route path="products" element={<Products />} />
            <Route path="products/:id" element={<ProductDetails />} />
            <Route path="login" element={<Login />} />
            <Route path="register" element={<Register />} />
            <Route path="verify-email" element={<VerifyEmail />} />
            <Route path="cart" element={<Cart />} />
            <Route path="unauthorized" element={<Unauthorized />} />

            <Route element={<ProtectedRoute roles={[CUSTOMER]} />}>
              <Route path="wishlist" element={<Wishlist />} />
              <Route path="checkout" element={<Checkout />} />
              <Route path="orders" element={<Orders />} />
              <Route path="orders/:id" element={<OrderDetails />} />
            </Route>
            <Route element={<ProtectedRoute />}>
              <Route path="profile" element={<Profile />} />
            </Route>
            <Route path="*" element={<NotFound />} />
          </Route>

          {/* ---------------- admin ---------------- */}
          <Route path="admin" element={<ProtectedRoute roles={[ADMIN]}><DashboardLayout title="Admin" links={NAV.admin} /></ProtectedRoute>}>
            <Route index element={<AdminDashboard />} />
            <Route path="users" element={<ManageUsers />} />
            <Route path="products" element={<ManageProducts />} />
            <Route path="categories" element={<ManageCategories />} />
            <Route path="pricing" element={<PriceManagement />} />
            <Route path="inventory" element={<ManageInventory />} />
            <Route path="orders" element={<ManageOrders />} />
            <Route path="payments" element={<ManagePayments />} />
            <Route path="offers" element={<ManageOffers />} />
            <Route path="requests" element={<CustomerRequests />} />
            <Route path="reports" element={<Reports />} />
            <Route path="profile" element={<Profile embedded />} />
          </Route>

          {/* ---------------- store manager ---------------- */}
          <Route path="store" element={<ProtectedRoute roles={[STORE_MANAGER, ADMIN]}><DashboardLayout title="Store" links={NAV.store} /></ProtectedRoute>}>
            <Route index element={<StoreDashboard />} />
            <Route path="products" element={<StoreProducts />} />
            <Route path="categories" element={<ManageCategories />} />
            <Route path="pricing" element={<PriceManagement />} />
            <Route path="inventory" element={<Inventory />} />
            <Route path="orders" element={<StoreOrders />} />
            <Route path="requests" element={<CustomerRequests />} />
            <Route path="reports" element={<StoreReports />} />
            <Route path="profile" element={<Profile embedded />} />
          </Route>

          {/* ---------------- staff ---------------- */}
          <Route path="staff" element={<ProtectedRoute roles={[STAFF, STORE_MANAGER, ADMIN]}><DashboardLayout title="Staff" links={NAV.staff} /></ProtectedRoute>}>
            <Route index element={<StaffDashboard />} />
            <Route path="products" element={<StaffProducts />} />
            <Route path="inventory" element={<Inventory />} />
            <Route path="orders" element={<StaffOrders />} />
            <Route path="requests" element={<CustomerRequests />} />
            <Route path="profile" element={<Profile embedded />} />
          </Route>

          {/* ---------------- delivery partner ---------------- */}
          <Route path="delivery" element={<ProtectedRoute roles={[DELIVERY_PARTNER, ADMIN]}><DashboardLayout title="Delivery" links={NAV.delivery} /></ProtectedRoute>}>
            <Route index element={<DeliveryDashboard />} />
            <Route path="orders" element={<AssignedOrders />} />
            <Route path="history" element={<DeliveryHistory />} />
            <Route path="profile" element={<Profile embedded />} />
          </Route>
        </Routes>
      </Suspense>
    </>
  )
}
