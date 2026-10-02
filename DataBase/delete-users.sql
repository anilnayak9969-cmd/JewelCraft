-- =====================================================================
-- Permanently deletes two customer accounts and everything linked to them:
--   anilnayak9969@gmail.com   (users.id 16)
--   capricorn28002@gmail.com  (users.id 15)
--
-- Removes: orders (+ items, payments, deliveries, tracking), addresses, cart,
-- wishlist, reviews, customer requests, profiles and email OTPs.
-- Stock still reserved by their open orders is put back into inventory.
--
-- Run the whole file in MySQL Workbench (lightning-bolt icon, not "execute current statement").
-- Back up first if you might want this data again: DataBase\backup-database.bat
-- =====================================================================

USE JewelCraft;
SET SQL_SAFE_UPDATES = 0;

-- 1) Preview: check these are the right accounts before anything is deleted
SELECT id, email, first_name, last_name, phone FROM users
WHERE email IN ('anilnayak9969@gmail.com', 'capricorn28002@gmail.com');

START TRANSACTION;

SET @u1 = (SELECT id FROM users WHERE email = 'anilnayak9969@gmail.com');
SET @u2 = (SELECT id FROM users WHERE email = 'capricorn28002@gmail.com');

-- 2) Give back stock held by their orders that were neither delivered nor cancelled
UPDATE inventory i
JOIN (SELECT oi.product_id, SUM(oi.quantity) AS qty
      FROM order_items oi JOIN orders o ON o.id = oi.order_id
      WHERE o.user_id IN (@u1, @u2) AND o.status NOT IN ('DELIVERED', 'CANCELLED')
      GROUP BY oi.product_id) held ON held.product_id = i.product_id
SET i.quantity = i.quantity + held.qty;

-- 3) Everything hanging off their orders
DELETE FROM order_tracking_events WHERE order_id IN (SELECT id FROM orders WHERE user_id IN (@u1, @u2));
DELETE FROM payments              WHERE order_id IN (SELECT id FROM orders WHERE user_id IN (@u1, @u2));
DELETE FROM deliveries            WHERE order_id IN (SELECT id FROM orders WHERE user_id IN (@u1, @u2));
DELETE FROM order_items           WHERE order_id IN (SELECT id FROM orders WHERE user_id IN (@u1, @u2));
DELETE FROM customer_requests     WHERE user_id IN (@u1, @u2)
                                     OR order_id IN (SELECT id FROM orders WHERE user_id IN (@u1, @u2));
DELETE FROM orders                WHERE user_id IN (@u1, @u2);

-- 4) Cart and wishlist
DELETE FROM cart_items     WHERE cart_id     IN (SELECT id FROM cart     WHERE user_id IN (@u1, @u2));
DELETE FROM cart           WHERE user_id IN (@u1, @u2);
DELETE FROM wishlist_items WHERE wishlist_id IN (SELECT id FROM wishlist WHERE user_id IN (@u1, @u2));
DELETE FROM wishlist       WHERE user_id IN (@u1, @u2);

-- 5) Everything else tied to the user
UPDATE customer_requests SET handled_by = NULL WHERE handled_by IN (@u1, @u2);
DELETE FROM deliveries WHERE delivery_partner_id IN (@u1, @u2);
DELETE FROM reviews                   WHERE user_id IN (@u1, @u2);
DELETE FROM addresses                 WHERE user_id IN (@u1, @u2);
DELETE FROM customer_profiles         WHERE user_id IN (@u1, @u2);
DELETE FROM staff_profiles            WHERE user_id IN (@u1, @u2);
DELETE FROM delivery_partner_profiles WHERE user_id IN (@u1, @u2);

-- 6) OTP codes (stored by email, not user id) and the users themselves
DELETE FROM email_otps WHERE email IN ('anilnayak9969@gmail.com', 'capricorn28002@gmail.com');
DELETE FROM users      WHERE email IN ('anilnayak9969@gmail.com', 'capricorn28002@gmail.com');

COMMIT;
SET SQL_SAFE_UPDATES = 1;

-- 7) Check: both should return no rows
SELECT id, email FROM users      WHERE email IN ('anilnayak9969@gmail.com', 'capricorn28002@gmail.com');
SELECT id, email FROM email_otps WHERE email IN ('anilnayak9969@gmail.com', 'capricorn28002@gmail.com');
