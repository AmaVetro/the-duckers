# The Duckers 🦆

The Duckers is a fullstack e-commerce system focused on gamer products.

It is designed as a portfolio-grade backend project that goes beyond academic CRUD implementations.  
The system demonstrates:

- Transactional integrity across heterogeneous databases (MySQL + MongoDB)
- Concurrency-safe stock reservation using atomic MongoDB updates
- Explicit order state machine (`PENDING → PAID → CANCELLED`)
- Financially realistic pricing engine (VAT 19%, conditional domain discounts, capped loyalty redemption)
- Stateless JWT-based security with real filter-chain execution
- Deterministic integration testing using Testcontainers

The project is intentionally architected to be explainable and defensible in technical interviews from transactional, concurrency, and financial integrity perspectives.

---

## 🚀 Tech Stack

### Frontend
- React
- Vite

The React frontend provides a functional e-commerce interface connected
to the backend API, including authentication, catalog browsing,
product detail views, cart management and checkout flow.

### Backend
- Java 21 LTS
- Spring Boot 3.2.5
- Spring Security 6.2.x
- JWT (stateless authentication)

### Databases
- MySQL (transactional domain: users, carts, orders)
- MongoDB (product catalog & stock management)

### Infrastructure
- Docker
- Docker Compose (local development)
- Testcontainers (integration testing)
- Render (backend hosting)
- Railway (managed MySQL)
- MongoDB Atlas (managed MongoDB)
- Vercel (frontend hosting)

---

## 🌍 Public Deployment

- Public backend API (Render)
- Public frontend (Vercel)
- MySQL (Railway managed database)
- MongoDB Atlas
- Production profile configuration
- CI pipeline enforced before deploy

---

### Production Architecture

Frontend → Vercel  
Backend → Render  
MySQL → Railway  
MongoDB → MongoDB Atlas

---

## 📦 Project Structure

```
the-duckers/
├─ infra/        # Docker infrastructure (MySQL, MongoDB)
├─ backend/      # Spring Boot backend (API)
├─ frontend/     # React frontend
└─ README.md
```

---

## ▶️ Run Locally

### 1. Clone repository

```bash
git clone <REPOSITORY_URL>
cd the-duckers
```

### 2. Create environment file

```bash
cp .env.example .env
```

### 3. Start infrastructure

```bash
docker compose -f infra/docker-compose.yml up -d
```

Services started:
- MySQL → `localhost:3306`
- MongoDB → `localhost:27017`

### 4. Reset environment (if needed)

```bash
docker compose -f infra/docker-compose.yml down --volumes
docker compose -f infra/docker-compose.yml up -d
```

### 5. Start the backend API

```bash
cd backend
./mvnw spring-boot:run

---

# 🛒 Core Architecture Overview

## Shopping Cart Design

- One **ACTIVE cart per user**
- Persistent (stored in MySQL)
- Not session-based
- Lazy creation on first item addition
- Items use independent `itemId`
- Prices are snapshotted at add-to-cart time

This guarantees pricing stability between cart and checkout.

---

# 🔥 Checkout & Order Lifecycle

Checkout converts:

```
ACTIVE CART → ORDER (PENDING)
```

Order state machine:

```
PENDING → PAID → CANCELLED
```

### Guarantees

- Stock validation
- Atomic stock reservation
- Concurrency safety
- Order creation inside SQL transaction
- Cart closure
- Explicit state transition validation
- No distributed transactions (no XA)

---

## 💰 Financial Rules

The financial engine models realistic Chilean tax and discount rules:

- **VAT (IVA) 19%** applied over the taxable base.
- **DUOC domain discount (10%)** applied when email ends with `@duocuc.cl`.
- Loyalty redemption conversion: **100 points = 1 CLP**.
- **Redemption cap: 30%** of order subtotal.
- Points are emitted only after `ORDER → PAID`.

This ensures mathematical consistency and economic sustainability.

---

## ⚙ Atomic MongoDB Stock Reservation

Stock is reserved using a conditional atomic update:

```java
Query query = new Query(
    Criteria.where("_id").is(productId)
            .and("stock").gte(quantity)
);

Update update = new Update().inc("stock", -quantity);

UpdateResult result =
    mongoTemplate.updateFirst(query, update, ProductDocument.class);

boolean reserved = result.getModifiedCount() == 1;
```

### Properties

- Update succeeds only if `stock >= quantity`
- No read-then-write race condition
- Safe under concurrent checkout attempts
- Database-level concurrency protection

---

## 🔄 Compensation Strategy

Because MySQL and MongoDB do not share a distributed transaction:

1. Stock is reserved atomically in MongoDB.
2. Order creation runs inside `@Transactional`.

If failure occurs after stock reservation but before SQL commit:

```javascript
$inc: { stock: +quantity }
```

Stock is restored via manual compensation.

This ensures eventual consistency across data stores.

---

# 🧪 Testing Strategy

## Integration Testing

- Testcontainers (MySQL + MongoDB)
- Isolated per test execution
- CI-compatible
- Real JWT authentication
- Real SecurityFilterChain execution
- No embedded HTTP server

### Validated Flows

- User registration (`POST /auth/register`)
- User authentication (`POST /auth/login`)
- Add-to-cart
- Snapshot verification
- Successful checkout reduces stock atomically
- Insufficient stock returns 400 without stock mutation
- Explicit 401 validation when JWT is missing
- Order lifecycle transitions (`PENDING → PAID → CANCELLED`)
- Financial rule validation (VAT, DUOC discount, redemption cap)

Integration tests explicitly validate financial correctness and state machine integrity.

---

# 🔐 Security Architecture

- Stateless JWT authentication
- Custom `JwtAuthenticationFilter`
- Spring Security 6 filter chain
- Explicit 401 handling
- MockMvc-based integration testing aligned with Servlet stack

---

# 🏁 Architectural Highlights

- Stateless backend design
- Concurrency-safe checkout
- Atomic stock reservation in MongoDB
- SQL transactional integrity
- Explicit order state machine
- Financially realistic (VAT + conditional discounts)
- Deterministic integration tests
- Containerized local & CI environment

This project is designed to be explainable in technical interviews from both transactional, concurrency, and financial integrity perspectives.

---

# 🎖 Loyalty & Level System

- Points are emitted after successful payment (`ORDER → PAID`).
- Conversion: **1 CLP = 1 point**.
- Redemption: **100 points = 1 CLP** (1% real cashback).
- Max redemption cap: **30%** of order subtotal.
- Levels are based on `totalEarned` (historical spend).
- Levels are recalculated dynamically.
- Levels do not affect pricing or financial logic.

This ensures economic sustainability while keeping financial rules decoupled from reputation mechanics.

---

# Referral System

- Each user receives a unique referral code.
- Referral codes can be used only during registration.
- Both the referrer and the referred user receive 700,000 points.

Level Thresholds

- BRONZE   → 0
- SILVER   → 250,000
- GOLD     → 500,000
- PLATINUM → 850,000
- DIAMOND  → 1,500,000

---

## 📄 License

Educational and portfolio purposes.
