# 🐦 Twitter Service — Messaging & Subscriptions

[← Back to root README](../README.md)

The **Twitter Service** handles everything related to messages (tweets) and producer–subscriber relationships. It is deliberately stateless with respect to user identity: it stores `author_id` / `subscriber_id` / `producer_id` as UUIDs but delegates all user validation to UMS via an internal HTTP call before every write operation.

---

## Responsibilities

- Create, retrieve, and delete messages (up to 280 characters)
- Manage subscriptions (follow / unfollow producers)
- Fetch a subscriber's personalized feed (messages from followed producers)
- Validate user roles by calling UMS before any write

---

## Tech Stack

| Concern | Technology |
|---|---|
| Runtime | Java 25 (preview features enabled) |
| Framework | Spring Boot 4.0.5 — WebFlux (reactive, Netty) |
| Persistence | Spring JDBC (`JdbcTemplate`) |
| Inter-service HTTP | Spring `WebClient` (non-blocking) |
| Database | MySQL 8 — `twitter` schema |
| JSON | Jackson + `JavaTimeModule` + `jackson-datatype-jsr310` |
| Build | Gradle 9.4.1 |
| Boilerplate | Lombok |

---

## Database Schema

```
erDiagram
    messages {
        BINARY_16 id PK "UUID_TO_BIN · idx PK"
        BINARY_16 author_id FK "logical FK → ums.users.id · idx_messages_author"
        VARCHAR_280 content "NOT NULL"
        DATETIME created_at "DEFAULT NOW()"
    }

    subscriptions {
        BINARY_16 subscriber_id PK,FK "logical FK → ums.users.id · composite PK"
        BINARY_16 producer_id PK,FK "logical FK → ums.users.id · idx_subs_producer"
        DATETIME created_at "DEFAULT NOW()"
    }
```

> **No mirror tables** — Earlier versions had standalone `producers` and `subscribers` tables duplicating user data. These were removed. User identity now lives exclusively in the `ums` database and is accessed via the `UMSConnector`.

> **Cross-database FKs** — MySQL does not support foreign keys across databases. Referential integrity between `twitter.messages.author_id` and `ums.users.id` is enforced in the application layer: every write first calls UMS to confirm the user exists and holds the correct role.

---

## Service-to-Service Communication

```
sequenceDiagram
    participant Client
    participant Twitter as Twitter Service
    participant UMS as UMS Service
    participant DB as UMS Database

    Client->>Twitter: POST /messages/message<br/>{ "author": "<uuid>", ... }

    activate Twitter
    Twitter->>UMS: WebClient GET /users/user/{id}

    activate UMS
    UMS->>DB: SELECT user + roles
    DB-->>UMS: user row + roles[]
    deactivate UMS

    UMS-->>Twitter: UserDto { roles: [...] }
    deactivate Twitter

    activate Twitter
    note over Twitter: Check: roles contains PRODUCER?
    alt ✓ authorized
        Twitter->>DB: INSERT INTO messages
    else ✗ unauthorized
        Twitter-->>Client: 403 Forbidden
    end
    deactivate Twitter
```

The `UMSConnector` bean holds a pre-configured `WebClient` pointing at `http://localhost:9000`. All role checks return `Mono<>` chains — no threads are blocked waiting for UMS to respond.

---

## API Endpoints

Base URL: `http://localhost:9001`

All responses use the same standard envelope as UMS:
```json
{ "code": "200", "message": "...", "data": { ... } }
```

### Messages

| Method | Path | Auth check | Description |
|---|---|---|---|
| `GET` | `/messages` | — | All messages |
| `GET` | `/messages/message/{id}` | — | Single message by UUID |
| `GET` | `/messages/producer/{producerId}` | — | All messages by a producer |
| `GET` | `/messages/subscriber/{subscriberId}` | — | Feed for a subscriber (followed producers only) |
| `POST` | `/messages/message` | must be `PRODUCER` | Create a new message |
| `DELETE` | `/messages/message/{id}` | — | Delete a message |

### Subscriptions

| Method | Path | Auth check | Description |
|---|---|---|---|
| `GET` | `/subscriptions` | — | All subscriptions |
| `GET` | `/subscriptions/subscriber/{id}` | — | Subscriptions for a subscriber |
| `POST` | `/subscriptions/subscriber/{subId}/producer/{prodId}` | `subId` must be `SUBSCRIBER`, `prodId` must be `PRODUCER` | Follow a producer |
| `DELETE` | `/subscriptions/subscriber/{id}` | — | Unfollow all (delete all subscriptions for subscriber) |

---

## Feed Query

The subscriber feed is a single SQL join — no caching layer needed at this scale:

```sql
SELECT m.id, m.author_id, m.content, m.created_at
FROM messages m
JOIN subscriptions s ON m.author_id = s.producer_id
WHERE s.subscriber_id = UUID_TO_BIN(?)
ORDER BY m.created_at DESC
```

---

## Package Structure

```
com.ziminpro.twitter/
├── TwitterCloneApp.java
├── config/
│   ├── CorsConfig.java             ← CORS headers for browser clients
│   ├── JacksonConfig.java          ← ObjectMapper with JavaTimeModule
│   └── GlobalExceptionHandler.java ← Centralised error responses
├── controllers/
│   ├── MessageController.java
│   └── SubscriptionController.java
├── dao/
│   ├── MessageRepository.java
│   ├── JdbcMessageRepository.java
│   ├── SubscriptionRepository.java
│   ├── JdbcSubscriptionRepository.java
│   └── DaoHelper.java              ← byte[] ↔ UUID conversion
├── services/
│   ├── MessagesService.java        ← Role check → INSERT via repo
│   ├── SubscriptionsService.java   ← Role check → INSERT via repo
│   └── UMSConnector.java           ← WebClient calls to UMS
└── dtos/
    ├── Message.java
    ├── Subscription.java
    ├── User.java                   ← Mirrors UMS User for deserialization
    ├── Roles.java                  ← Role enum (ADMIN, PRODUCER, SUBSCRIBER)
    ├── LastSession.java
    ├── HttpResponseExtractor.java  ← Unwraps the UMS response envelope
    └── Constants.java              ← All SQL strings + URI paths
```

---

## Configuration

`src/main/resources/application.yaml`:

```yaml
server:
  port: 9001

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://127.0.0.1:3306/twitter?serverTimezone=UTC
    username: root
    password: passw

ums:
  host: http://localhost
  port: 9000
  paths:
    user: /users/user
```

---

## Build & Run

> UMS **must be running** before starting the Twitter service. On startup, the Twitter service will attempt to reach UMS to validate connections.

```bash
cd $HOME/bird/twitter

gradle build

java -jar build/libs/twitter-2.0.jar
```

---

## Notable Implementation Details

**`INSERT IGNORE` for subscriptions** — Re-subscribing to an already-followed producer is a no-op at the database level rather than an error. This keeps the service idempotent and client-friendly.

**Reactive chain pattern** — Every write method in `MessagesService` and `SubscriptionsService` returns a `Mono<ResponseEntity<Map<String, Object>>>`. The UMS call and the database write are chained with `.flatMap()`, keeping the Netty event loop unblocked throughout.

**`HttpResponseExtractor`** — A utility class that unwraps the UMS response envelope (`{ code, message, data }`) and deserializes the `data` field into the target DTO (e.g., `User.class`). This is shared across all UMS-calling code.

**`JacksonConfig`** — A `@Primary` `ObjectMapper` bean registers `JavaTimeModule` and disables `WRITE_DATES_AS_TIMESTAMPS`. Without this, `LocalDateTime` fields serialize as numeric arrays like `[2024, 11, 12, 14, 38, 29]` instead of `"2024-11-12T14:38:29"`.

---

[← Back to root README](../README.md) | [UMS Service →](../ums/README.md) | [Frontend →](../frontend/README.md)