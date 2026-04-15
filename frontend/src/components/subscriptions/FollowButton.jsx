import { useState } from 'react';

export default function FollowButton({ isFollowing, onFollow, onUnfollow, disabled }) {
  const [hovered, setHovered] = useState(false);
  const [loading, setLoading] = useState(false);

  async function handleClick() {
    setLoading(true);
    try {
      if (isFollowing) await onUnfollow?.();
      else             await onFollow?.();
    } finally {
      setLoading(false);
    }
  }

  const label = loading
    ? '…'
    : isFollowing
      ? hovered ? 'Unfollow' : 'Following'
      : 'Follow';

  return (
    <button
      onClick={handleClick}
      disabled={disabled || loading}
      onMouseEnter={() => setHovered(true)}
      onMouseLeave={() => setHovered(false)}
      style={{
        borderRadius: 'var(--radius-full)',
        padding: '6px 18px',
        fontSize: 14,
        fontWeight: 700,
        fontFamily: 'var(--font-ui)',
        cursor: disabled || loading ? 'default' : 'pointer',
        transition: 'background 0.15s, color 0.15s, border-color 0.15s',
        border: isFollowing ? '1px solid var(--border)' : 'none',
        background: isFollowing
          ? hovered ? 'var(--danger-dim)' : 'transparent'
          : 'var(--text-primary)',
        color: isFollowing
          ? hovered ? 'var(--danger)' : 'var(--text-primary)'
          : 'var(--bg-primary)',
        opacity: loading ? 0.6 : 1,
      }}
    >
      {label}
    </button>
  );
}
