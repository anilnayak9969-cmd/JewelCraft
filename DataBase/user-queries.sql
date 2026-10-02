-- =====================================================================
-- JewelCraft – handy queries for users, roles, sign-up and order tracking
-- Run in MySQL Workbench (or: mysql -u root -p < user-queries.sql)
--
-- Passwords and email OTP codes are stored ONLY as BCrypt hashes ($2a$10$..., 60 chars).
-- A hash cannot be turned back into the original; the app hashes what is typed and compares.
-- =====================================================================
USE JewelCraft;

-- 1. Every user: username (email), role, encrypted password and account state
SELECT id, username, full_name, role, password_bcrypt, email_verified, approval_status, enabled, created_at
FROM user_accounts
ORDER BY role, id;

-- 2. How many users each role has
SELECT role, COUNT(*) AS users
FROM user_accounts
GROUP BY role
ORDER BY role;

-- 3. The roles (no SUPER_ADMIN – ADMIN is the highest role)
SELECT id, name FROM roles ORDER BY id;

-- 4. Applications waiting for an admin (staff / store manager / delivery partner sign-ups)
SELECT id, username, full_name, role, email_verified, created_at
FROM user_accounts
WHERE approval_status = 'PENDING'
ORDER BY created_at;

-- 5. Accounts that signed up but never entered their email code
SELECT id, username, role, created_at
FROM user_accounts
WHERE email_verified = 0;

-- 6. Customer sign-up details (birthday / anniversary offers)
SELECT ua.username, ua.full_name, cp.gender, cp.date_of_birth, cp.anniversary_date, cp.preferred_metal, cp.marketing_opt_in
FROM customer_profiles cp
JOIN user_accounts ua ON ua.id = cp.user_id;

-- 7. Staff & store managers with their employee details
SELECT ua.username, ua.full_name, ua.role, sp.employee_code, sp.department, sp.designation,
       sp.store_branch, sp.experience_years, sp.qualification, ua.approval_status
FROM staff_profiles sp
JOIN user_accounts ua ON ua.id = sp.user_id
ORDER BY sp.employee_code;

-- 8. Delivery partners with vehicle and service area
SELECT ua.username, ua.full_name, dp.vehicle_type, dp.vehicle_number, dp.driving_license_number,
       dp.service_city, dp.service_pincode, ua.approval_status
FROM delivery_partner_profiles dp
JOIN user_accounts ua ON ua.id = dp.user_id;

-- 9. Email OTPs – only hashes are stored; shows expiry, attempts and whether used
SELECT email, purpose, LEFT(code_hash, 20) AS code_hash_start, expires_at, attempts, consumed, created_at
FROM email_otps
ORDER BY created_at DESC
LIMIT 20;

-- 10. Live tracking history of one order (change the order number)
SELECT o.order_number, e.created_at, e.event, e.order_status, e.actor_name, e.actor_role, e.message
FROM order_tracking_events e
JOIN orders o ON o.id = e.order_id
WHERE o.order_number = 'JC261001-980269'
ORDER BY e.created_at, e.id;

-- 11. Safety check: any password that is NOT a BCrypt hash? (should return 0 rows)
SELECT id, username
FROM user_accounts
WHERE password_bcrypt NOT LIKE '$2a$%' AND password_bcrypt NOT LIKE '$2b$%';

-- ---------------------------------------------------------------------
-- Create users through the app (Register page with OTP, or Admin → Users),
-- NOT with a plain INSERT – the backend hashes the password with BCrypt.
-- Approve an application from Admin → Users → Pending approval (this also emails the applicant).
-- ---------------------------------------------------------------------
