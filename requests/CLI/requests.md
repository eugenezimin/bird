# Set of `curl` commands for both services.

## UMS Service (port 9000)

**Get all users**
```bash
curl -s http://localhost:9000/users | jq
```

**Get user by ID**
```bash
curl -s http://localhost:9000/users/user/6e27ea06-a716-4c89-af88-813749a8bd48 | jq
```

**Create a new user**
```bash
curl -s -X POST http://localhost:9000/users/user \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Eugene Zimin",
    "email": "eugene@zimin.world",
    "passwordHash": "password",
    "roles": [
      { "role": "SUBSCRIBER", "description": "Message content consumer" },
      { "role": "PRODUCER",   "description": "Message content producer" }
    ]
  }' | jq
```

**Update a user** *(full replacement — replaces all fields including roles)*
```bash
curl -s -X PUT http://localhost:9000/users/user/6e27ea06-a716-4c89-af88-813749a8bd48 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Donald J. Trump",
    "email": "donald@trump.us",
    "passwordHash": "newpassword",
    "roles": [
      { "role": "SUBSCRIBER" },
      { "role": "PRODUCER" }
    ]
  }' | jq
```

**Patch a user** *(partial update — only the fields you include are changed; roles are untouched)*
```bash
curl -s -X PATCH http://localhost:9000/users/user/6e27ea06-a716-4c89-af88-813749a8bd48 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Donald J. Trump"
  }' | jq
```

**Delete a user**
```bash
curl -s -X DELETE http://localhost:9000/users/user/6e27ea06-a716-4c89-af88-813749a8bd48 | jq
```

---

## UMS Roles (port 9000)

**Get all roles**
```bash
curl -s http://localhost:9000/roles | jq
```

**Assign a role to a user** *(idempotent — safe to call even if already assigned)*
```bash
curl -s -X POST \
  http://localhost:9000/users/user/6e27ea06-a716-4c89-af88-813749a8bd48/role/b479b357-7e25-47fa-8dba-bfdaeecc6c2c \
  | jq
```

---

## UMS Sessions (port 9000)

**Open a session**
```bash
curl -s -X POST http://localhost:9000/sessions/user/6e27ea06-a716-4c89-af88-813749a8bd48 | jq
```

**Close a session** *(replace with a real session ID returned from open)*
```bash
curl -s -X DELETE http://localhost:9000/sessions/56de00c1-a7dd-4f66-9728-307c5230455e | jq
```

**Get last session for a user**
```bash
curl -s http://localhost:9000/sessions/user/6e27ea06-a716-4c89-af88-813749a8bd48/last | jq
```

---

## Twitter Service (port 9001)

**Get all messages for a producer**
```bash
curl -s http://localhost:9001/messages/producer/6e27ea06-a716-4c89-af88-813749a8bd48 | jq
```

**Get all messages for a subscriber**
```bash
curl -s http://localhost:9001/messages/subscriber/70a64b54-43c3-4c18-bbec-64590ff7e0cc | jq
```

**Get a single message by ID**
```bash
curl -s http://localhost:9001/messages/message/e5c6f13a-76d6-48b6-81f0-20b74fa9f04c | jq
```

**Create a new message** *(author must have PRODUCER role in UMS)*
```bash
curl -s -X POST http://localhost:9001/messages/message \
  -H "Content-Type: application/json" \
  -d '{
    "author": "1cd89e11-602a-4186-afbf-e0149b59eb08",
    "content": "Mr. Macron would like to say hello!"
  }' | jq
```

**Delete a message**
```bash
curl -s -X DELETE http://localhost:9001/messages/message/e5c6f13a-76d6-48b6-81f0-20b74fa9f04c | jq
```

---

## Twitter Subscriptions (port 9001)

**Get subscription for a subscriber**
```bash
curl -s http://localhost:9001/subscriptions/subscriber/70a64b54-43c3-4c18-bbec-64590ff7e0cc | jq
```

**Create a subscription** *(subscriber must have SUBSCRIBER role in UMS)*
```bash
curl -s -X POST http://localhost:9001/subscriptions \
  -H "Content-Type: application/json" \
  -d '{
    "subscriber": "70a64b54-43c3-4c18-bbec-64590ff7e0cc",
    "producers": [
      "1cd89e11-602a-4186-afbf-e0149b59eb08",
      "6e27ea06-a716-4c89-af88-813749a8bd48"
    ]
  }' | jq
```

**Update a subscription** *(replaces the producer list entirely)*
```bash
curl -s -X PUT http://localhost:9001/subscriptions \
  -H "Content-Type: application/json" \
  -d '{
    "subscriber": "70a64b54-43c3-4c18-bbec-64590ff7e0cc",
    "producers": [
      "1cd89e11-602a-4186-afbf-e0149b59eb08"
    ]
  }' | jq
```

**Delete all subscriptions for a subscriber**
```bash
curl -s -X DELETE http://localhost:9001/subscriptions/subscriber/70a64b54-43c3-4c18-bbec-64590ff7e0cc | jq
```

---

`jq` is optional but makes the JSON responses much easier to read — drop it if you don't have it installed.