import { useState } from 'react';
import { twitterApi } from '../../api';
import { useUserStore } from '../../store';
import { safeExtract } from '../../utils';
import { Avatar } from '../ui';

const MAX_CHARS = 280;

export default function ComposeBox({ onPosted }) {
  const currentUser = useUserStore(s => s.currentUser);
  const [content,   setContent]   = useState('');
  const [posting,   setPosting]   = useState(false);
  const [error,     setError]     = useState(null);
  const [focused,   setFocused]   = useState(false);

  const isProducer = currentUser?.roles?.some(r => r.role === 'PRODUCER');
  if (!isProducer) return null;

  const remaining = MAX_CHARS - content.length;
  const overLimit = remaining < 0;
  const nearLimit = remaining <= 20;

  async function handlePost() {
    if (!content.trim() || overLimit || posting) return;
    setPosting(true);
    setError(null);
    try {
      const res = await twitterApi.createMessage({
        author:  currentUser.id,
        content: content.trim(),
      });
      const id = safeExtract(res, null);
      if (id) {
        onPosted?.({
          id,
          author:    currentUser.id,
          content:   content.trim(),
          createdAt: new Date().toISOString(),
        });
        setContent('');
      }
    } catch (e) {
      setError(e.message);
    } finally {
      setPosting(false);
    }
  }

  function handleKey(e) {
    if (e.key === 'Enter' && (e.metaKey || e.ctrlKey)) handlePost();
  }

  const circumference = 2 * Math.PI * 10;
  const progress = Math.min(content.length / MAX_CHARS, 1);
  const dashOffset = circumference * (1 - progress);

  return (
    <div style={box(focused)}>
      <Avatar name={currentUser?.name ?? ''} size={42} style={{ flexShrink: 0 }} />

      <div style={{ flex: 1, minWidth: 0 }}>
        <textarea
          value={content}
          onChange={e => setContent(e.target.value)}
          onKeyDown={handleKey}
          onFocus={() => setFocused(true)}
          onBlur={() => setFocused(false)}
          placeholder="What's happening?"
          rows={focused || content ? 3 : 1}
          style={textarea}
        />

        {error && <p style={{ color: 'var(--danger)', fontSize: 13, marginBottom: 8 }}>{error}</p>}

        <div style={footer}>
          {/* Char counter ring */}
          <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
            {content.length > 0 && (
              <div style={{ position: 'relative', width: 24, height: 24 }}>
                <svg width="24" height="24" style={{ transform: 'rotate(-90deg)' }}>
                  <circle cx="12" cy="12" r="10" fill="none" stroke="var(--border)" strokeWidth="2" />
                  <circle
                    cx="12" cy="12" r="10" fill="none"
                    stroke={overLimit ? 'var(--danger)' : nearLimit ? '#ffd700' : 'var(--accent)'}
                    strokeWidth="2"
                    strokeDasharray={circumference}
                    strokeDashoffset={dashOffset}
                    style={{ transition: 'stroke-dashoffset 0.1s' }}
                  />
                </svg>
                {nearLimit && (
                  <span style={{
                    position: 'absolute', inset: 0,
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                    fontSize: 9, fontWeight: 700,
                    color: overLimit ? 'var(--danger)' : 'var(--text-secondary)',
                  }}>
                    {remaining}
                  </span>
                )}
              </div>
            )}
          </div>

          <button
            onClick={handlePost}
            disabled={!content.trim() || overLimit || posting}
            style={postBtn(posting)}
          >
            {posting ? 'Posting…' : 'Post'}
          </button>
        </div>
      </div>
    </div>
  );
}

// ─── Styles ───────────────────────────────────────────────────────────────

const box = (focused) => ({
  display: 'flex',
  gap: 12,
  padding: '12px 16px',
  borderBottom: '1px solid var(--border)',
  background: focused ? 'rgba(29,155,240,0.02)' : 'transparent',
  transition: 'background 0.2s',
});

const textarea = {
  width: '100%',
  background: 'transparent',
  border: 'none',
  outline: 'none',
  resize: 'none',
  color: 'var(--text-primary)',
  fontSize: 18,
  lineHeight: 1.5,
  fontFamily: 'var(--font-ui)',
  padding: '8px 0',
  transition: 'height 0.15s',
};

const footer = {
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'space-between',
  paddingTop: 8,
  borderTop: '1px solid var(--border)',
  marginTop: 4,
};

const postBtn = (loading) => ({
  background: 'var(--accent)',
  color: '#fff',
  border: 'none',
  borderRadius: 'var(--radius-full)',
  padding: '8px 20px',
  fontWeight: 700,
  fontSize: 15,
  cursor: loading ? 'wait' : 'pointer',
  opacity: loading ? 0.7 : 1,
  transition: 'background 0.15s, opacity 0.15s',
  fontFamily: 'var(--font-ui)',
});
