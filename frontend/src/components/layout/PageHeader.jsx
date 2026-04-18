import { useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';

// ─── Theme helpers ────────────────────────────────────────────────────────

function getInitialTheme() {
  try {
    const saved = localStorage.getItem('theme');
    if (saved === 'dark' || saved === 'light') return saved;
  } catch (_) {}
  return 'light'; // default
}

function applyTheme(theme) {
  document.documentElement.setAttribute('data-theme', theme);
  try { localStorage.setItem('theme', theme); } catch (_) {}
}

// Apply theme immediately on first load (before any React render)
if (typeof window !== 'undefined') {
  applyTheme(getInitialTheme());
}

// ─── ThemeToggle ──────────────────────────────────────────────────────────

function ThemeToggle() {
  const [theme, setTheme] = useState(getInitialTheme);

  useEffect(() => {
    applyTheme(theme);
  }, [theme]);

  const isDark = theme === 'dark';

  return (
    <button
      onClick={() => setTheme(isDark ? 'light' : 'dark')}
      aria-label={`Switch to ${isDark ? 'light' : 'dark'} mode`}
      style={toggleBtn}
      title={`Switch to ${isDark ? 'light' : 'dark'} mode`}
    >
      {/* Track */}
      <span style={track(isDark)}>
        {/* Thumb */}
        <span style={thumb(isDark)}>
          {isDark ? <MoonIcon /> : <SunIcon />}
        </span>
      </span>
    </button>
  );
}

// ─── PageHeader ───────────────────────────────────────────────────────────

export default function PageHeader({ title, subtitle, showBack = false }) {
  const navigate = useNavigate();
  return (
    <header style={header}>
      <div style={{ display: 'flex', alignItems: 'center', gap: 16, flex: 1 }}>
        {showBack && (
          <button onClick={() => navigate(-1)} style={backBtn}>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
              <polyline points="15 18 9 12 15 6"/>
            </svg>
          </button>
        )}
        <div>
          <h1 style={titleStyle}>{title}</h1>
          {subtitle && <p style={subtitleStyle}>{subtitle}</p>}
        </div>
      </div>
      <ThemeToggle />
    </header>
  );
}

// ─── Styles ───────────────────────────────────────────────────────────────

const header = {
  position: 'sticky',
  top: 0,
  zIndex: 10,
  display: 'flex',
  alignItems: 'center',
  gap: 16,
  padding: '12px 16px',
  backdropFilter: 'blur(12px)',
  background: 'var(--header-bg)',
  borderBottom: '1px solid var(--border)',
};

const backBtn = {
  background: 'none',
  border: 'none',
  cursor: 'pointer',
  color: 'var(--text-primary)',
  display: 'flex',
  alignItems: 'center',
  padding: 6,
  borderRadius: '50%',
  transition: 'background 0.15s',
};

const titleStyle = {
  fontFamily: 'var(--font-display)',
  fontSize: 20,
  fontWeight: 800,
  color: 'var(--text-primary)',
  margin: 0,
  lineHeight: 1.2,
};

const subtitleStyle = {
  fontSize: 13,
  color: 'var(--text-secondary)',
  margin: 0,
};

const toggleBtn = {
  background: 'none',
  border: 'none',
  padding: 0,
  cursor: 'pointer',
  display: 'flex',
  alignItems: 'center',
  flexShrink: 0,
};

const track = (isDark) => ({
  display: 'flex',
  alignItems: 'center',
  width: 52,
  height: 28,
  borderRadius: 999,
  background: isDark ? 'var(--accent)' : 'var(--border)',
  padding: '3px',
  transition: 'background 0.25s ease',
  position: 'relative',
});

const thumb = (isDark) => ({
  width: 22,
  height: 22,
  borderRadius: '50%',
  background: isDark ? '#fff' : '#fff',
  boxShadow: '0 1px 4px rgba(0,0,0,0.25)',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  transform: isDark ? 'translateX(24px)' : 'translateX(0)',
  transition: 'transform 0.25s ease',
  color: isDark ? '#1d9bf0' : '#f59e0b',
});

// ─── Icons ────────────────────────────────────────────────────────────────

function SunIcon() {
  return (
    <svg width="13" height="13" viewBox="0 0 24 24" fill="currentColor">
      <circle cx="12" cy="12" r="5"/>
      <line x1="12" y1="1" x2="12" y2="3" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
      <line x1="12" y1="21" x2="12" y2="23" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
      <line x1="4.22" y1="4.22" x2="5.64" y2="5.64" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
      <line x1="18.36" y1="18.36" x2="19.78" y2="19.78" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
      <line x1="1" y1="12" x2="3" y2="12" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
      <line x1="21" y1="12" x2="23" y2="12" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
      <line x1="4.22" y1="19.78" x2="5.64" y2="18.36" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
      <line x1="18.36" y1="5.64" x2="19.78" y2="4.22" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
    </svg>
  );
}

function MoonIcon() {
  return (
    <svg width="12" height="12" viewBox="0 0 24 24" fill="currentColor">
      <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/>
    </svg>
  );
}
