import { avatarColors, initials } from '../../utils/avatar';

export default function Avatar({ name = '', size = 40, style = {} }) {
  const colors = avatarColors(name);
  const fontSize = Math.round(size * 0.36);

  return (
    <div
      style={{
        width: size,
        height: size,
        minWidth: size,
        borderRadius: '50%',
        background: colors.bg,
        color: colors.text,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        fontSize,
        fontWeight: 600,
        fontFamily: 'var(--font-ui)',
        letterSpacing: '0.02em',
        userSelect: 'none',
        ...style,
      }}
    >
      {initials(name)}
    </div>
  );
}
