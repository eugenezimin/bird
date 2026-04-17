# UMS — User Management Service

[← Back to root README](../README.md)

The **User Management Service (UMS)** is the identity backbone of the Bird platform. It owns all user accounts, role assignments, and session records. Every other service trusts UMS as the single source of truth for authentication and authorization.

## Responsibilities

- CRUD operations on user accounts
- Role catalogue management (`ADMIN`, `PRODUCER`, `SUBSCRIBER`)
- Many-to-many user ↔ role assignment
- Session lifecycle (open / close / query)

## Functionality

`UserManagementService` or `UMS` serves User domain with all its related subdomains, including Users themselves, Roles, and Sessions.

Its functionality from the customer prospective view is possible to show on the following action diagrams below.

UMS user action flows:

- Account management — a user (or admin) registers, views their profile, updates it, or deletes the account

```mermaid
flowchart TD
    A([User wants to manage their account]) --> B{What action?}

    B --> C[Register]
    B --> D[View profile]
    B --> E[Update profile]
    B --> F[Delete account]

    C --> C1[Provide name, email, password]
    C1 --> C2{Email already taken?}
    C2 -- Yes --> C3([Show error: email in use])
    C2 -- No --> C4([Account created])

    D --> D1{User exists?}
    D1 -- No --> D2([Show error: not found])
    D1 -- Yes --> D3([Show profile + roles])

    E --> E1[Provide new name / email / password]
    E1 --> E2{User exists?}
    E2 -- No --> E3([Show error: not found])
    E2 -- Yes --> E4([Profile updated])

    F --> F1{User exists?}
    F1 -- No --> F2([Show error: not found])
    F1 -- Yes --> F3([Account deleted, sessions + roles removed])
```

- Role management — an admin assigns a role to a user

```mermaid
flowchart TD
    A([Admin wants to manage user roles]) --> B[Browse available roles]
    B --> B1([View role catalogue: ADMIN · PRODUCER · SUBSCRIBER])
    B1 --> C{Assign a role to a user?}

    C -- No --> Z([Done])
    C -- Yes --> D[Select user + select role]
    D --> E{User exists?}
    E -- No --> E1([Show error: user not found])
    E -- Yes --> F{Role already assigned?}
    F -- Yes --> F1([No change, silently ignored])
    F -- No --> G([Role assigned to user])
    G --> C
```

- Session lifecycle — a user logs in (opens a session) and logs out (closes it); can also check their last session

```mermaid
flowchart TD
    A([User wants to start using the platform]) --> B[Log in]
    B --> C{User account exists?}
    C -- No --> C1([Show error: user not found])
    C -- Yes --> D([Session opened, session ID returned])

    D --> E{What does the user do next?}

    E -- Use the platform --> E1([Perform actions under active session])
    E1 --> E

    E -- Check last activity --> F[View last session]
    F --> G{Any session on record?}
    G -- No --> G1([Show: no sessions found])
    G -- Yes --> G2([Show: login time + logout time])
    G2 --> E

    E -- Log out --> H[Close session]
    H --> I{Session found?}
    I -- No --> I1([Show error: session not found])
    I -- Yes --> J([Session closed, logged_out_at recorded])
    J --> K([User is logged out])
```

## Tech Stack

| Concern | Technology |
|---|---|
| Runtime | Java 25 (preview features enabled) |
| Framework | Spring Boot 4.0.5 — WebFlux (reactive, Netty) |
| Persistence | Spring JDBC (`JdbcTemplate`) |
| Database | MySQL 8 — `ums` schema |
| JSON | Jackson + `JavaTimeModule` (ISO-8601 dates) |
| Build | Gradle 9.4.1 |
| Boilerplate | Lombok (`@Data`, `@AllArgsConstructor`, etc.) |

## Database Schema

```mermaid
erDiagram
    roles {
        BINARY_16 id PK "UUID_TO_BIN"
        VARCHAR name "UNIQUE NOT NULL"
        VARCHAR description
    }

    users {
        BINARY_16 id PK "UUID_TO_BIN"
        VARCHAR name "NOT NULL"
        VARCHAR email "UNIQUE NOT NULL"
        VARCHAR password_hash "NOT NULL"
        DATETIME created_at "DEFAULT CURRENT_TIMESTAMP"
        DATETIME updated_at "ON UPDATE CURRENT_TIMESTAMP"
    }

    users_roles {
        BINARY_16 user_id PK,FK "FK → users.id · CASCADE DELETE"
        BINARY_16 role_id PK,FK "FK → roles.id · CASCADE DELETE"
    }

    sessions {
        BINARY_16 id PK "UUID_TO_BIN"
        BINARY_16 user_id FK "FK → users.id · CASCADE DELETE"
        DATETIME logged_in_at "DEFAULT NOW()"
        DATETIME logged_out_at "nullable"
    }

    users ||--o{ users_roles : "has roles"
    roles ||--o{ users_roles : "assigned to"
    users ||--o{ sessions : "has sessions"
```

> **Why `BINARY(16)`?** UUIDs stored as `CHAR(36)` waste space and slow index operations. `BINARY(16)` stores the raw 16 bytes — half the size — and uses `UUID_TO_BIN()` / `BIN_TO_UUID()` in every SQL statement to convert transparently.

## API Endpoints

Base URL: `http://localhost:9000`

All responses use a standard JSON envelope:
```json
{
  "code": "200",
  "message": "Human-readable status",
  "data": { ... }
}
```

### Users

| Method | Path | Description |
|---|---|---|
| `GET` | `/users` | List all users (with roles) |
| `POST` | `/users/user` | Create a new user |
| `GET` | `/users/user/{id}` | Get user by UUID |
| `PUT` | `/users/user/{id}` | Update user (name, email, password) |
| `DELETE` | `/users/user/{id}` | Delete user |
| `POST` | `/users/user/{id}/role/{roleId}` | Assign a role to a user |

### Roles

| Method | Path | Description |
|---|---|---|
| `GET` | `/roles` | List all roles |

### Sessions

| Method | Path | Description |
|---|---|---|
| `POST` | `/sessions/user/{userId}` | Open a new session → returns session UUID |
| `GET` | `/sessions/user/{userId}/last` | Get the user's most recent session |
| `PUT` | `/sessions/{sessionId}/close` | Close a session (sets `logged_out_at`) |

## Component diagram

```mermaid
flowchart TB

    Client([Client])
    Twitter([Twitter Service :9001])
    DB[(MySQL ums DB)]

    subgraph CONFIG[config]
        CorsConfig[CorsConfig]
    end

    subgraph CONTROLLERS[controllers]
        UserController[UserController]
        RolesController[RolesController]
        SessionController[SessionController]
    end

    subgraph DAO[dao]
        UmsRepository[UmsRepository interface]
        JdbcUmsRepository[JdbcUmsRepository]
        DaoHelper[DaoHelper]
    end

    subgraph DTOS[dtos]
        User[User]
        Roles[Roles]
        LastSession[LastSession]
        Constants[Constants]
    end

    Client -->|HTTP :9000| UserController
    Client -->|HTTP :9000| RolesController
    Client -->|HTTP :9000| SessionController
    Twitter -->|WebClient GET /users/user/id| UserController

    UserController --> UmsRepository
    RolesController --> UmsRepository
    SessionController --> UmsRepository

    UmsRepository -.->|implements| JdbcUmsRepository

    JdbcUmsRepository --> DaoHelper
    JdbcUmsRepository -->|JDBC| DB
    JdbcUmsRepository --> Constants

    JdbcUmsRepository --> User
    JdbcUmsRepository --> Roles
    JdbcUmsRepository --> LastSession
```

## Package Structure

```
com.ziminpro.ums/
├── UmsApplication.java
├── config/
│   └── CorsConfig.java              ← CORS headers for browser clients
├── controllers/
│   ├── UserController.java          ← /users endpoints
│   ├── RolesController.java         ← /roles endpoints
│   └── SessionController.java       ← /sessions endpoints
├── dao/
│   ├── UmsRepository.java           ← Interface
│   ├── JdbcUmsRepository.java       ← JdbcTemplate implementation
│   └── DaoHelper.java               ← byte[] ↔ UUID conversion
└── dtos/
    ├── User.java                    ← User DTO (with List<Role>)
    ├── Roles.java                   ← Role enum / DTO
    ├── LastSession.java
    └── Constants.java               ← All SQL strings centralised here
```

## Configuration

`src/main/resources/application.yaml`:

```yaml
server:
  port: 9000

spring:
  datasource:
    driverClassName: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://0.0.0.0:3306/ums?serverTimezone=UTC&useLegacyDatetimeCode=false
    username: root
    password: passw
```

## Build & Run

```bash
cd $HOME/bird/ums

# Build (runs tests, compiles, packages JAR)
gradle build

# Run
java-jar build/libs/ums-2.0.jar
```
## Notable Implementation Details

**SQL centralised in `Constants.java`** — All SQL strings live as `public static final String` constants. This keeps DAO classes clean and SQL changes easy to locate without an ORM.

**Multi-role result collapsing** — `GET /users` joins `users → users_roles → roles`, producing one row per role per user. The DAO collapses these into a single `User` object with a `List<Role>` using a custom `ResultSetExtractor`.

**`ON UPDATE CURRENT_TIMESTAMP`** — The `users.updated_at` column updates automatically on every `UPDATE` at the database level, requiring no application-side logic.

**Session history** — Rather than a simple "last login" column, a `sessions` table records every login and logout, enabling full audit capability and concurrent session detection.

[← Back to root README](../README.md) | [Twitter Service →](../twitter/README.md) | [Frontend →](../frontend/README.md)