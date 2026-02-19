# System Architecture: Naive Solution

This diagram visualizes the "Naive Solution" (Robust approach) currently implemented in `bloco-1`.

```mermaid
graph TD
    subgraph "External"
        K6["K6 (Load Test)"]
    end

    subgraph "Application Container (Java Spring Boot)"
        direction TB
        Tomcat["Tomcat Thread Pool <br/> (max: 200)"]
        AppLogic["Application Logic <br/> (Purchase Controller)"]
        Hikari["HikariCP Connection Pool <br/> (max: 10)"]
        
        Tomcat --> AppLogic
        AppLogic --> Hikari
    end

    subgraph "Database Container (PostgreSQL)"
        DB[("Postgres Database <br/> (ingressos)")]
    end

    subgraph "Observability Stack"
        direction LR
        Prometheus["Prometheus <br/> (Metrics Collector)"]
        Grafana["Grafana <br/> (Dashboards)"]
        Exporters["Exporters <br/> (cAdvisor, PG Exporter)"]
        
        Prometheus -- "Scrapes /actuator/prometheus" --> AppLogic
        Prometheus -- "Scrapes metrics" --> Exporters
        Grafana -- "Queries" --> Prometheus
    end

    %% Interactions
    K6 -- "HTTP POST /compras" --> Tomcat
    Hikari -- "DB Connections" --> DB
    Exporters -- "Monitor" --> DB
    Exporters -- "Monitor Docker" --> Tomcat

    %% Styles
    classDef generic fill:#f9f9f9,stroke:#333,stroke-width:1px
    classDef app fill:#e1f5fe,stroke:#01579b,stroke-width:2px
    classDef db fill:#fff3e0,stroke:#e65100,stroke-width:2px
    classDef obs fill:#f3e5f5,stroke:#4a148c,stroke-width:1px

    class K6,Tomcat,AppLogic,Hikari,DB,Prometheus,Grafana,Exporters generic
    class Tomcat,AppLogic,Hikari app
    class DB db
    class Prometheus,Grafana,Exporters obs
```

### Key Components

1.  **K6 (External)**: Generates high-concurrency traffic to simulate a real-world ticket sale event.
2.  **Tomcat (Web Server)**: Manages incoming HTTP requests. The thread pool size (default 200) determines how many requests can be *processed* simultaneously.
3.  **HikariCP (Connection Pool)**: Manages a pool of active connections to PostgreSQL. With a limit of 10, it acts as a throttle, preventing the application from overwhelming the database.
4.  **PostgreSQL (Database)**: Relies on "Skip Locked" row-level locking to ensure data consistency during concurrent purchases.
5.  **Observability Stack**:
    *   **Prometheus**: Periodically pulls metrics from the application (via Actuator) and infrastructure (via exporters).
    *   **Grafana**: Provides the visual interface to analyze the relationship between traffic, thread usage, pool saturation, and database performance.
