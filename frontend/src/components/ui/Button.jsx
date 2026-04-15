import styles from './Button.module.css';

export default function Button({
  children,
  variant = 'primary',   // 'primary' | 'outline' | 'ghost' | 'danger'
  size    = 'md',        // 'sm' | 'md' | 'lg'
  full    = false,
  loading = false,
  disabled = false,
  onClick,
  style = {},
  ...props
}) {
  return (
    <button
      className={[
        styles.btn,
        styles[variant],
        styles[size],
        full ? styles.full : '',
      ].join(' ')}
      disabled={disabled || loading}
      onClick={onClick}
      style={style}
      {...props}
    >
      {loading ? <span className={styles.spinner} /> : children}
    </button>
  );
}
