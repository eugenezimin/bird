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
