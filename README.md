# OrganicMart — Farm-to-Customer Organic Marketplace

A Spring Boot 3 / Java 17 full-stack web application connecting verified organic
farmers directly with customers.

---

## 1. Prerequisites

Install these on your machine before running the project:

- **Java 17** (JDK) — `java -version` should show 17.x
- **Maven 3.9+** — `mvn -version` (or use your IDE's bundled Maven)
- **MySQL 8** — running locally, plus MySQL Workbench (optional, for viewing data)
- An IDE: **IntelliJ IDEA** (recommended) or Eclipse / VS Code with Java extensions

---

## 2. Database Setup

1. Open MySQL Workbench (or the `mysql` CLI) and run the script at:
   `src/main/resources/db/schema.sql`

   This creates the `organicmart_db` database, all 27 tables, and seeds the
   4 roles + 11 product categories.

   > You can skip this step if you prefer — Hibernate is configured with
   > `spring.jpa.hibernate.ddl-auto=update`, so tables will also be
   > auto-created on first run. Running the script manually is recommended
   > for review, since it documents the full 3NF schema.

2. Update your DB credentials in
   `src/main/resources/application.properties`:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=your_mysql_password
   ```

---

## 3. Email (OTP) Setup

The app sends OTP and notification emails via Gmail SMTP. To make this work:

1. Enable 2-Step Verification on a Gmail account.
2. Generate an **App Password**: Google Account → Security → App Passwords.
3. Update `application.properties`:
   ```properties
   spring.mail.username=your-email@gmail.com
   spring.mail.password=your-16-char-app-password
   app.mail.from=OrganicMart <your-email@gmail.com>
   ```

If you skip this, registration/OTP flows will fail silently on the email
step (the error is logged, not thrown) — the OTP row is still created in
the DB, so for local testing you can check the `otp_verifications` table
directly instead of your inbox.

---

## 4. Running the Project

### Option A — IDE (recommended)
1. Open the `organicmart` folder as a Maven project in IntelliJ/Eclipse.
2. Let it download dependencies (first import takes a few minutes).
3. Run `OrganicMartApplication.java`.

### Option B — Command line
```bash
cd organicmart
mvn clean install
mvn spring-boot:run
```

The app starts at **http://localhost:8080**

---

## 5. Default Login

A default Admin account is seeded automatically on first startup:

| Field    | Value                  |
|----------|------------------------|
| Email    | admin@organicmart.com  |
| Password | Admin@123              |

**Change this password after first login in a real deployment.**

Farmers, Customers, and Delivery Partners register themselves via
`/auth/register` (Delivery Partner self-registration UI is not wired in
this build — see "What's Scaffolded" below — add delivery partner accounts
directly via SQL or extend `AuthController` to support the `ROLE_DELIVERY`
signup path).

---

## 6. What's Fully Built vs. Scaffolded

This is a large spec (30 tables, 4 dashboards, OTP, certificate review,
cart/checkout, reports). Everything below is **fully wired and functional**:

- Spring Security with BCrypt, role-based access, CSRF, custom 403/404/500 pages
- Registration + Email OTP verification + Login + Forgot/Reset Password
- Farmer profile completion, Aadhaar/bank details, organic certificate upload
- Admin certificate approval/rejection (with email notification)
- Product catalog (create, list, search, category filter, multi-image upload)
- Cart, Wishlist, Checkout, Address management, Order placement
- Order status history + email notifications on status change
- Admin dashboard (farmer/customer/product/order counts, revenue)
- Delivery partner dashboard (assigned orders, status updates)
- Custom green/white themed UI (Bootstrap 5 + custom CSS, no default look)

**Scaffolded / left as extension points** (DB tables + entities exist,
service-layer methods are stubbed or straightforward to add):

- Delivery Partner self-registration form (entity + admin-approval flow exists;
  add a `/auth/register-delivery` form following the same OTP pattern as
  customer/farmer registration)
- Reviews & Ratings UI (entity + repository exist; add a small controller +
  form on the product detail page)
- Coupons at checkout (entity + repository exist; apply discount logic in
  `OrderServiceImpl.placeOrder`)
- PDF/Excel report export (`itext7` and `apache-poi` are already in `pom.xml`;
  add an `AdminReportController` that queries `OrderRepository` and streams
  a file)
- Chart.js dashboard graphs (data is available via `AdminService.getDashboardStats()`;
  add a `<canvas>` + fetch call in `admin/dashboard.html`)

---

## 7. Project Structure

```
organicmart/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/organicmart/
    │   ├── config/         # Security, DataSeeder, WebMvcConfig
    │   ├── controller/      # MVC controllers
    │   ├── dto/             # Request DTOs with validation
    │   ├── entity/          # 27 JPA entities
    │   ├── exception/       # Global exception handler
    │   ├── repository/      # Spring Data JPA repositories
    │   ├── security/        # UserDetails implementation
    │   ├── service/         # Service interfaces
    │   ├── serviceimpl/     # Service implementations
    │   └── util/            # OTP + file storage helpers
    └── resources/
        ├── application.properties
        ├── db/schema.sql     # Full 3NF schema (reference/manual setup)
        ├── static/css/style.css
        ├── static/js/main.js
        └── templates/        # Thymeleaf views (auth, farmer, customer, admin, delivery, errors)
```

---

## 8. Architecture

Layered architecture throughout:

```
Controller → Service (interface) → ServiceImpl → Repository → Database
```

- DTOs decouple form input from entities (`RegisterRequest`, `ProductRequest`, `FarmerProfileRequest`)
- All business rules live in `serviceimpl/`, never in controllers
- Passwords are BCrypt-hashed, never stored/logged in plain text
- CSRF protection is Spring Security's default (never disabled)
- File uploads (certificates, Aadhaar, product images, profile photos) are
  stored under `./uploads/` on disk and served via `/uploads/**`

---

## 9. Bilingual Support (English / Gujarati)

The site now has an **EN / ગુ** switcher button in the top navbar. Clicking
it reloads the current page in the chosen language and remembers your
choice in a cookie for 30 days.

**How it works:**
- `src/main/resources/messages.properties` — English text
- `src/main/resources/messages_gu.properties` — Gujarati text
- `config/LocaleConfig.java` — wires up the `?lang=en` / `?lang=gu` switch
- Templates use `th:text="#{key.name}"` instead of hardcoded text

**Currently translated:** navbar, footer, home page, login, register, OTP
verification, farmer dashboard, add-product form.

**Not yet translated** (still English-only): checkout, orders, admin pages,
delivery dashboard, product detail page. To translate any of these:
1. Add the new text as a `key=value` pair in both `messages.properties`
   and `messages_gu.properties`
2. In the template, replace hardcoded text with `th:text="#{your.key}"`

**ગુજરાતીમાં:** Navbar માં **EN / ગુ** button ઉમેર્યું છે. Click કરો એટલે
current page એ language માં ફરી load થાય છે, અને પસંદગી 30 દિવસ સુધી
cookie માં યાદ રહે છે. Home, Login, Register, OTP, Farmer Dashboard, Add
Product — આ pages already બંને ભાષામાં કામ કરે છે. બાકીના pages (checkout,
orders, admin, delivery) હજુ ફક્ત English માં છે — એ translate કરવા માટે,
ઉપર આપેલા 2 steps follow કરો.

---

## 10. Uploads Folder
(not inside `src`, so it won't clutter your repo). Add `uploads/` to
`.gitignore` before committing to a real Git repo.
