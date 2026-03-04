#backend/Dockerfile:


# ============================================================
# STAGE 1 — BUILD
# ============================================================

FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy pom.xml first (layer caching optimization)
COPY pom.xml .

# Download dependencies
RUN mvn -B -q -e -DskipTests dependency:go-offline

# Copy source code
COPY src ./src

# Build application
RUN mvn clean package -DskipTests


# ============================================================
# STAGE 2 — RUNTIME
# ============================================================

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy built jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose application port
EXPOSE 8080

# Use non-root user (optional but professional)
RUN useradd -ms /bin/bash appuser
USER appuser

# Run application
ENTRYPOINT ["java","-jar","app.jar"]