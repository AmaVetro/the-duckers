# The Duckers 🦆

The Duckers is a portfolio-grade fullstack e-commerce system designed to demonstrate real-world backend engineering practices such as transactional integrity, concurrency control, and deterministic testing.

It is designed as a **portfolio-grade backend project** that goes beyond academic CRUD implementations and demonstrates real-world backend engineering practices.

Key engineering aspects demonstrated in this project:

- Transactional integrity across **heterogeneous databases** (MySQL + MongoDB)
- **Concurrency-safe stock reservation** using atomic MongoDB updates
- Explicit **order state machine** (`PENDING → PAID → CANCELLED`)
- **Financially realistic pricing engine** (VAT 19%, conditional discounts, capped loyalty redemption)
- **Stateless JWT-based security** using the real Spring Security filter chain
- **Deterministic integration testing** using Testcontainers

The system is intentionally architected to be **explainable and defensible in technical interviews** from transactional, concurrency, and financial integrity perspectives.

---

# 🌐 Live Demo

 REMAINS TO BE DONE

Frontend: https://...
Backend API: https://...
Swagger: https://...

> ⚠️ The backend runs on Render free tier.  
> First request may take ~30s due to cold start.

---

# 🚀 Tech Stack

## Frontend
- React 18
- Vite
- React Router
- Bootstrap

The frontend provides a complete e-commerce interface including authentication, catalog browsing, product detail pages, cart management, checkout flow, and order history.

## Backend
- Java 21 (LTS)
- Spring Boot
- Spring Security
- JWT (stateless authentication)
- Spring Data JPA
- Spring Data MongoDB

## Databases
- **MySQL** — transactional domain data  
  (users, carts, orders, loyalty points, referrals)

- **MongoDB** — product catalog and stock management

## Infrastructure
- Docker
- Docker Compose (local development)
- Testcontainers (integration testing)
- Render (backend hosting)
- Railway (managed MySQL)
- MongoDB Atlas (managed MongoDB)
- Vercel (frontend hosting)
- GitHub Actions (CI pipeline)

---

# 🌐 Public Deployment

- Frontend deployed on **Vercel**
- Backend deployed on **Render**
- MySQL hosted on **Railway**
- MongoDB hosted on **MongoDB Atlas**

CI pipeline automatically builds and deploys the backend on every push.

---

# 🏗 Production Architecture

```
Frontend (Vercel)
        │
        ▼
Backend API (Render)
        │
        ├── MySQL (Railway)
        └── MongoDB (MongoDB Atlas)
```

The system intentionally separates **transactional data** from **catalog data**, allowing different consistency models and database capabilities to be used where they fit best.

---

# 📦 Project Structure

```
the-duckers/
├─ infra/        # Docker infrastructure (MySQL, MongoDB)
├─ backend/      # Spring Boot backend API
├─ frontend/     # React frontend
└─ README.md
```

---

# ▶️ Run Locally

### 1. Clone repository

```bash
git clone <REPOSITORY_URL>
cd the-duckers
```

### 2. Create environment file

```bash
cp .env.example .env
```

### 3. Start databases

```bash
docker compose -f infra/docker-compose.yml up -d
```

Services started:

- MySQL → `localhost:3306`
- MongoDB → `localhost:27017`

### 4. Start backend

```bash
cd backend
./mvnw spring-boot:run
```

### 5. Start frontend

```bash
cd frontend
npm install
npm run dev
```

---

# 🛒 Purchase Flow

The system implements a full end-to-end purchasing workflow:

```
Catalog → Cart → Checkout → Order Creation (PENDING) → Payment → Order Finalization (PAID)
```

Key guarantees:

- Cart prices are **snapshotted when items are added**
- Checkout converts the active cart into an **order (`PENDING`)**
- Payment finalizes the order (`PAID`)
- Orders can only be cancelled while in `PENDING`
- Loyalty points are emitted **only after successful payment**

---

# 💰 Financial Rules

The financial engine models realistic Chilean tax and discount rules.

Calculation pipeline:

```
subtotal
 − DUOC discount
 − points redemption
 = taxable base
 + VAT (19%)
 = final total
```

Rules implemented:

- **VAT (IVA) 19%** applied over the taxable base
- **DUOC domain discount (10%)** if email ends with `@duocuc.cl`
- Loyalty redemption conversion: **100 points = 1 CLP**
- Redemption cap: **30% of order subtotal**
- Points are emitted **only when the order transitions to `PAID`**

These rules ensure **mathematical consistency and economic sustainability** in the loyalty system.

---

# ⚙ Concurrency-Safe Stock Reservation

Stock is reserved using **atomic conditional updates in MongoDB**.

Properties:

- Update succeeds only if `stock ≥ requested quantity`
- Prevents overselling during concurrent checkouts
- No read-then-write race condition
- Database-level concurrency protection

If the SQL transaction fails after stock reservation, a **compensation update restores the stock**, ensuring consistency between MongoDB and MySQL.

---

# 🧪 Testing Strategy

Integration testing uses **Testcontainers** to spin up real database containers during test execution.

Key properties:

- MySQL + MongoDB containers started automatically
- Tests run in **isolated environments**
- Real **JWT authentication flow**
- Real **Spring Security filter chain**
- CI-compatible

### Validated flows

- User registration (`POST /auth/register`)
- User authentication (`POST /auth/login`)
- Add-to-cart
- Cart snapshot integrity
- Successful checkout
- Atomic stock reservation
- Insufficient stock validation
- Order lifecycle (`PENDING → PAID → CANCELLED`)
- Financial rule validation (VAT, DUOC discount, redemption cap)

This ensures **business logic correctness and transactional safety**.

---

# 🔐 Security Architecture

The backend uses **stateless JWT authentication** with Spring Security.

Key elements:

- Custom `JwtAuthenticationFilter`
- Full **SecurityFilterChain** execution
- Explicit **401 handling** for unauthenticated requests
- Protected API routes
- MockMvc integration tests aligned with the Servlet stack

---

# 🎖 Loyalty & Level System

The loyalty system rewards users based on real spending.

Rules:

- **1 CLP spent = 1 loyalty point**
- Redemption: **100 points = 1 CLP**
- Redemption capped at **30% of order subtotal**
- Points emitted only when **order is paid**

Levels are calculated using **historical total earned points**.

Level thresholds:

```
BRONZE   → 0
SILVER   → 250,000
GOLD     → 500,000
PLATINUM → 850,000
DIAMOND  → 1,500,000
```

Levels are **recalculated dynamically** and are intentionally **decoupled from financial logic**.

---

# 🤝 Referral System

Each user receives a **unique referral code**.

Referral rules:

- Referral codes can only be used during **registration**
- Both the **referrer** and the **referred user** receive **700,000 points**
- High reward values are used intentionally to make referral effects visible during testing.

This encourages user acquisition while integrating with the loyalty system.

---

# 📄 License

This project was built for **educational and portfolio purposes**.