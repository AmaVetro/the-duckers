# The Duckers 🦆

The Duckers is a fullstack web e-commerce project focused on gamer products.
This repository is designed as a portfolio-ready project, emphasizing reproducible infrastructure, clean architecture, and professional development practices.

---

## 🚀 Tech Stack

Frontend
- React + Vite

Backend
- Java + Spring Boot

Databases
- MySQL (transactional data)
- MongoDB (product catalog)

Infrastructure
- Docker
- Docker Compose

---

## 📦 Project Structure

the-duckers/
├─ infra/        # Docker infrastructure (MySQL, MongoDB)
├─ backend/      # Spring Boot backend (API)
├─ frontend/     # React frontend
└─ README.md

---

## ✅ Prerequisites

To run this project locally, you need:
- Docker Desktop
- Git
- A terminal (PowerShell, Bash, etc.)

No local installation of MySQL, MongoDB, or XAMPP is required.

---

## ▶️ How to Run (Local Development)

1. Clone the repository

git clone <REPOSITORY_URL>
cd the-duckers

---

2. Create environment variables file

Copy the example environment file:

cp .env.example .env

The .env file is ignored by Git and is used only for local development.

---

3. Start infrastructure with Docker Compose

From the project root:

docker compose -f infra/docker-compose.yml up -d

This will start:
- MySQL (port 3306)
- MongoDB (port 27017)

---

4. Verify containers are running

docker compose -f infra/docker-compose.yml ps

You should see both services with status Up (healthy).

---

## 🛠 Database Access (Optional / Debug Only)

These databases are exposed locally for inspection and debugging only.

MySQL
- Host: localhost
- Port: 3306
- Database: the_duckers

MongoDB
- Host: localhost
- Port: 27017
- Database: the_duckers_catalog

Databases schema and data should never be created manually.
All structure and data are managed via Docker and versioned scripts.

---

## 🧹 Resetting the Environment

To completely reset the environment (remove all data):

docker compose -f infra/docker-compose.yml down --volumes
docker compose -f infra/docker-compose.yml up -d

---

## 📌 Project Status

- Infrastructure setup: Completed
- Backend development: In progress
- Frontend development: In progress

---

## 📄 License

This project is for educational and portfolio purposes.
