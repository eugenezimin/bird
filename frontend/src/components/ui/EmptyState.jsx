export default function EmptyState({ icon = '🐦', title, subtitle }) {
  return (
    <div style={{
      display: 'flex', flexDirection: 'column', alignItems: 'center',
      justifyContent: 'center', padding: '64px 32px', gap: 12,
      animation: 'fadeIn 0.3s ease',
    }}>
      <span style={{ fontSize: 48, lineHeight: 1 }}>{icon}</span>
      {title && (
        <p style={{ fontFamily: 'var(--font-display)', fontSize: 22, fontWeight: 800, color: 'var(--text-primary)', textAlign: 'center' }}>
          {title}
        </p>
      )}
      {subtitle && (
        <p style={{ fontSize: 14, color: 'var(--text-secondary)', textAlign: 'center', maxWidth: 280, lineHeight: 1.6 }}>
          {subtitle}
        </p>
      )}
    </div>
  );
}
