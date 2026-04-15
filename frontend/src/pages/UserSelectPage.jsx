import { useNavigate } from 'react-router-dom';
import { useUsers } from '../hooks';
import { useUserStore } from '../store';
import { umsApi } from '../api';
import { safeExtract } from '../utils';
import { Avatar, RoleBadge, Spinner } from '../components/ui';

export default function UserSelectPage() {
  const navigate    = useNavigate();
  const { users, loading, error } = useUsers();
  const setCurrentUser = useUserStore(s => s.setCurrentUser);
  const setSessionId   = useUserStore(s => s.setSessionId);

  async function handleSelect(user) {
    setCurrentUser(user);
    // open a session and store the sessionId
    try {
      const res = await umsApi.openSession(user.id);
      const sid = safeExtract(res, null);
      if (sid) setSessionId(sid);
    } catch { /* session open is best-effort */ }
    navigate('/feed');
  }

  const producers   = users.filter(u => u.roles?.some(r => r.role === 'PRODUCER'));
  const subscribers = users.filter(u => !u.roles?.some(r => r.role === 'PRODUCER'));

  return (
    <div style={page}>
      {/* Hero */}
      <div style={hero}>
        <svg width="36" height="36" viewBox="0 0 24 24" fill="var(--accent)">
          <path d="M23 3a10.9 10.9 0 01-3.14 1.53A4.48 4.48 0 0022.43.36a9 9 0 01-2.88 1.1A4.52 4.52 0 0016.11 0c-2.5 0-4.52 2.02-4.52 4.52 0 .35.04.7.11 1.03C7.69 5.37 4.07 3.58 1.64.9a4.52 4.52 0 00-.61 2.27c0 1.57.8 2.95 2.01 3.76a4.5 4.5 0 01-2.05-.56v.06c0 2.19 1.56 4.02 3.63 4.43a4.6 4.6 0 01-2.04.08 4.52 4.52 0 004.22 3.14A9.06 9.06 0 010 15.54 12.77 12.77 0 006.92 17.5c8.3 0 12.84-6.88 12.84-12.85 0-.2 0-.39-.01-.58A9.17 9.17 0 0023 3z"/>
        </svg>
        <h1 style={heroTitle}>Welcome to Bird</h1>
        <p style={heroSub}>Choose your account to continue</p>
      </div>

      {loading && <Spinner />}
      {error   && <p style={{ color: 'var(--danger)', textAlign: 'center', padding: 24 }}>{error}</p>}

      {!loading && !error && (
        <>
          {producers.length > 0 && (
            <section style={section}>
              <h2 style={sectionLabel}>Producers &amp; Subscribers</h2>
              <div style={grid}>
                {producers.map(u => (
                  <UserCard key={u.id} user={u} onSelect={handleSelect} />
                ))}
              </div>
            </section>
          )}
          {subscribers.length > 0 && (
            <section style={section}>
              <h2 style={sectionLabel}>Subscribers</h2>
              <div style={grid}>
                {subscribers.map(u => (
                  <UserCard key={u.id} user={u} onSelect={handleSelect} />
                ))}
              </div>
            </section>
          )}
        </>
      )}
    </div>
  );
}

function UserCard({ user, onSelect }) {
  const hasProducer   = user.roles?.some(r => r.role === 'PRODUCER');
  const hasSubscriber = user.roles?.some(r => r.role === 'SUBSCRIBER');
  const hasAdmin      = user.roles?.some(r => r.role === 'ADMIN');

  return (
    <button onClick={() => onSelect(user)} style={card}>
      <Avatar name={user.name} size={52} />
      <div style={{ flex: 1, minWidth: 0, textAlign: 'left' }}>
        <p style={cardName}>{user.name}</p>
        <p style={cardEmail}>{user.email}</p>
        <div style={{ display: 'flex', gap: 4, flexWrap: 'wrap', marginTop: 6 }}>
          {hasAdmin      && <RoleBadge role="ADMIN" />}
          {hasProducer   && <RoleBadge role="PRODUCER" />}
          {hasSubscriber && <RoleBadge role="SUBSCRIBER" />}
        </div>
      </div>
      <svg width="16" height="16" viewBox="0 0 24 24" fill="var(--text-muted)">
        <path d="M9 18l6-6-6-6"/>
      </svg>
    </button>
  );
}

// ─── Styles ───────────────────────────────────────────────────────────────

const page = {
  minHeight: '100vh',
  background: 'var(--bg-primary)',
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
  padding: '0 16px 64px',
};

const hero = {
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
  gap: 8,
  padding: '56px 0 40px',
};

const heroTitle = {
  fontFamily: 'var(--font-display)',
  fontSize: 32,
  fontWeight: 800,
  color: 'var(--text-primary)',
  margin: 0,
};

const heroSub = {
  fontSize: 16,
  color: 'var(--text-secondary)',
  margin: 0,
};

const section = {
  width: '100%',
  maxWidth: 600,
  marginBottom: 32,
};

const sectionLabel = {
  fontSize: 13,
  fontWeight: 600,
  color: 'var(--text-secondary)',
  textTransform: 'uppercase',
  letterSpacing: '0.08em',
  marginBottom: 12,
};

const grid = {
  display: 'flex',
  flexDirection: 'column',
  gap: 1,
  borderRadius: 'var(--radius-lg)',
  overflow: 'hidden',
  border: '1px solid var(--border)',
};

const card = {
  display: 'flex',
  alignItems: 'center',
  gap: 14,
  padding: '16px 20px',
  background: 'var(--bg-secondary)',
  border: 'none',
  cursor: 'pointer',
  transition: 'background 0.15s',
  width: '100%',
  borderBottom: '1px solid var(--border)',
  textDecoration: 'none',
};

const cardName = {
  fontSize: 15,
  fontWeight: 600,
  color: 'var(--text-primary)',
  margin: 0,
  overflow: 'hidden',
  textOverflow: 'ellipsis',
  whiteSpace: 'nowrap',
};

const cardEmail = {
  fontSize: 13,
  color: 'var(--text-secondary)',
  margin: 0,
  overflow: 'hidden',
  textOverflow: 'ellipsis',
  whiteSpace: 'nowrap',
};
