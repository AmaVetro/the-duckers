# The Duckers 🦆

The Duckers is a production-style fullstack e-commerce system designed to demonstrate **real-world backend engineering** under realistic constraints.

It models a complete purchase lifecycle with transactional integrity, concurrency-safe stock handling, and financially consistent calculations.

Unlike typical CRUD projects, this system focuses on correctness under real-world scenarios: concurrent checkouts, atomic operations, and strict financial rules.


---


# 🌐 Live Demo

- Frontend: https://the-duckers.vercel.app (frontend initialization may take up to **2 minutes**)
- Backend API: https://the-duckers.onrender.com
- Swagger: https://the-duckers.onrender.com/swagger-ui.html

⚠️ **Important**: the frontend initialization may take up to **2 minutes** due to Cold Start 
(Render Free Tier).

⚠️ **Important**: on Render's free tier, the system spins down the service after **10 minutes of inactivity**.

This delay is mainly due to:
- Spring Boot application startup
- Initial database connections (MySQL + MongoDB Atlas cluster)

Subsequent requests are fast once the service is warm.


---


## 🎬 Video Demo

### Feature Demos
- Normal flow without discounts → [Link](https://youtu.be/SjyB0ADXDRs)
- Discounts and referral code → [Link](https://youtu.be/iKweOwaJQ4Q)
- Order cancelation and restoring stock → [Link](https://youtu.be/h9NXZJ_vmY0)
- Protected Routes and JWT roken → [Link](https://youtu.be/n5YDOQkBduI)


---


### 🧪 Suggested Demo Flow

1. Register a new user  
2. Browse the catalog and open a product  
3. Add items to the cart  
4. Proceed to checkout and observe discounts if applied
5. Complete payment  
6. Verify order appears in "My Account"  
7. Observe points earned and order status

> This demonstrates the full transactional flow of the system.


---


## ⚙️ Core Engineering Highlights

- Transactional integrity across heterogeneous databases (MySQL + MongoDB)
- Concurrency-safe stock reservation using atomic MongoDB updates
- Full purchase lifecycle (Cart → Checkout → Payment → Orders)
- Explicit order state machine (`PENDING → PAID → CANCELLED`)
- Financially realistic pricing engine (VAT 19%, conditional discounts, capped loyalty redemption)
- **Stateless JWT-based security** using the real **Spring Security filter chain**
- Deterministic integration testing using **Testcontainers**

The system is intentionally designed to be explainable and defensible in technical interviews, focusing on transactional integrity, concurrency, and financial correctness.


---


# 🚀 Tech Stack

## Frontend
- React 18
- Vite
- React Router
- Bootstrap

## Backend
- Java 21 (LTS)
- Spring Boot
- Spring Security
- JWT (stateless authentication)
- Spring Data JPA
- Spring Data MongoDB

## Databases
- MySQL — transactional domain data  
  (users, levels, carts, orders, user points, loyalty points, shopping carts, shopping cart items, referrals)

- MongoDB — product catalog, categories, and stock management

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


# 🏗 Production Architecture
```text
Frontend (Vercel)
        │
        ▼
Backend API (Render)
        │
        ├── MySQL (Railway)
        └── MongoDB (MongoDB Atlas)
```
The system intentionally separates transactional data from catalog data, allowing different consistency models and database capabilities to be used where they fit best.


---


## 🔄 System Flow

User → Cart → Checkout → Order (PENDING) → Payment → Order (PAID)

- Cart is snapshotted at checkout
- Orders start as `PENDING`
- Payment transitions order to `PAID`
- Points are emitted only after successful payment


---


# 📦 Project Structure
```text
the-duckers/
├─ infra/        # Docker infrastructure (MySQL, MongoDB)
├─ backend/      # Spring Boot backend API
├─ frontend/     # React frontend
└─ README.md
```

---


# 💰 Financial Rules

The financial engine models realistic Chilean tax and discount rules.

```bash
subtotal
− discounts (DUOC + points)
= taxable base
+ VAT (19%)
= final total
```

Rules implemented:

- VAT (IVA) 19% applied over the taxable base
- DUOC discount (10%) for `@duocuc.cl` emails
- Loyalty redemption: 100 points = 1 CLP
- Redemption cap: 30% of order subtotal
- Points emitted only after payment

All calculations are enforced in the backend to guarantee consistency.


---


# ⚙ Concurrency-Safe Stock Reservation

Stock is reserved using atomic conditional updates in MongoDB.

Properties:

- Update succeeds only if `stock ≥ requested quantity`
- Prevents overselling during concurrent checkouts
- No read-then-write race condition
- Database-level concurrency protection

If the SQL transaction fails after stock reservation, a compensation update restores the stock, ensuring consistency between MongoDB and MySQL.


---


# 🔐 Security Architecture

The backend uses **stateless JWT authentication** with Spring Security.

Key elements:

- Custom `JwtAuthenticationFilter`
- Full **SecurityFilterChain** execution
- Explicit 401 handling for unauthenticated requests
- Protected API routes
- MockMvc integration tests aligned with the Servlet stack


---


# 🧪 Testing Strategy

Integration tests are implemented using **Testcontainers**, running real MySQL and MongoDB instances during execution.

Key aspects:

- Real database environments (no mocks)
- Full JWT authentication flow tested
- Spring Security filter chain included
- CI-compatible execution

Validated flows include:

- Authentication
- Cart operations
- Checkout and payment
- Order lifecycle
- Financial rules

This ensures end-to-end correctness of business logic.


---


# 🎖 Loyalty & Level System

The loyalty system rewards users based on real spending.

Rules:

- 1 CLP spent = 1 loyalty point
- Redemption: 100 points = 1 CLP
- Redemption capped at 30% of order subtotal
- Points emitted only when order is paid

Levels are calculated using historical total earned points.

Level thresholds:

```
BRONZE   → 0
SILVER   → 250,000
GOLD     → 500,000
PLATINUM → 850,000
DIAMOND  → 1,500,000
```

Levels are recalculated dynamically and are intentionally decoupled from financial logic.


---


# 🤝 Referral System

Each user receives a unique referral code.

Referral rules:

- Referral codes can only be used during registration
- Both the referrer and the referred user receive 700,000 points
- High reward values are used intentionally to make referral effects visible during testing.

This encourages user acquisition while integrating with the loyalty system.


---


# ▶️ Run Locally (Optional)

> ⚠️ Not required for evaluation — the system is fully deployed.

### 1. Clone repository

```bash
git clone https://github.com/AmaVetro/the-duckers.git
cd the-duckers
```

### 2. Start Databases

```bash
docker compose -f infra/docker-compose.yml up -d
```

### 3. Start Backend

```bash
cd backend
./mvnw spring-boot:run
```

### 4. Start Frontend

```bash
cd frontend
npm install
npm run dev
```

Backend runs on: http://localhost:8080  
Frontend runs on: http://localhost:5173


---


# 📌 Design Decisions

This project intentionally avoids overengineering while focusing on core backend complexity.

- No refresh tokens → simplified stateless JWT model
- No admin panel → scope focused on purchase flow
- No real payment integration → controlled simulation
- Dual database design → separation of transactional and catalog data

The goal is to maximize learning value and technical clarity rather than feature quantity.


---


# 📄 License

This project was built for educational and portfolio purposes.


