import { useNavigate } from 'react-router-dom';

export default function PageHeader({ title, subtitle, showBack = false }) {
  const navigate = useNavigate();
  return (
    <header style={header}>
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
    </header>
  );
}

const header = {
  position: 'sticky',
  top: 0,
  zIndex: 10,
  display: 'flex',
  alignItems: 'center',
  gap: 16,
  padding: '12px 16px',
  backdropFilter: 'blur(12px)',
  background: 'rgba(4, 76, 118, 0.75)',
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
