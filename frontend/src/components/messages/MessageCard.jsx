import { useState } from 'react';
import { twitterApi } from '../../api';
import { useUserStore } from '../../store';
import { Avatar } from '../ui';
import { timeAgo } from '../../utils';

export default function MessageCard({ message, onDelete, authorName }) {
  const currentUser  = useUserStore(s => s.currentUser);
  const [liked,      setLiked]      = useState(false);
  const [likeCount,  setLikeCount]  = useState(Math.floor(Math.random() * 40));
  const [deleting,   setDeleting]   = useState(false);
  const [likeAnim,   setLikeAnim]   = useState(false);

  const isOwn = currentUser?.id === message.author;

  function handleLike() {
    setLiked(l => !l);
    setLikeCount(c => liked ? c - 1 : c + 1);
    setLikeAnim(true);
    setTimeout(() => setLikeAnim(false), 300);
  }

  async function handleDelete() {
    if (!window.confirm('Delete this post?')) return;
    setDeleting(true);
    try {
      await twitterApi.deleteMessage(message.id);
      onDelete?.(message.id);
    } catch {
      setDeleting(false);
    }
  }

  const displayName = authorName ?? `User ${message.author?.slice(0, 8)}`;

  return (
    <article style={card}>
      <div style={cardLeft}>
        <Avatar name={displayName} size={42} />
        <div style={threadLine} />
      </div>

      <div style={cardBody}>
        {/* Header */}
        <div style={header}>
          <span style={name}>{displayName}</span>
          <span style={meta}>·</span>
          <span style={meta}>{timeAgo(message.createdAt)}</span>
          {isOwn && (
            <button
              onClick={handleDelete}
              disabled={deleting}
              style={deleteBtn}
              title="Delete"
            >
              <TrashIcon />
            </button>
          )}
        </div>

        {/* Content */}
        <p style={content}>{message.content}</p>

        {/* Actions */}
        <div style={actions}>
          <ActionBtn
            icon={<LikeIcon filled={liked} />}
            count={likeCount}
            active={liked}
            activeColor="var(--like)"
            activeBg="var(--like-dim)"
            onClick={handleLike}
            style={likeAnim ? { animation: 'pop 0.3s ease' } : {}}
          />
          <ActionBtn
            icon={<CommentIcon />}
            count={0}
            activeColor="var(--accent)"
            activeBg="var(--accent-dim)"
          />
          <ActionBtn
            icon={<ShareIcon />}
            count={null}
            activeColor="var(--accent)"
            activeBg="var(--accent-dim)"
          />
        </div>
      </div>
    </article>
  );
}

function ActionBtn({ icon, count, active, activeColor, activeBg, onClick, style = {} }) {
  const [hovered, setHovered] = useState(false);
  return (
    <button
      onClick={onClick}
      onMouseEnter={() => setHovered(true)}
      onMouseLeave={() => setHovered(false)}
      style={{
        display: 'flex', alignItems: 'center', gap: 5,
        background: 'none', border: 'none', cursor: onClick ? 'pointer' : 'default',
        color: active ? activeColor : hovered ? activeColor : 'var(--text-secondary)',
        fontSize: 13, padding: '4px 8px 4px 4px',
        borderRadius: 'var(--radius-full)',
        backgroundColor: hovered && onClick ? activeBg : 'transparent',
        transition: 'color 0.15s, background 0.15s',
        ...style,
      }}
    >
      {icon}
      {count !== null && <span>{count}</span>}
    </button>
  );
}

// ─── Styles ───────────────────────────────────────────────────────────────

const card = {
  display: 'flex',
  gap: 12,
  padding: '12px 16px',
  borderBottom: '1px solid var(--border)',
  animation: 'fadeIn 0.2s ease',
  transition: 'background 0.15s',
};

const cardLeft = {
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
  flexShrink: 0,
};

const threadLine = {
  flex: 1,
  width: 2,
  background: 'var(--border)',
  marginTop: 4,
  minHeight: 0,
  display: 'none',
};

const cardBody = {
  flex: 1,
  minWidth: 0,
};

const header = {
  display: 'flex',
  alignItems: 'center',
  gap: 6,
  marginBottom: 4,
  flexWrap: 'wrap',
};

const name = {
  fontSize: 15,
  fontWeight: 700,
  color: 'var(--text-primary)',
};

const meta = {
  fontSize: 14,
  color: 'var(--text-secondary)',
};

const content = {
  fontSize: 15,
  color: 'var(--text-primary)',
  lineHeight: 1.6,
  marginBottom: 10,
  whiteSpace: 'pre-wrap',
  wordBreak: 'break-word',
};

const actions = {
  display: 'flex',
  gap: 16,
  marginTop: 4,
};

const deleteBtn = {
  marginLeft: 'auto',
  background: 'none',
  border: 'none',
  cursor: 'pointer',
  color: 'var(--text-muted)',
  display: 'flex',
  alignItems: 'center',
  padding: 4,
  borderRadius: '50%',
  transition: 'color 0.15s',
};

// ─── Icons ────────────────────────────────────────────────────────────────

function LikeIcon({ filled }) {
  return (
    <svg width="17" height="17" viewBox="0 0 24 24" fill={filled ? 'var(--like)' : 'none'} stroke="currentColor" strokeWidth="2">
      <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
    </svg>
  );
}

function CommentIcon() {
  return (
    <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
      <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
    </svg>
  );
}

function ShareIcon() {
  return (
    <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
      <path d="M4 12v8a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8"/>
      <polyline points="16 6 12 2 8 6"/>
      <line x1="12" y1="2" x2="12" y2="15"/>
    </svg>
  );
}

function TrashIcon() {
  return (
    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
      <polyline points="3 6 5 6 21 6"/>
      <path d="M19 6l-1 14H6L5 6"/>
      <path d="M10 11v6M14 11v6"/>
      <path d="M9 6V4h6v2"/>
    </svg>
  );
}
