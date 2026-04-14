# List of queries to be used in service

**1. Get all users**
```sql
-- Run against: ums
SELECT
    BIN_TO_UUID(u.id)   AS user_id,
    u.name,
    u.email,
    u.created_at
FROM users u
ORDER BY u.created_at;
```

---

**2. Get all producers with their roles**
```sql
-- Run against: ums
SELECT
    BIN_TO_UUID(u.id)   AS user_id,
    u.name,
    u.email,
    u.created_at,
    r.name              AS role
FROM users u
JOIN users_roles ur ON u.id = ur.user_id
JOIN roles r        ON ur.role_id = r.id
WHERE r.name = 'PRODUCER'
ORDER BY u.name;
```

---

**3. Get all subscribers with their roles**
```sql
-- Run against: ums
SELECT
    BIN_TO_UUID(u.id)   AS user_id,
    u.name,
    u.email,
    u.created_at,
    r.name              AS role
FROM users u
JOIN users_roles ur ON u.id = ur.user_id
JOIN roles r        ON ur.role_id = r.id
WHERE r.name = 'SUBSCRIBER'
ORDER BY u.name;
```

---

**4. Get all subscribers subscribed to a specific producer by producer's email**

This requires two steps because the databases are not directly connected.

```sql
-- Step 1: Run against ums — get the producer's ID by email
SELECT BIN_TO_UUID(id) AS producer_id
FROM users
WHERE email = 'emmanuel@macron.fr';

-- Step 2: HTTP service passes producer_id to the twitter service.
-- Run against: twitter — get all subscriber_ids for that producer
SELECT BIN_TO_UUID(subscriber_id) AS subscriber_id
FROM subscriptions
WHERE producer_id = UUID_TO_BIN('1cd89e11-602a-4186-afbf-e0149b59eb08');

-- Step 3: HTTP service passes subscriber_ids back to ums.
-- Run against: ums — get subscriber details with roles
SELECT
    BIN_TO_UUID(u.id)   AS user_id,
    u.name,
    u.email,
    u.created_at,
    r.name              AS role
FROM users u
JOIN users_roles ur ON u.id = ur.user_id
JOIN roles r        ON ur.role_id = r.id
WHERE u.id IN (
    UUID_TO_BIN('70a64b54-43c3-4c18-bbec-64590ff7e0cc'),
    UUID_TO_BIN('0dd03a59-7dbc-4d00-8107-3271b3345434')
    -- ... list of subscriber UUIDs from Step 2
)
ORDER BY u.name;
```

---

**5. Get all messages**
```sql
-- Run against: twitter
SELECT
    BIN_TO_UUID(id)        AS message_id,
    BIN_TO_UUID(author_id) AS author_id,
    content,
    created_at
FROM messages
ORDER BY created_at DESC;
```

---

**6. Get all messages created by a specific producer by their email**

```sql
-- Step 1: Run against: ums — resolve email to user ID
SELECT BIN_TO_UUID(id) AS producer_id
FROM users
WHERE email = 'donald@trump.us';

-- Step 2: HTTP service passes producer_id to twitter.
-- Run against: twitter
SELECT
    BIN_TO_UUID(id)        AS message_id,
    BIN_TO_UUID(author_id) AS author_id,
    content,
    created_at
FROM messages
WHERE author_id = UUID_TO_BIN('6e27ea06-a716-4c89-af88-813749a8bd48')
ORDER BY created_at DESC;
```

---

**7. Get all messages for a specific subscriber by their email**

```sql
-- Step 1: Run against: ums — resolve subscriber email to ID
SELECT BIN_TO_UUID(id) AS subscriber_id
FROM users
WHERE email = 'justin@trudeau.ca';

-- Step 2: HTTP service passes subscriber_id to twitter.
-- Run against: twitter — get producer_ids the subscriber follows,
-- then join to messages in a single query
SELECT
    BIN_TO_UUID(m.id)        AS message_id,
    BIN_TO_UUID(m.author_id) AS author_id,
    m.content,
    m.created_at
FROM messages m
JOIN subscriptions s ON m.author_id = s.producer_id
WHERE s.subscriber_id = UUID_TO_BIN('70a64b54-43c3-4c18-bbec-64590ff7e0cc')
ORDER BY m.created_at DESC;

-- Step 3 (optional enrichment): HTTP service passes author_ids back to ums
-- to resolve author names if needed.
SELECT
    BIN_TO_UUID(id) AS user_id,
    name,
    email
FROM users
WHERE id IN (
    UUID_TO_BIN('1cd89e11-602a-4186-afbf-e0149b59eb08'),
    UUID_TO_BIN('6e27ea06-a716-4c89-af88-813749a8bd48')
    -- ... author UUIDs returned from Step 2
);
```
