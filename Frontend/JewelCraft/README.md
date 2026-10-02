# JewelCraft – Frontend

React 19 + Vite storefront and back-office for **JewelCraft – Jewellery E-Commerce & Store Management System**.
Bootstrap 5, React Router, React Hook Form, Axios, Chart.js.

## Run

```bash
# 1. start the Spring Boot backend on http://localhost:8080 (JewelCraft/ folder)
# 2. then:
npm install
npm run dev        # http://localhost:5173
```

In development Vite proxies `/api` and `/uploads` to `localhost:8080` (see `vite.config.js`).
For production set `VITE_API_URL` to the backend URL (see `.env.example`) and run `npm run build`.

## Demo logins (created by the backend when `app.seed.demo-data=true`)

| Role | Email | Password |
|---|---|---|
| Customer | customer@jewelcraft.com | Customer@123 |
| Admin | admin@jewelcraft.com | Admin@123 |
| Store Manager | manager@jewelcraft.com | Manager@123 |
| Staff | staff@jewelcraft.com | Staff@123 |
| Delivery Partner | delivery@jewelcraft.com | Delivery@123 |

The sign-in page shows one-click buttons for these in development only
(or when `VITE_SHOW_DEMO_LOGINS=true`).

## Structure

```
src/
  components/          shared UI (Navbar, Footer, ProductCard, ProductFilter, SearchBar, Modal, …)
    dashboard/         back-office shell, stat cards, charts
    manage/            screens shared by staff / store manager / admin (products, orders, inventory, reports)
  pages/               customer pages (Home, Products, ProductDetails, Cart, Checkout, Orders, Profile, …)
  admin/ store/ staff/ delivery/   role areas (lazy-loaded)
  context/             AuthContext, CartContext, WishlistContext
  hooks/               useAuth, useCart, useWishlist, useApi
  services/            Axios API calls, one file per area
  utils/               formatting, constants, price estimate, Razorpay helper
```

## Roles → areas

| Area | URL | Roles |
|---|---|---|
| Shop, cart, checkout, orders | `/` | CUSTOMER |
| Admin console | `/admin` | ADMIN |
| Store console | `/store` | STORE_MANAGER, ADMIN |
| Staff console | `/staff` | STAFF, STORE_MANAGER, ADMIN |
| Delivery console | `/delivery` | DELIVERY_PARTNER, ADMIN |

Routes are guarded by `ProtectedRoute`; the backend enforces the same rules with Spring Security.
Prices are never sent from React – the server calculates them from the live metal rate.
