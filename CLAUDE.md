# RabbitMQProducer

## O que é

Produtor de mensagens de estudo em Spring Boot: o `POST /v1/pedidos` dispara o evento
`PedidoCriadoEvent`, e o `PedidoProducer` o publica como JSON na exchange `pedidos.exchange`,
que entrega na fila `pedidos.criados`. Não tem banco. Quem consome a fila é o projeto irmão
`RabbitMQConsumer` (`../RabbitMQConsumer`): o `PedidoCriadoEvent` e os nomes em `rabbitmq.pedidos.*`
precisam ficar iguais nos dois, e a fila é declarada com os mesmos argumentos dos dois lados.

## Tipo

Backend/API de estudo, sem consumidores externos. Release: commit numerado direto na `main`,
no formato `<n> - <descrição>`.

## Stack

- Java 25, Spring Boot 4.1.1, Spring AMQP 4.1.1, Gradle 9.7.1 (Kotlin DSL)
- RabbitMQ 4 (imagem `rabbitmq:4-management`), Log4j2 no lugar do Logback, Lombok, MapStruct e Springdoc

## Estrutura

- `config/` — `RabbitMQConfig` (fila, exchange, binding, DLQ `pedidos.criados.dlq` na exchange `pedidos.dlx`, conversor JSON, callbacks de confirmação e devolução) e `FilaPedidosProperties` (`rabbitmq.pedidos.*`)
- `controller/pedido/` — `PedidoController` + `PedidoDocs`
- `service/pedido/` — `PedidoService`, que monta o evento e o publica com o `ApplicationEventPublisher`
- `producer/` — `PedidoProducer`, o `@EventListener` que envia pelo `RabbitTemplate`
- `model/` — `dto/pedido`, `event` (o corpo da mensagem) e `mapper`
- `exceptions/` — `ErrorResponseDTO` e `GlobalExceptionHandler` (`AmqpException` vira 503)

## Comandos

| Objetivo | Comando |
|---|---|
| Subir o RabbitMQ | `docker compose -f docker-compose-rabbitmq.yml up -d` |
| Rodar (dev, porta 9019) | `./gradlew bootRun --args="--spring.profiles.active=dev"` |
| Testar | `./gradlew test` (com o RabbitMQ no ar) |
| Build | `./gradlew clean build -x test` |

## Convenções

- Segue a skill `java-clean-architecture`: sem comentários, campos do menor para o maior, mensagens
  terminando em `!`, imports no padrão do IntelliJ como no OrbitAPI.
- A exceção são os rótulos `// Nome` dos grupos de dependências do `build.gradle.kts` (Spring Boot,
  RabbitMQ, MapStruct, Lombok, Logging, Swagger e Testes), cada grupo da linha mais curta para a mais longa.
- O service não conhece o RabbitMQ: ele publica um evento do Spring e o produtor decide para onde vai.
  Um evento novo segue o mesmo caminho: record em `model/event`, propriedades em `rabbitmq.<nome>`,
  fila/exchange/binding no `RabbitMQConfig` e um produtor em `producer/`.
- A rota responde `202 Accepted`, por isso o `testPost` do `AbstractControllerTest` espera 202, e não
  201 como nos outros projetos.
- Os testes seguem o modelo do PrismaAPI sem a parte de banco: `@SpringBootTest` contra o RabbitMQ
  real, sem mocks. O perfil `test` usa nomes próprios (`pedidos.criados.test`).

## Pegadinhas

- Mudar os argumentos da fila (como os de dead letter) exige apagar a fila existente no RabbitMQ
  uma vez: redeclarar com argumentos diferentes dá `PRECONDITION_FAILED` e a declaração falha.
- Sem o RabbitMQ no ar a aplicação sobe normalmente (a conexão é preguiçosa), mas o `POST` responde 503
  e os testes falham.
- A confirmação do RabbitMQ chega de forma assíncrona, numa thread `rabbitConnectionFactory*`: uma
  recusa (`nack`) aparece só no log, nunca na resposta HTTP.
- O aviso `Error opening zip file ... byte-buddy-agent` durante o `./gradlew test` vem do caminho do
  usuário com acento (`Usuário`) e não afeta os testes.