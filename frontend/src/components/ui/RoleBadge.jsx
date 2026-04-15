const COLORS = {
  ADMIN:      { bg: 'rgba(249,24,128,0.15)', color: '#f91880' },
  PRODUCER:   { bg: 'rgba(29,155,240,0.15)', color: '#1d9bf0' },
  SUBSCRIBER: { bg: 'rgba(0,186,124,0.15)',  color: '#00ba7c' },
};

export default function RoleBadge({ role }) {
  const c = COLORS[role?.toUpperCase()] ?? { bg: 'var(--bg-card)', color: 'var(--text-secondary)' };
  return (
    <span style={{
      fontSize: 11, fontWeight: 600, letterSpacing: '0.04em',
      textTransform: 'uppercase',
      padding: '2px 8px', borderRadius: 'var(--radius-full)',
      background: c.bg, color: c.color,
      display: 'inline-block',
    }}>
      {role}
    </span>
  );
}
