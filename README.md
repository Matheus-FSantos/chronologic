# ⏱️ Chronologic - High-Performance Log Ingestion Engine

**Chronologic** is a distributed, high-performance log ingestion and consolidation engine designed to asynchronously absorb massive volumes of data, mitigate microservice load spikes, and persist historical logs in a structured, columnar data store.

The project was built adhering to the strictest principles of **Hexagonal Architecture (Ports & Adapters)**, **SOLID**, and advanced Big Data concepts. It serves as a highly efficient, low-infrastructure-cost alternative to traditional setups like the Elasticsearch stack.

---

## 🏗️ Architectural Design and Data Strategy

The system architecture is split into two decoupled phases, ensuring that the core of the application (*Domain/Core*) remains completely isolated from third-party frameworks and database technologies.

```text
[ Microservices ] ➔ (HTTP POST /api/persist/logs)
                             │
                             ▼  [202 ACCEPTED] (Thread Released)
                   [ ChronologicController ]
                             │
                             ▼ (Asynchronous Micro-Batch - rightPush)
                   [ Redis Buffer (Volatile RAM) ] ➔ 36h TTL / Partitioned by Hour
                             │
                      [ Scheduler Job ] ➔ Runs every 15 minutes (Half of TTL)
                             │
                             ▼ (Guards and processes strictly past hour keys)
                   [ MoveLogsToHistoryUseCase ]
                             │
                             ▼ (Virtual CSV Generation in RAM via StringWriter)
                   [ HistoryPersistAdapter ]
                             │
                             ▼ (Bulk Load Streaming via HTTP)
                   [ ClickHouse (Columnar DB) ] ➔ Partitioned by Month
```

### 🧠 Key Engineering Decisions:
1. **Pure Domain (No-Dependencies):** The dynamic `metadata` field (sent as a `JsonNode`) is sanitized and transformed into a regular `String` at the system boundary (`LogRequestMapper`). The business core works exclusively with native Java primitives, shielding the domain from external library updates (such as Jackson or Gson).
2. **Interface Segregation (SOLID - ISP):** The outbound interfaces interacting with the Redis buffer were strictly segregated by behavioral intent (`FetchBufferLogsOutputPort` for reads and `PurgeBufferLogsOutputPort` for deletions), preventing scope creep and leakage.
3. **Constant Time Complexity $O(1)$:** The insertion of new logs into the Redis list appends to the tail (`rightPush`). This prevents the performance degradation that would occur with index shifting caused by `leftPush`.
4. **Virtual Flat File Generation (Anti-Overengineering):** The CSV payload used for bulk loading is dynamically generated straight into the memory RAM using `StringWriter` and `BufferedWriter`. This eliminates performance bottlenecks related to local disk I/O on the server instance.
5. **Transactional Self-Cleaning Loop:** Redis buffer keys are only purged if, and only if, the columnar database returns an absolute success status for the write operation. Upon failure, keys remain untouched for the next scheduler execution cycle (*Resilient Retry*).

---

## 🛠️ Tech Stack

* **Java 21** & **Spring Boot 4.x** (Application core)
* **Lombok** (Automatic boilerplate code generation)
* **Spring Data Redis / Lettuce** (Fast volatile buffering layer)
* **Spring JDBC Starter / ClickHouse Client 0.9.x** (Big Data HTTP streaming)
* **Docker** (Database containers management)

---

## 🚀 How to Run the Project Locally (Mac/Linux/Windows)

### 📋 Prerequisites
* Java 21+ installed
* Maven installed
* Docker and Docker Desktop running

### 1. Initialize the Database Containers
Open your terminal and run the commands below to boot up Redis and ClickHouse with verified development environment settings:

```bash
# Initialize the Volatile Buffer (Redis)
docker run -d --name chronologic-redis -p 6379:6379 redis:latest

# Initialize the Columnar Historical Storage (ClickHouse)
docker run -d \
  --name chronologic-clickhouse \
  -p 8123:8123 -p 9000:9000 \
  --env CLICKHOUSE_USER=default \
  --env CLICKHOUSE_PASSWORD=vivo123 \
  --volume chronologic_clickhouse_data:/var/lib/clickhouse \
  clickhouse/clickhouse-server:latest
```

### 2. Create the Historical Table in ClickHouse
Access the ClickHouse interactive SQL client:
```bash
docker exec -it chronologic-clickhouse clickhouse-client --password vivo123
```
Paste the query below to construct the structured table using **physical monthly partitioning**:
```sql
CREATE TABLE app_logs (
    timestamp DateTime64(3),
    app_name String,
    environment String,
    level String,
    trace_id String,
    message String,
    metadata String
) 
ENGINE = MergeTree()
PARTITION BY toYYYYMM(timestamp)
ORDER BY (timestamp, app_name);
```
Type `exit` and press Enter to return to your machine's standard shell.

### 3. Run the Java Application
From the root directory of the `chronologic_core` repository, execute Maven to download dependencies and start the microservice:
```bash
mvn clean spring-boot:run
```

---

## 🧪 Validating the Pipeline (Real-World Test)

### 1. Simulate Log Submission (Micro-Batching)
Send a batch of structured logs (this example matches real boot logs from a `jupiter/JpServiceDiscovery` microservice) via terminal. Adjust the timestamp if necessary to trigger your past-hour filter criteria in the Use Case:

```bash
curl -X POST http://localhost:8080/api/persist/logs \
  -H "Content-Type: application/json" \
  -d '[
    {
      "timestamp": "2026-05-16T14:03:48.617-03:00",
      "app_name": "jp-service-discovery",
      "environment": "LOCAL",
      "level": "INFO",
      "trace_id": "main-thread-1",
      "message": "Started JpServiceDiscoveryApplication in 1.981 seconds",
      "metadata": {"@version":"1","logger_name":"io.github.matheus_fsantos.Character","thread_name":"main"}
    },
    {
      "timestamp": "2026-05-16T14:03:48.707-03:00",
      "app_name": "jp-service-discovery",
      "environment": "LOCAL",
      "level": "INFO",
      "trace_id": "nio-exec-1",
      "message": "Initializing Spring DispatcherServlet '\''dispatcherServlet'\''",
      "metadata": {"@version":"1","logger_name":"org.apache.catalina.core.ContainerBase","thread_name":"http-nio-8761-exec-1"}
    }
  ]'
```
The server will respond immediately with a `202 Accepted` status.

### 2. Verify Data Permanently Written
You can audit the table data by opening **DBeaver** (connecting to host `localhost`, port `8123`, username `default`, password `vivo123`) or straight from the command line interface:

```bash
docker exec -it chronologic-clickhouse clickhouse-client --password vivo123 --query "SELECT * FROM app_logs"
```

### 3. Search Data Intelligently
Thanks to the columnar storage capabilities, you can hunt for specific text strings (such as phone numbers, user IDs, or execution threads) inside the metadata payload on the fly:
```sql
SELECT timestamp, app_name, message, 
       visitParamExtractString(metadata, 'thread') AS execution_thread
FROM app_logs 
WHERE metadata LIKE '%main%';
```
