# ⏱️ Chronologic - High-Performance Log Ingestion Engine

O **Chronologic** é um motor de ingestão e consolidação de logs distribuídos de alta performance, projetado para absorver grandes volumes de dados de forma assíncrona, mitigar picos de carga de microsserviços e persistir dados históricos de forma estruturada e colunar.

O projeto foi construído utilizando os padrões mais rígidos de **Arquitetura Hexagonal (Ports & Adapters)**, **SOLID** e conceitos avançados de Big Data, atuando como uma alternativa eficiente e de baixo custo de infraestrutura em relação a stacks tradicionais como o Elasticsearch.

---

## 🏗️ Desenho Arquitetural e Estratégia de Dados

A arquitetura do sistema é dividida em duas grandes etapas totalmente desacopladas, garantindo que o núcleo da aplicação (*Domain/Core*) permaneça isolado de frameworks e tecnologias de banco de dados.

```text
[ Microsserviços ] ➔ (HTTP POST /api/persist/logs)
                             │
                             ▼  [202 ACCEPTED] (Thread Liberada)
                   [ ChronologicController ]
                             │
                             ▼ (Micro-Batch Assíncrono - rightPush)
                   [ Redis Buffer (RAM Volátil) ] ➔ TTL 30 minutos / Particionado por Hora
                             │
                   [ Scheduler Job ] ➔ Executa a cada 15 minutos (Metade do TTL)
                             │
                             ▼ (Garante processamento apenas de chaves passadas)
                   [ MoveLogsToHistoryUseCase ]
                             │
                             ▼ (Virtualização de CSV em Memória RAM via StringWriter)
                   [ HistoryPersistAdapter ]
                             │
                             ▼ (Bulk Load Streaming via HTTP)
                   [ ClickHouse (Banco Colunar) ] ➔ Particionado por Mês
```

### 🧠 Principais Decisões de Engenharia:
1. **Domínio Puro (No-Dependencies):** O campo dinâmico `metadata` (JsonNode) é sanitizado e transformado em String na borda do sistema (`LogRequestMapper`). O core de negócio trabalha apenas com tipos primitivos do Java, blindando o domínio contra alterações em bibliotecas de terceiros (como Jackson ou Gson).
2. **Segregação de Portas (SOLID - ISP):** As interfaces de comunicação com o Redis foram segregadas por intenção de ação (`FetchBufferLogsOutputPort` para leitura e `PurgeBufferLogsOutputPort` para deleção), impedindo vazamento de escopo.
3. **Complexidade Constante $O(1)$:** A inserção de novos logs no Redis utiliza a cauda da lista (`rightPush`). Isso impede a perda de performance que ocorreria com o rearranjo de índices provocado pelo `leftPush`.
4. **Virtualização de Arquivos Planos (Anti-Overengineering):** O arquivo CSV usado para a carga em massa é gerado dinamicamente dentro da memória RAM utilizando `StringWriter` e `BufferedWriter`. Isso elimina o desperdício de desempenho com Entrada/Saída (I/O) de disco rígido na máquina do servidor.
5. **Circuito Transacional Autolimpante:** As chaves do buffer do Redis só recebem o comando de deleção (`Purge`) se, e somente se, o banco colunar retornar sucesso absoluto de gravação. Se houver falha, as chaves permanecem intactas para o próximo ciclo do Scheduler (*Retry Resiliente*).

---

## 🛠️ Tecnologias Utilizadas

* **Java 21** & **Spring Boot 4.x** (Core da aplicação)
* **Lombok** (Geração de boilerplate de código)
* **Spring Data Redis / Lettuce** (Camada de bufferização volátil rápida)
* **Spring JDBC Starter / ClickHouse Client 0.9.x** (Streaming de Big Data via HTTP)
* **Docker** (Gerenciamento dos contêineres de banco de dados)

---

## 🚀 Como Executar o Projeto Localmente (Mac/Linux/Windows)

### 📋 Pré-requisitos
* Java 21+ instalado
* Maven instalado
* Docker e Docker Desktop rodando

### 1. Inicializar os Contêineres de Banco de Dados
Abra o terminal e execute os comandos abaixo para subir o Redis e o ClickHouse com as configurações de desenvolvimento validadas:

```bash
# Inicializar o Buffer Volátil (Redis)
docker run -d --name chronologic-redis -p 6379:6379 redis:latest

# Inicializar o Armazenamento Histórico Colunar (ClickHouse)
docker run -d \
  --name chronologic-clickhouse \
  -p 8123:8123 -p 9000:9000 \
  --env CLICKHOUSE_USER=default \
  --env CLICKHOUSE_PASSWORD=vivo123 \
  --volume chronologic_clickhouse_data:/var/lib/clickhouse \
  clickhouse/clickhouse-server:latest
```

### 2. Criar a Tabela Histórica no ClickHouse
Acesse o console interativo do ClickHouse:
```bash
docker exec -it chronologic-clickhouse clickhouse-client --password vivo123
```
Cole a query abaixo para fabricar a tabela estruturada com **particionamento físico por mês**:
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
Digite `exit` para retornar ao terminal do seu computador.

### 3. Executar o Projeto Java
Na raiz do projeto `chronologic_core`, execute o Maven para baixar as dependências e iniciar o microsserviço:
```bash
mvn clean spring-boot:run
```

---

## 🧪 Validando o Fluxo (Mundo Real)

### 1. Simular o Envio de Logs (Micro-Batching)
Mande um lote de logs estruturados (exemplo real de boot do serviço *Jupiter/JpServiceDiscovery*) via terminal. Altere o timestamp para simular uma chave do passado se necessário na sua lógica do Use Case:

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
      "metadata": {"@version":"1","logger_name":"io.github.matheus_fsantos.JpApplication","thread_name":"main"}
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
O servidor responderá instantaneamente com status `202 Accepted`.

### 2. Verificar os Dados Gravados Permanentemente
Você pode auditar a tabela abrindo o **DBeaver** (conectando no host `localhost`, porta `8123`, usuário `default`, senha `vivo123`) ou direto pela linha de comando:

```bash
docker exec -it chronologic-clickhouse clickhouse-client --password vivo123 --query "SELECT * FROM app_logs"
```

### 3. Buscar Dados de Forma Inteligente
Graças ao poder colunar, você consegue caçar padrões de texto (como números de telefone, IDs de usuários ou threads) dentro do objeto do metadado de forma cirúrgica:
```sql
SELECT timestamp, app_name, message, 
       visitParamExtractString(metadata, 'thread') AS thread_execucao
FROM app_logs 
WHERE metadata LIKE '%main%';
```
