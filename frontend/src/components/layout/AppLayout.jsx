import { Outlet, Navigate } from 'react-router-dom';
import { useUserStore } from '../../store';
import Sidebar from './Sidebar';

export default function AppLayout() {
  const currentUser = useUserStore(s => s.currentUser);

  // Guard: must have selected a user
  if (!currentUser) return <Navigate to="/" replace />;

  return (
    <div style={shell}>
      <Sidebar />
      <main style={main}>
        <Outlet />
      </main>
      {/* Right column reserved for future widgets */}
      <aside style={right} />
    </div>
  );
}

const shell = {
  display: 'flex',
  minHeight: '100vh',
  maxWidth: 1280,
  margin: '0 auto',
};

const main = {
  flex: 1,
  borderRight: '1px solid var(--border)',
  minHeight: '100vh',
  minWidth: 0,
};

const right = {
  width: 'var(--right-w)',
  flexShrink: 0,
};
