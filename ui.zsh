#!/usr/bin/env bash
# =============================================================================
# Bird — Twitter Clone Frontend Setup Script
# Run this from the root of your project (where /ums and /twitter live).
# Prerequisites: Node.js 20+, npm 10+
# =============================================================================

set -e  # Exit immediately on any error

# ---------------------------------------------------------------------------
# 1. Scaffold with Vite (React + JS)
# ---------------------------------------------------------------------------
npm create vite@latest frontend -- --template react
cd frontend

# ---------------------------------------------------------------------------
# 2. Install dependencies
# ---------------------------------------------------------------------------
npm install

# Install routing, HTTP client, state management, and UI utilities
npm install \
  react-router-dom \
  axios \
  zustand \
  date-fns

# Install dev dependencies
npm install -D \
  eslint \
  eslint-plugin-react \
  eslint-plugin-react-hooks \
  @eslint/js \
  prettier \
  eslint-config-prettier

# ---------------------------------------------------------------------------
# 3. Remove Vite boilerplate files
# ---------------------------------------------------------------------------
rm -f src/App.css
rm -f src/assets/react.svg
rm -f public/vite.svg

# ---------------------------------------------------------------------------
# 4. Create the full directory structure
# ---------------------------------------------------------------------------
mkdir -p \
  src/api \
  src/components/layout \
  src/components/ui \
  src/components/auth \
  src/components/messages \
  src/components/subscriptions \
  src/components/users \
  src/pages \
  src/store \
  src/hooks \
  src/utils \
  src/styles

# ---------------------------------------------------------------------------
# 5. Create placeholder index files (barrel exports)
# ---------------------------------------------------------------------------

# API layer — one module per microservice
cat > src/api/umsApi.js << 'EOF'
/**
 * UMS Service API client (port 9000)
 * All requests return the standard envelope: { code, message, data }
 */
import axios from 'axios';

const umsClient = axios.create({
  baseURL: import.meta.env.VITE_UMS_BASE_URL ?? 'http://localhost:9000',
  headers: { 'Content-Type': 'application/json' },
});

// ---- Users ----
export const getAllUsers        = ()           => umsClient.get('/users');
export const getUserById        = (id)         => umsClient.get(`/users/user/${id}`);
export const createUser         = (payload)    => umsClient.post('/users/user', payload);
export const updateUser         = (id, payload)=> umsClient.put(`/users/user/${id}`, payload);
export const deleteUser         = (id)         => umsClient.delete(`/users/user/${id}`);

// ---- Roles ----
export const getAllRoles         = ()           => umsClient.get('/roles');

// ---- Sessions ----
export const openSession        = (userId)     => umsClient.post(`/sessions/user/${userId}`);
export const closeSession       = (sessionId)  => umsClient.delete(`/sessions/${sessionId}`);
export const getLastSession     = (userId)     => umsClient.get(`/sessions/user/${userId}/last`);
EOF

cat > src/api/twitterApi.js << 'EOF'
/**
 * Twitter Service API client (port 9001)
 * UMS must be running before this service works.
 */
import axios from 'axios';

const twitterClient = axios.create({
  baseURL: import.meta.env.VITE_TWITTER_BASE_URL ?? 'http://localhost:9001',
  headers: { 'Content-Type': 'application/json' },
});

// ---- Messages ----
export const getMessageById          = (id)          => twitterClient.get(`/messages/message/${id}`);
export const getMessagesByProducer   = (producerId)  => twitterClient.get(`/messages/producer/${producerId}`);
export const getMessagesBySubscriber = (subscriberId)=> twitterClient.get(`/messages/subscriber/${subscriberId}`);
export const createMessage           = (payload)     => twitterClient.post('/messages/message', payload);
export const deleteMessage           = (id)          => twitterClient.delete(`/messages/message/${id}`);

// ---- Subscriptions ----
export const getSubscription         = (subscriberId)=> twitterClient.get(`/subscriptions/subscriber/${subscriberId}`);
export const createSubscription      = (payload)     => twitterClient.post('/subscriptions', payload);
export const updateSubscription      = (payload)     => twitterClient.put('/subscriptions', payload);
export const deleteSubscription      = (subscriberId)=> twitterClient.delete(`/subscriptions/subscriber/${subscriberId}`);
EOF

cat > src/api/index.js << 'EOF'
export * as umsApi     from './umsApi';
export * as twitterApi from './twitterApi';
EOF

# ---------------------------------------------------------------------------
# Zustand stores
# ---------------------------------------------------------------------------
cat > src/store/userStore.js << 'EOF'
/**
 * Global user/auth state.
 * Tracks the currently "logged-in" user and their active session.
 */
import { create } from 'zustand';

export const useUserStore = create((set) => ({
  currentUser:   null,
  sessionId:     null,
  setCurrentUser: (user)      => set({ currentUser: user }),
  setSessionId:   (sessionId) => set({ sessionId }),
  logout:         ()          => set({ currentUser: null, sessionId: null }),
}));
EOF

cat > src/store/feedStore.js << 'EOF'
/**
 * Feed / message list state for the current subscriber's timeline.
 */
import { create } from 'zustand';

export const useFeedStore = create((set) => ({
  messages:    [],
  isLoading:   false,
  error:       null,
  setMessages: (messages) => set({ messages }),
  setLoading:  (isLoading)=> set({ isLoading }),
  setError:    (error)    => set({ error }),
}));
EOF

cat > src/store/index.js << 'EOF'
export { useUserStore } from './userStore';
export { useFeedStore } from './feedStore';
EOF

# ---------------------------------------------------------------------------
# Custom hooks
# ---------------------------------------------------------------------------
cat > src/hooks/useMessages.js << 'EOF'
/**
 * Hook: fetch messages for a producer or subscriber.
 */
import { useState, useEffect } from 'react';
import { twitterApi } from '../api';

export function useMessagesForProducer(producerId) {
  const [messages, setMessages] = useState([]);
  const [loading,  setLoading]  = useState(false);
  const [error,    setError]    = useState(null);

  useEffect(() => {
    if (!producerId) return;
    setLoading(true);
    twitterApi.getMessagesByProducer(producerId)
      .then(res  => setMessages(res.data.data ?? []))
      .catch(err => setError(err))
      .finally(  () => setLoading(false));
  }, [producerId]);

  return { messages, loading, error };
}
EOF

cat > src/hooks/useUsers.js << 'EOF'
/**
 * Hook: fetch the full user list from UMS.
 */
import { useState, useEffect } from 'react';
import { umsApi } from '../api';

export function useUsers() {
  const [users,   setUsers]   = useState([]);
  const [loading, setLoading] = useState(false);
  const [error,   setError]   = useState(null);

  useEffect(() => {
    setLoading(true);
    umsApi.getAllUsers()
      .then(res  => setUsers(res.data.data ?? []))
      .catch(err => setError(err))
      .finally(  () => setLoading(false));
  }, []);

  return { users, loading, error };
}
EOF

cat > src/hooks/index.js << 'EOF'
export { useMessagesForProducer } from './useMessages';
export { useUsers }               from './useUsers';
EOF

# ---------------------------------------------------------------------------
# Utilities
# ---------------------------------------------------------------------------
cat > src/utils/formatDate.js << 'EOF'
import { format, formatDistanceToNow } from 'date-fns';

/** "Nov 12, 2020 at 2:38 PM" */
export const formatFull = (dateStr) =>
  format(new Date(dateStr), 'MMM d, yyyy \'at\' h:mm a');

/** "3 hours ago" */
export const formatRelative = (dateStr) =>
  formatDistanceToNow(new Date(dateStr), { addSuffix: true });
EOF

cat > src/utils/extractData.js << 'EOF'
/**
 * Unwrap the standard API envelope { code, message, data }.
 * Throws if the code is not 2xx.
 */
export function extractData(response) {
  const envelope = response.data;
  if (!envelope.code?.startsWith('2')) {
    throw new Error(envelope.message ?? 'Unknown error');
  }
  return envelope.data;
}
EOF

cat > src/utils/index.js << 'EOF'
export * from './formatDate';
export * from './extractData';
EOF

# ---------------------------------------------------------------------------
# Global styles
# ---------------------------------------------------------------------------
cat > src/styles/globals.css << 'EOF'
/* Bird — Global CSS Variables & Reset */

:root {
  --color-bg:        #0f1117;
  --color-surface:   #1a1d27;
  --color-border:    #2a2d3a;
  --color-accent:    #1d9bf0;
  --color-accent-hover: #1a8cd8;
  --color-text:      #e7e9ea;
  --color-muted:     #71767b;
  --color-danger:    #f4212e;
  --color-success:   #00ba7c;

  --radius-sm:  6px;
  --radius-md:  12px;
  --radius-lg:  20px;
  --radius-full: 9999px;

  --font-body:    'DM Sans', sans-serif;
  --font-display: 'Syne', sans-serif;

  --shadow-card: 0 1px 3px rgba(0,0,0,0.4);
}

*, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

body {
  background: var(--color-bg);
  color:      var(--color-text);
  font-family: var(--font-body);
  line-height: 1.5;
  -webkit-font-smoothing: antialiased;
}

a { color: var(--color-accent); text-decoration: none; }
a:hover { text-decoration: underline; }

button { cursor: pointer; font-family: inherit; }
EOF

# ---------------------------------------------------------------------------
# Pages (empty shells)
# ---------------------------------------------------------------------------
for page in Home Feed Profile Messages Login; do
cat > "src/pages/${page}Page.jsx" << EOF
// TODO: implement ${page} page
export default function ${page}Page() {
  return <div className="page">${page}</div>;
}
EOF
done

# ---------------------------------------------------------------------------
# Layout components
# ---------------------------------------------------------------------------
cat > src/components/layout/AppLayout.jsx << 'EOF'
// Top-level layout: sidebar + main content area
import { Outlet } from 'react-router-dom';

export default function AppLayout() {
  return (
    <div style={{ display: 'flex', minHeight: '100vh' }}>
      {/* <Sidebar /> */}
      <main style={{ flex: 1 }}>
        <Outlet />
      </main>
    </div>
  );
}
EOF

cat > src/components/layout/Sidebar.jsx << 'EOF'
// TODO: navigation sidebar (Home, Feed, Profile, etc.)
export default function Sidebar() {
  return <aside />;
}
EOF

# ---------------------------------------------------------------------------
# Update main App.jsx with router setup
# ---------------------------------------------------------------------------
cat > src/App.jsx << 'EOF'
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import AppLayout    from './components/layout/AppLayout';
import HomePage     from './pages/HomePage';
import FeedPage     from './pages/FeedPage';
import ProfilePage  from './pages/ProfilePage';
import MessagesPage from './pages/MessagesPage';
import LoginPage    from './pages/LoginPage';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route element={<AppLayout />}>
          <Route index       element={<HomePage />}     />
          <Route path="feed" element={<FeedPage />}     />
          <Route path="profile/:userId" element={<ProfilePage />} />
          <Route path="messages"        element={<MessagesPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
EOF

# ---------------------------------------------------------------------------
# Update main.jsx
# ---------------------------------------------------------------------------
cat > src/main.jsx << 'EOF'
import { StrictMode } from 'react';
import { createRoot }  from 'react-dom/client';
import './styles/globals.css';
import App from './App';

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>
);
EOF

# ---------------------------------------------------------------------------
# Environment variable template
# ---------------------------------------------------------------------------
cat > .env.example << 'EOF'
VITE_UMS_BASE_URL=http://localhost:9000
VITE_TWITTER_BASE_URL=http://localhost:9001
EOF
cp .env.example .env

# ---------------------------------------------------------------------------
# Prettier config
# ---------------------------------------------------------------------------
cat > .prettierrc << 'EOF'
{
  "semi": true,
  "singleQuote": true,
  "tabWidth": 2,
  "trailingComma": "es5",
  "printWidth": 100
}
EOF

# ---------------------------------------------------------------------------
# Done
# ---------------------------------------------------------------------------
echo ""
echo "✅  Bird frontend scaffolded at ./frontend"
echo ""
echo "Next steps:"
echo "  cd frontend"
echo "  npm run dev       # Start dev server at http://localhost:5173"
echo ""
echo "Structure:"
echo "  src/"
echo "  ├── api/               # axios clients for UMS (9000) & Twitter (9001)"
echo "  ├── components/"
echo "  │   ├── layout/        # AppLayout, Sidebar"
echo "  │   ├── ui/            # Shared UI primitives (Button, Card, Avatar…)"
echo "  │   ├── auth/          # Login form components"
echo "  │   ├── messages/      # MessageCard, MessageList, ComposeBox"
echo "  │   ├── subscriptions/ # SubscriptionList, FollowButton"
echo "  │   └── users/         # UserCard, UserList"
echo "  ├── pages/             # Route-level page components"
echo "  ├── store/             # Zustand global state (user, feed)"
echo "  ├── hooks/             # Custom React hooks"
echo "  ├── utils/             # Date formatting, envelope extraction"
echo "  └── styles/            # globals.css with CSS variables"
