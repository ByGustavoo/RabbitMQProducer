<div align="center"> <br>
  <img align="center" alt="rabbitmqproducer-rabbitmq" height="150" width="150" src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/rabbitmq/rabbitmq-original.svg" />
</div>

<br>

<div align="center">
  Produtor de mensagens em Spring Boot com RabbitMQ. Quando um pedido é registrado, a aplicação dispara o evento PedidoCriado, publica esse evento como JSON em uma exchange e o RabbitMQ entrega a mensagem na fila de pedidos, consumida pelo <a href="https://github.com/ByGustavoo/RabbitMQConsumer">RabbitMQConsumer</a>, com logs em cada etapa do envio.
</div>

<br> <br>

## 🚀 Ferramentas Utilizadas

* 🐳 Docker

* 📝 Log4j2

* 🔴 Lombok

* ☕️ Java 25

* 🧪 JUnit 5

* 🗺️ MapStruct

* 🐇 RabbitMQ 4

* 🟢 Spring Boot 4.1.1

* 📨 Spring AMQP 4.1.1

* 🐘 Gradle 9.7.1 (Kotlin DSL)

* 📄 Springdoc OpenAPI (Swagger UI)

<br>

## 🔎 Como Funciona

```
POST /v1/pedidos ──► PedidoService ──► evento PedidoCriado ──► PedidoProducer ──► pedidos.exchange ──► pedidos.criados
                                                                                  (routing key pedido.criado)
```

* **Evento:** o `PedidoService` monta o `PedidoCriadoEvent` e o publica com o `ApplicationEventPublisher` do Spring, sem conhecer o RabbitMQ.

* **Produtor:** o `PedidoProducer` escuta esse evento com `@EventListener` e o envia pelo `RabbitTemplate` para a exchange `pedidos.exchange`, com a routing key `pedido.criado` e o id do pedido como id de correlação.

* **Topologia:** o `RabbitMQConfig` declara a fila durável `pedidos.criados`, a `DirectExchange` `pedidos.exchange` e o binding entre elas. O Spring cria tudo no RabbitMQ na primeira conexão e recria após uma reconexão.

* **Dead letter queue:** a fila principal é declarada com `x-dead-letter-exchange` apontando para a `DirectExchange` `pedidos.dlx`, ligada à fila `pedidos.criados.dlq`. Uma mensagem rejeitada pelo consumidor sai da fila principal e cai na DLQ, com o cabeçalho `x-death` dizendo de onde veio e por quê.

* **Formato:** as mensagens viajam como JSON (`JacksonJsonMessageConverter`), com o cabeçalho `__TypeId__` apontando para a classe do evento.

* **Confirmação:** com `publisher-confirm-type: correlated` e `publisher-returns: true`, o RabbitMQ avisa se aceitou a mensagem e devolve a que não encontrou fila. Os dois retornos viram log.

* **Resposta:** a API responde `202 Accepted`, porque o pedido não é gravado em banco. A resposta indica que o evento foi aceito para processamento.

<br>

## 📝 Logs

Configurados no `log4j2.xml`: console colorido em todos os perfis e arquivo diário em `/app/logs` no perfil `prod`.

| Momento | Nível | Mensagem |
|---|---|---|
| Pedido registrado | `INFO` | `Pedido registrado! Disparando o evento PedidoCriado...` |
| Antes do envio | `INFO` | `Enviando o pedido para a fila...` |
| Depois do envio | `INFO` | `Pedido publicado na exchange! Aguardando a confirmação do RabbitMQ...` |
| RabbitMQ aceitou | `INFO` | `Mensagem confirmada pelo RabbitMQ!` |
| RabbitMQ recusou | `ERROR` | `Mensagem recusada pelo RabbitMQ!` |
| Sem fila de destino | `WARN` | `Mensagem devolvida sem fila de destino!` |
| RabbitMQ fora do ar | `ERROR` | `Falha ao publicar no RabbitMQ em /RabbitMQProducer/v1/pedidos` (resposta 503) |

Exemplo de um envio:

```
23:03:51.527 [http-nio-9019-exec-1] INFO PedidoService - Pedido registrado! Disparando o evento PedidoCriado... - Id: a376c7f1-... - Cliente: Maria Souza - Valor: 249.90
23:03:51.527 [http-nio-9019-exec-1] INFO PedidoProducer - Enviando o pedido para a fila... - Id: a376c7f1-... - Exchange: pedidos.exchange - Routing key: pedido.criado
23:03:51.592 [http-nio-9019-exec-1] INFO PedidoProducer - Pedido publicado na exchange! Aguardando a confirmação do RabbitMQ... - Id: a376c7f1-...
23:03:51.594 [rabbitConnectionFactory2] INFO RabbitMQConfig - Mensagem confirmada pelo RabbitMQ! - Id: a376c7f1-...
```

<br>

## ⚙️ Pré-requisitos

* JDK 25 instalada

* Docker, para subir o RabbitMQ local

<br>

## 🔐 Variáveis de Ambiente

Todas são opcionais: sem elas, a aplicação usa os valores do `docker-compose-rabbitmq.yml`.

| Variável | Descrição |
|---|---|
| `RABBITMQ_HOST` | Host do RabbitMQ (padrão `localhost`) |
| `RABBITMQ_PORT` | Porta AMQP (padrão `5672`) |
| `RABBITMQ_USER` | Usuário (padrão `rabbitmq`) |
| `RABBITMQ_PASSWORD` | Senha (padrão `rabbitmq`) |

<br>

## ▶️ Como Executar

🔹 RabbitMQ

```bash
# Sobe o RabbitMQ com o painel de gerenciamento (AMQP na 5672, painel na 15672)
docker compose -f docker-compose-rabbitmq.yml up -d
```

🔹 Aplicação

```bash
# Ambiente de desenvolvimento (porta 9019)
./gradlew bootRun --args="--spring.profiles.active=dev"

# Ambiente de produção (porta 9029)
./gradlew bootRun --args="--spring.profiles.active=prod"
```

🔹 Disparar um evento

```bash
# Registra um pedido e envia o evento para a fila pedidos.criados
curl -X POST http://localhost:9019/RabbitMQProducer/v1/pedidos \
  -H "Content-Type: application/json" \
  -d '{"cliente":"Maria Souza","email":"maria.souza@email.com","valor":249.90}'
```

Se a fila `pedidos.criados` já existir no seu RabbitMQ sem a DLQ, apague-a uma vez (painel › **Queues and Streams** › `pedidos.criados` › **Delete**) antes de subir a aplicação: o RabbitMQ recusa redeclarar uma fila com argumentos diferentes (`PRECONDITION_FAILED`).

A documentação fica em `http://localhost:9019/RabbitMQProducer/swagger-ui.html`. As mensagens podem
ser vistas no painel do RabbitMQ, em `http://localhost:15672` (usuário e senha `rabbitmq`), na aba
**Queues and Streams** › `pedidos.criados` › **Get messages**.

<br>

## 🔌 API

| Método | Rota | Corpo | Respostas |
|---|---|---|---|
| `POST` | `/v1/pedidos` | `cliente`, `email`, `valor` | `202` evento enviado · `400` dados inválidos · `503` RabbitMQ indisponível |

<br>

## 🧪 Testes e Build

Os testes sobem o contexto completo e usam o RabbitMQ real, então o `docker-compose-rabbitmq.yml`
precisa estar no ar. O perfil `test` usa fila, DLQ, exchanges e routing key próprias (`pedidos.criados.test`)
para não misturar com as mensagens de desenvolvimento.

```bash
# Testes
./gradlew test

# Build sem testes
./gradlew clean build -x test
```

O `PedidoProducerTest` envia um evento, lê a mensagem de volta da fila e confere que ela chegou igual.

<br>

## 📁 Estrutura

```
src/main/java/br/com/rabbitmqproducer
├── RabbitMQProducerApplication.java   # Classe de inicialização
├── config                             # RabbitMQConfig (fila, exchange, binding, DLQ, JSON e callbacks) e FilaPedidosProperties
├── controller/pedido                  # PedidoController e PedidoDocs (Swagger)
├── exceptions                         # ErrorResponseDTO e GlobalExceptionHandler
├── model
│   ├── dto/pedido                     # SalvarPedidoDTO (entrada) e PedidoDTO (resposta)
│   ├── event                          # PedidoCriadoEvent, o corpo da mensagem
│   └── mapper                         # PedidoMapper (MapStruct)
├── producer                           # PedidoProducer, que publica o evento no RabbitMQ
└── service/pedido                     # PedidoService, que registra o pedido e dispara o evento

src/main/resources
├── application.yaml                   # Conexão, confirmações e nomes da fila por perfil (dev, prod, test)
└── log4j2.xml                         # Configuração de logging
```

<br>

## 🖥️ Desenvolvedor

### 🔵 LinkedIn: [Gustavo Correa](https://www.linkedin.com/in/gustavo-chauar-correa-946168269/)