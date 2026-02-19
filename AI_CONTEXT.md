# AI Agent Context: System Design Study

This file provides essential context for AI agents assisting with this project.

## 🎯 Mission
The goal is to study System Design by evolving a ticket sales system from a **Naive Solution** to a **Robust Solution**.
- **Focus**: Scalability, Consistency (Skip Locked), Observability, and Performance Tuning.
- **Language**: Java 21 (Spring Boot 3).
- **Infrastructure**: Docker Compose.

## 🗺️ Project Structure

The project is organized into "Blocos" (Blocks) defined in **`plano-1.md`**.

### 📂 `plano-1/`
- **`plano-1.md`**: The Master Plan. Contains the definition of all blocks/phases.
- **`shared/`**: Reusable modules (e.g., `observability` with Grafana/Prometheus configs).

### 🏗️ `plano-1/bloco-1/` (Naive Implementation)
- **`app/`**: Java Spring Boot Application.
- **`infra-ingenua/`**: Docker Compose & Makefiles for the naive infrastructure.
- **`tests/`**: Load tests (K6 scripts).

## 🛠️ Key Commands

| Action | Command | Working Directory |
| :--- | :--- | :--- |
| **Start Environment** | `make up` | `plano-1/bloco-1/infra-ingenua` |
| **Stop Environment** | `make down` | `plano-1/bloco-1/infra-ingenua` |
| **Run Load Test** | `k6 run plano-1/bloco-1/tests/load-test.js` | `Root` or `tests/` |
| **Check Metrics** | `http://localhost:3000` (Grafana) | Browser |

## 🤖 Guidelines for AI Agents

1.  **Architecture Awareness**: Always check `architecture.md` (in `infra*/`) to understand the current design before suggesting changes.
2.  **Observability First**: Any new feature or change must be verifiable via Grafana/Prometheus. New endpoints need metrics.
3.  **Performance Mindset**: We are simulating high concurrency. Always consider connection pools (Hikari) and thread pools (Tomcat).
4.  **Language**: Communicate in **Portuguese** for explanations, but keep code comments in English/Portuguese mixed as appropriate for the codebase.
