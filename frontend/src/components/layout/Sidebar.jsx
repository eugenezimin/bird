import { NavLink, useNavigate } from 'react-router-dom';
import { useUserStore } from '../../store';
import { umsApi } from '../../api';
import { Avatar } from '../ui';

const NAV = [
  { to: '/feed',    label: 'My Feed',         icon: HomeIcon },
  { to: '/explore', label: 'Explore',      icon: ExploreIcon },
  // { to: '/profile', label: 'Profile',      icon: ProfileIcon },
];

export default function Sidebar() {
  const navigate       = useNavigate();
  const currentUser    = useUserStore(s => s.currentUser);
  const sessionId      = useUserStore(s => s.sessionId);
  const logout         = useUserStore(s => s.logout);

  async function handleLogout() {
    if (sessionId) {
      try { await umsApi.closeSession(sessionId); } catch { /* best-effort */ }
    }
    logout();
    navigate('/');
  }

  const isProducer = currentUser?.roles?.some(r => r.role === 'PRODUCER');

  return (
    <nav style={nav}>
      {/* Logo */}
      <div style={logoWrap}>
        <svg width="28" height="28" viewBox="0 0 24 24" fill="var(--accent)">
          <path d="M23 3a10.9 10.9 0 01-3.14 1.53A4.48 4.48 0 0022.43.36a9 9 0 01-2.88 1.1A4.52 4.52 0 0016.11 0c-2.5 0-4.52 2.02-4.52 4.52 0 .35.04.7.11 1.03C7.69 5.37 4.07 3.58 1.64.9a4.52 4.52 0 00-.61 2.27c0 1.57.8 2.95 2.01 3.76a4.5 4.5 0 01-2.05-.56v.06c0 2.19 1.56 4.02 3.63 4.43a4.6 4.6 0 01-2.04.08 4.52 4.52 0 004.22 3.14A9.06 9.06 0 010 15.54 12.77 12.77 0 006.92 17.5c8.3 0 12.84-6.88 12.84-12.85 0-.2 0-.39-.01-.58A9.17 9.17 0 0023 3z"/>
        </svg>
      </div>

      {/* Nav links */}
      <div style={links}>
        {NAV.map(({ to, label, icon: Icon }) => (
          <NavLink key={to} to={to} style={({ isActive }) => navItem(isActive)}>
            <Icon />
            <span style={navLabel}>{label}</span>
          </NavLink>
        ))}
        <NavLink to={`/profile/${currentUser?.id}`} style={({ isActive }) => navItem(isActive)}>
          <ProfileIcon />
          <span style={navLabel}>Profile</span>
        </NavLink>
        {isProducer && (
          <NavLink to="/compose" style={({ isActive }) => navItem(isActive)}>
            <ComposeIcon />
            <span style={navLabel}>Post</span>
          </NavLink>
        )}
      </div>

      {/* User panel at bottom */}
      {currentUser && (
        <div style={userPanel}>
          <Avatar name={currentUser.name} size={38} />
          <div style={{ flex: 1, minWidth: 0 }}>
            <p style={userName}>{currentUser.name}</p>
            <p style={userEmail}>{currentUser.email}</p>
          </div>
          <button onClick={handleLogout} style={logoutBtn} title="Switch account">
            <LogoutIcon />
          </button>
        </div>
      )}
    </nav>
  );
}

// ─── Styles ───────────────────────────────────────────────────────────────

const nav = {
  width: 'var(--sidebar-w)',
  flexShrink: 0,
  borderRight: '1px solid var(--border)',
  display: 'flex',
  flexDirection: 'column',
  padding: '8px 12px',
  position: 'sticky',
  top: 0,
  height: '100vh',
};

const logoWrap = {
  padding: '8px 12px 16px',
};

const links = {
  display: 'flex',
  flexDirection: 'column',
  gap: 4,
  flex: 1,
};

const navItem = (isActive) => ({
  display: 'flex',
  alignItems: 'center',
  gap: 16,
  padding: '10px 12px',
  borderRadius: 'var(--radius-full)',
  color: isActive ? 'var(--text-primary)' : 'var(--text-primary)',
  fontWeight: isActive ? 700 : 400,
  fontSize: 18,
  textDecoration: 'none',
  transition: 'background 0.15s',
  background: isActive ? 'var(--accent-dim)' : 'transparent',
});

const navLabel = {
  fontSize: 18,
  fontWeight: 'inherit',
};

const userPanel = {
  display: 'flex',
  alignItems: 'center',
  gap: 10,
  padding: '10px 12px',
  borderRadius: 'var(--radius-full)',
  marginTop: 8,
  cursor: 'default',
  background: 'var(--bg-secondary)',
  border: '1px solid var(--border)',
};

const userName = {
  fontSize: 14,
  fontWeight: 600,
  color: 'var(--text-primary)',
  margin: 0,
  overflow: 'hidden',
  textOverflow: 'ellipsis',
  whiteSpace: 'nowrap',
};

const userEmail = {
  fontSize: 12,
  color: 'var(--text-secondary)',
  margin: 0,
  overflow: 'hidden',
  textOverflow: 'ellipsis',
  whiteSpace: 'nowrap',
};

const logoutBtn = {
  background: 'none',
  border: 'none',
  cursor: 'pointer',
  color: 'var(--text-secondary)',
  display: 'flex',
  alignItems: 'center',
  padding: 4,
  borderRadius: '50%',
  transition: 'background 0.15s, color 0.15s',
  flexShrink: 0,
};

// ─── Icons (inline SVG) ───────────────────────────────────────────────────

function HomeIcon() {
  return (
    <svg width="22" height="22" viewBox="0 0 24 24" fill="currentColor">
      <path d="M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z"/>
    </svg>
  );
}

function ExploreIcon() {
  return (
    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
      <circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/>
    </svg>
  );
}

function ProfileIcon() {
  return (
    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
      <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>
    </svg>
  );
}

function ComposeIcon() {
  return (
    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
      <path d="M12 20h9"/><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"/>
    </svg>
  );
}

function LogoutIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
      <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
      <polyline points="16 17 21 12 16 7"/>
      <line x1="21" y1="12" x2="9" y2="12"/>
    </svg>
  );
}
