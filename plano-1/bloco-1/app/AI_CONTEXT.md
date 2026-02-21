# AI Agent Context: Spring Boot Application (`app`)

This file provides specific context for AI agents working within the Java Spring Boot application directory (`plano-1/bloco-1/app`).

## 🎯 Architecture Rules
- **Framework**: Spring Boot 3 with Java 21.
- **Data Access Strategy**:
  - We use standard **Spring Data JPA** with Hibernate for simple CRUD operations.
  - Due to the nature of the study (high-performance tuning), **Bulk Inserts must use `@GeneratedValue(strategy = GenerationType.SEQUENCE)`** and batching configured in `application.properties`. Do NOT use `GenerationType.IDENTITY` for entities that receive high-volume inserts, as it disables Hibernate batching.
  - Extremely fast operations (like `TRUNCATE`) should use `@Query(nativeQuery = true)` with `@Modifying`.
- **Database**: PostgreSQL.
- **Observability**: Metrics must be exposed via Actuator (`/actuator/prometheus`).

## 🛠️ Testing Strategies
- We utilize load testing via `k6` rather than massive unit testing.
- The `TestAdminController.java` provides administration endpoints (`/test-admin/*`) explicitly to prepare or tear down data for these load tests.
- When generating dummy data, use simple generic loops inside Spring to minimize memory overhead before flushing to DB.

## 📝 Code Style & Refactoring
- Emphasize clear boundary separation for controllers and services.
- When suggesting optimizations, always consider Tomcat Thread Pool and HikariCP connection pool configurations.
- Use explicit logging for start/end of major operations to help during observability debugging.
