# JewelCraft – Database

MySQL database **`JewelCraft`** (the Spring Boot app creates and updates the tables automatically).
MySQL itself keeps the live data in its own data directory
(`C:\ProgramData\MySQL\MySQL Server 9.7\Data\`); this folder holds a full copy you can keep with the project.

| File | What it is |
|---|---|
| `JewelCraft.sql` | Full export: all 24 tables, the `user_accounts` view and **all data**. |
| `backup-database.bat` | Double-click to refresh `JewelCraft.sql` with the current data. |
| `restore-database.bat` | Double-click to load `JewelCraft.sql` back into MySQL (replaces the current data). |
| `user-queries.sql` | Ready-made queries: users by role, pending applications, profiles, OTPs, order tracking. |

## Users, roles and passwords

- `roles` – CUSTOMER, STAFF, STORE_MANAGER, DELIVERY_PARTNER, ADMIN
- `users` – email (the username), name, phone, `role_id` → `roles.id`,
  `password` as a **BCrypt hash** (`$2a$10$…`), `email_verified`, `approval_status` (APPROVED / PENDING / REJECTED).
- `user_accounts` – a view joining the two: `username`, `role`, `password_bcrypt`, `email_verified`, `approval_status`, `enabled`.

## Sign-up details per role (one-to-one with `users`)

| Table | Role | Holds |
|---|---|---|
| `customer_profiles` | CUSTOMER | gender, birthday, anniversary, favourite metal, offers opt-in |
| `staff_profiles` | STAFF, STORE_MANAGER | employee code, department, designation, branch, experience, qualification |
| `delivery_partner_profiles` | DELIVERY_PARTNER | vehicle type & number, driving licence, service city & pincode, emergency contact |

## Email verification (OTP)

`email_otps` – one row per code sent: email, **BCrypt hash of the code** (never the code itself),
expiry (10 min), wrong attempts (max 5) and whether it was used.

## Live order tracking

`order_tracking_events` – every step of an order (placed, confirmed, packed, picked up, out for delivery,
delivered, cancelled, refunds…) with time, who did it and the message the customer sees.

```sql
SELECT username, role, approval_status, password_bcrypt FROM JewelCraft.user_accounts ORDER BY role;
```

## Moving to another computer

1. Install MySQL, then double-click `restore-database.bat` (or run `mysql -u root -p < JewelCraft.sql`).
2. Set `DB_USERNAME` / `DB_PASSWORD` if they differ from `root` / `root`, and start the backend.
