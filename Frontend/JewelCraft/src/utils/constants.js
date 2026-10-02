export const ROLES = {
  CUSTOMER: 'CUSTOMER',
  STAFF: 'STAFF',
  STORE_MANAGER: 'STORE_MANAGER',
  DELIVERY_PARTNER: 'DELIVERY_PARTNER',
  ADMIN: 'ADMIN',
}

export const ROLE_LABELS = {
  CUSTOMER: 'Customer',
  STAFF: 'Staff',
  STORE_MANAGER: 'Store Manager',
  DELIVERY_PARTNER: 'Delivery Partner',
  ADMIN: 'Administrator',
}

/** Where each role lands after logging in. */
export const ROLE_HOME = {
  CUSTOMER: '/',
  STAFF: '/staff',
  STORE_MANAGER: '/store',
  DELIVERY_PARTNER: '/delivery',
  ADMIN: '/admin',
}

export const METAL_TYPES = ['GOLD', 'ROSE_GOLD', 'WHITE_GOLD', 'SILVER', 'PLATINUM']

/** Swatch colours for metal filters and image placeholders. */
export const METAL_TONES = {
  GOLD: { swatch: '#d4af6a', from: '#f6ecd6', to: '#e9d6ad', stroke: '#a8834a' },
  ROSE_GOLD: { swatch: '#d9a08b', from: '#f8e9e2', to: '#ecc9bb', stroke: '#a96a55' },
  WHITE_GOLD: { swatch: '#dcdcd6', from: '#f4f3ef', to: '#e2e0d9', stroke: '#8a877e' },
  SILVER: { swatch: '#c7c9cc', from: '#f2f3f4', to: '#dcdfe2', stroke: '#7d8389' },
  PLATINUM: { swatch: '#b9bcc0', from: '#eff0f1', to: '#d4d7da', stroke: '#6f747a' },
}

export const SORT_OPTIONS = [
  { value: 'newest', label: 'Newest' },
  { value: 'price_asc', label: 'Price: Low to High' },
  { value: 'price_desc', label: 'Price: High to Low' },
  { value: 'name_asc', label: 'Name: A to Z' },
  { value: 'weight_desc', label: 'Weight: Heaviest' },
]

export const ORDER_FLOW = ['PLACED', 'CONFIRMED', 'PROCESSING', 'PACKED', 'SHIPPED', 'OUT_FOR_DELIVERY', 'DELIVERED']
export const ORDER_STATUSES = [...ORDER_FLOW, 'CANCELLED']

/** Mirrors OrderStatus.TRANSITIONS on the server. */
export const ORDER_TRANSITIONS = {
  PLACED: ['CONFIRMED', 'CANCELLED'],
  CONFIRMED: ['PROCESSING', 'CANCELLED'],
  PROCESSING: ['PACKED', 'CANCELLED'],
  PACKED: ['SHIPPED', 'CANCELLED'],
  SHIPPED: ['OUT_FOR_DELIVERY'],
  OUT_FOR_DELIVERY: ['DELIVERED'],
  DELIVERED: [],
  CANCELLED: [],
}
/** What the store console can set; Shipped / Out for delivery / Delivered come only from the delivery partner. */
export const STORE_ORDER_STATUSES = ['CONFIRMED', 'PROCESSING', 'PACKED', 'CANCELLED']
/** Staff may only move orders through in-store preparation. */
export const STAFF_ORDER_STATUSES = ['CONFIRMED', 'PROCESSING', 'PACKED']
/** A delivery partner can be assigned while the order is in these states. */
export const ASSIGNABLE_STATUSES = ['CONFIRMED', 'PROCESSING', 'PACKED']

/** Mirrors DeliveryStatus.TRANSITIONS (ASSIGNED -> ACCEPTED happens via the accept button). */
export const DELIVERY_TRANSITIONS = {
  ASSIGNED: [],
  ACCEPTED: ['PICKED_UP'],
  PICKED_UP: ['OUT_FOR_DELIVERY', 'FAILED'],
  OUT_FOR_DELIVERY: ['DELIVERED', 'FAILED'],
  DELIVERED: [],
  FAILED: [],
}

export const PAYMENT_STATUSES = ['PENDING', 'SUCCESS', 'FAILED', 'REFUNDED']
export const PAYMENT_METHODS = ['COD', 'RAZORPAY']
export const REQUEST_TYPES = ['QUERY', 'RETURN', 'EXCHANGE', 'CUSTOMIZATION', 'COMPLAINT']
export const REQUEST_STATUSES = ['OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED']

export const INDIAN_STATES = [
  'Andhra Pradesh', 'Arunachal Pradesh', 'Assam', 'Bihar', 'Chhattisgarh', 'Goa', 'Gujarat', 'Haryana',
  'Himachal Pradesh', 'Jharkhand', 'Karnataka', 'Kerala', 'Madhya Pradesh', 'Maharashtra', 'Manipur', 'Meghalaya',
  'Mizoram', 'Nagaland', 'Odisha', 'Punjab', 'Rajasthan', 'Sikkim', 'Tamil Nadu', 'Telangana', 'Tripura',
  'Uttar Pradesh', 'Uttarakhand', 'West Bengal', 'Andaman and Nicobar Islands', 'Chandigarh',
  'Dadra and Nagar Haveli and Daman and Diu', 'Delhi', 'Jammu and Kashmir', 'Ladakh', 'Lakshadweep', 'Puducherry',
]

/** Same rules as the backend's @Pattern annotations. */
export const PATTERNS = {
  email: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
  phone: /^[6-9]\d{9}$/,
  pincode: /^\d{6}$/,
  password: /^(?=.*[A-Za-z])(?=.*\d).+$/,
  url: /^https?:\/\/.+/,
  coupon: /^[A-Za-z0-9_-]{3,30}$/,
  vehicle: /^[A-Za-z]{2}[ -]?\d{1,2}[ -]?[A-Za-z]{0,3}[ -]?\d{4}$/,
  licence: /^[A-Za-z]{2}[ -]?\d{2}[ -]?\d{4}[ -]?\d{7}$/,
  otp: /^\d{6}$/,
}

export const PRICE_RANGES = [
  { label: 'Under ₹25,000', min: '', max: '25000' },
  { label: '₹25,000 – ₹50,000', min: '25000', max: '50000' },
  { label: '₹50,000 – ₹1,00,000', min: '50000', max: '100000' },
  { label: '₹1,00,000 – ₹2,00,000', min: '100000', max: '200000' },
  { label: 'Above ₹2,00,000', min: '200000', max: '' },
]

// ---------------------------------------------------------------- sign-up

export const GENDERS = [
  { value: 'FEMALE', label: 'Female' },
  { value: 'MALE', label: 'Male' },
  { value: 'OTHER', label: 'Other' },
  { value: 'PREFER_NOT_TO_SAY', label: 'Prefer not to say' },
]

export const DEPARTMENTS = [
  { value: 'SALES', label: 'Sales & Styling', hint: 'Help customers choose their pieces' },
  { value: 'BILLING', label: 'Billing & Payments', hint: 'Invoices, payments and returns' },
  { value: 'INVENTORY', label: 'Inventory', hint: 'Stock, tagging and vault' },
  { value: 'CUSTOMER_SERVICE', label: 'Customer Service', hint: 'Queries, exchanges and after-sales' },
  { value: 'DESIGN_WORKSHOP', label: 'Design Workshop', hint: 'Custom designs and repairs' },
  { value: 'QUALITY_CHECK', label: 'Quality Check', hint: 'Hallmark and finish checks' },
]

export const VEHICLE_TYPES = [
  { value: 'BIKE', label: 'Motorbike', icon: 'speedometer2' },
  { value: 'SCOOTER', label: 'Scooter', icon: 'scooter' },
  { value: 'CAR', label: 'Car', icon: 'car-front' },
  { value: 'VAN', label: 'Van', icon: 'truck' },
  { value: 'BICYCLE', label: 'Bicycle', icon: 'bicycle' },
]

export const STORE_BRANCHES = [
  'MG Road, Bengaluru', 'Indiranagar, Bengaluru', 'Bandra West, Mumbai', 'Connaught Place, New Delhi',
  'T. Nagar, Chennai', 'Banjara Hills, Hyderabad', 'Park Street, Kolkata', 'Koregaon Park, Pune',
]

/** Which sign-up forms exist. ADMIN accounts are created by an existing admin, never self-registered. */
export const SIGNUP_ROLES = [
  { key: 'customer', role: 'CUSTOMER', label: 'Customer', icon: 'gem', tagline: 'Shop, save favourites and track orders' },
  { key: 'staff', role: 'STAFF', label: 'Store Staff', icon: 'person-badge', tagline: 'Join our store team' },
  { key: 'manager', role: 'STORE_MANAGER', label: 'Store Manager', icon: 'shop', tagline: 'Lead a JewelCraft boutique' },
  { key: 'delivery', role: 'DELIVERY_PARTNER', label: 'Delivery Partner', icon: 'truck', tagline: 'Deliver jewellery securely' },
  { key: 'admin', role: 'ADMIN', label: 'Admin', icon: 'shield-lock', tagline: 'Oversee the whole JewelCraft business' },
]

/** Where an administrator can be based: head office or any boutique. */
export const ADMIN_OFFICES = ['Head Office, Mumbai', ...STORE_BRANCHES]

export const ADMIN_DESIGNATIONS = [
  'Business Owner / Director', 'Operations Head', 'Finance & Accounts Head', 'IT / Systems Administrator', 'Regional Manager',
]
