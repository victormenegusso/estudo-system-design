# Plano de Estudo: "System Design para Engenheiros de Software"

Abaixo está um plano de estudo focado em **System Design** para engenheiros de software que já têm uma boa base em desenvolvimento, mas querem se aprofundar em como projetar sistemas escaláveis, resilientes e eficientes. O plano é dividido em blocos temáticos, cada um com uma combinação de teoria e prática, culminando em uma PoC (Proof of Concept) para solidificar o aprendizado.

### Bloco 1: Comunicação, L4/L7 e Resiliência (Semanas 1-4)

*Onde o sistema começa a falhar sob carga ou instabilidade de rede.*

* **Teoria/Design:** Diferença prática entre Load Balancing L4 (TCP) e L7 (HTTP/gRPC). Quando o gRPC é overkill? Entender o *Head-of-line blocking* no HTTP/2.
* **Prática (PoC):** Configurar um Nginx ou HAProxy como L4 e depois como L7. Implementar um **Circuit Breaker** manual em Node/Python e um **Rate Limiter (Token Bucket)** usando Redis e scripts Lua para garantir atomicidade.
* **Ferramenta:** Docker Compose + K6 para simular falhas e gargalos.

### Bloco 2: Consistência de Dados e Padrões Transacionais (Semanas 5-8)

*Como manter a verdade em um sistema onde as partes não se falam o tempo todo.*

* **Teoria/Design:** Padrão Saga (Orquestrada vs Coreografada). Quando usar o **Transactional Outbox** para evitar que o banco e a fila fiquem inconsistentes. Event Sourcing: quando o estado atual não é o mais importante, mas sim o histórico.
* **Prática (PoC):** Criar um mini-fluxo de "Pedido" com 2 microserviços. Usar o padrão **Outbox** com um "Relay" simples lendo do Postgres e enviando para o PubSub (ou RabbitMQ). Implementar uma compensação de erro (Saga).

### Bloco 3: Cache Avançado e Estratégias de Escrita (Semanas 9-12)

*Sair do "Redis na frente" para o "Sistema de Cache de alta performance".*

* **Teoria/Design:** Estratégias de Invalidação (o problema mais difícil da computação). Write-through vs Write-behind. Como evitar **Cache Stampede** (várias requisições tentando regenerar o cache ao mesmo tempo).
* **Prática (PoC):** Implementar um cache com "Probabilistic Early Recomputation" ou simplesmente um lock distribuído para evitar que 1000 requests batam no banco quando o cache expira.

### Bloco 4: Escalabilidade de Banco de Dados (Semanas 13-16)

*O que fazer quando o Postgres "grita" de cansaço.*

* **Teoria/Design:** Sharding (Horizontal Partitioning) manual vs automático. Replicação Síncrona vs Assíncrona e o impacto no Teorema CAP. Conflitos de escrita em sistemas multi-region (LWW - Last Write Wins vs Vector Clocks).
* **Prática (PoC):** Usar Docker para subir um cluster de banco com leitura/escrita separadas. Simular um atraso na replicação e ver o "efeito de leitura inconsistente" no seu código.

### Bloco 5: Observabilidade e Profiling Distribuído (Semanas 17-20)

*Entender o que acontece no "meio" das chamadas.*

* **Teoria/Design:** OpenTelemetry. Como propagar o Context ID entre serviços. Identificando o "pior gargalo" em uma árvore de chamadas complexa.
* **Prática (PoC):** Instrumentar sua PoC do Bloco 2 com **Jaeger**. Gerar um relatório de performance usando K6 e identificar visualmente qual microserviço está aumentando o p99 da aplicação.
