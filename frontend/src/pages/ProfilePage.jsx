import { useParams } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { umsApi } from '../api';
import { safeExtract, formatFull } from '../utils';
import { useProducerMessages, useSubscription } from '../hooks';
import { useUserStore } from '../store';
import { Avatar, RoleBadge, Spinner, EmptyState } from '../components/ui';
import { MessageList } from '../components/messages';
import FollowButton from '../components/subscriptions/FollowButton';
import PageHeader from '../components/layout/PageHeader';

export default function ProfilePage() {
  const { userId }    = useParams();
  const currentUser   = useUserStore(s => s.currentUser);
  const [user, setUser]         = useState(null);
  const [loadingUser, setLoading] = useState(false);

  const { messages, loading: loadingMsgs } = useProducerMessages(userId);
  const { follow, unfollow, isFollowing }  = useSubscription(currentUser?.id);

  const isOwn      = currentUser?.id === userId;
  const isProducer = user?.roles?.some(r => r.role === 'PRODUCER');

  useEffect(() => {
    if (!userId) return;
    setLoading(true);
    umsApi.getUserById(userId)
      .then(res => setUser(safeExtract(res, null)))
      .finally(()  => setLoading(false));
  }, [userId]);

  if (loadingUser) return <><PageHeader title="Profile" showBack /><Spinner /></>;
  if (!user) return (
    <>
      <PageHeader title="Profile" showBack />
      <EmptyState icon="❓" title="User not found" />
    </>
  );

  return (
    <div>
      <PageHeader title={user.name} subtitle={`${messages.length} posts`} showBack />

      {/* Profile banner */}
      <div style={banner} />

      <div style={profileBody}>
        <div style={avatarRow}>
          <Avatar name={user.name} size={72} />
          {!isOwn && isProducer && (
            <FollowButton
              isFollowing={isFollowing(userId)}
              onFollow={() => follow(userId)}
              onUnfollow={() => unfollow(userId)}
            />
          )}
        </div>

        <h2 style={displayName}>{user.name}</h2>
        <p style={handle}>{user.email}</p>

        <div style={roleRow}>
          {user.roles?.map(r => <RoleBadge key={r.role} role={r.role} />)}
        </div>

        {user.createdAt && (
          <p style={joined}>Joined {formatFull(user.createdAt)}</p>
        )}

        {user.lastSession && (
          <p style={session}>
            Last session: {formatFull(user.lastSession.loggedInAt)}
            {user.lastSession.loggedOutAt ? ` → ${formatFull(user.lastSession.loggedOutAt)}` : ' (active)'}
          </p>
        )}
      </div>

      {/* Tabs */}
      <div style={tabs}>
        <span style={tab(true)}>Posts</span>
      </div>

      {/* Posts */}
      {isProducer ? (
        <MessageList
          messages={messages}
          loading={loadingMsgs}
          error={null}
          onDelete={null}
        />
      ) : (
        <EmptyState
          icon="📬"
          title="Subscriber only"
          subtitle="This user doesn't post — they only read."
        />
      )}
    </div>
  );
}

// ─── Styles ───────────────────────────────────────────────────────────────

const banner = {
  height: 140,
  background: 'linear-gradient(135deg, #0a1628 0%, #1d2d44 50%, #0d1b2a 100%)',
  borderBottom: '1px solid var(--border)',
};

const profileBody = {
  padding: '12px 16px 0',
  borderBottom: '1px solid var(--border)',
};

const avatarRow = {
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'flex-end',
  marginTop: -36,
  marginBottom: 12,
};

const displayName = {
  fontFamily: 'var(--font-display)',
  fontSize: 22,
  fontWeight: 800,
  color: 'var(--text-primary)',
  margin: '0 0 2px',
};

const handle = {
  fontSize: 14,
  color: 'var(--text-secondary)',
  margin: '0 0 10px',
};

const roleRow = {
  display: 'flex',
  gap: 6,
  flexWrap: 'wrap',
  marginBottom: 10,
};

const joined = {
  fontSize: 13,
  color: 'var(--text-secondary)',
  margin: '0 0 4px',
};

const session = {
  fontSize: 12,
  color: 'var(--text-muted)',
  margin: '0 0 16px',
};

const tabs = {
  display: 'flex',
  borderBottom: '1px solid var(--border)',
};

const tab = (active) => ({
  padding: '16px 20px',
  fontSize: 15,
  fontWeight: active ? 700 : 400,
  color: active ? 'var(--text-primary)' : 'var(--text-secondary)',
  borderBottom: active ? '2px solid var(--accent)' : '2px solid transparent',
  cursor: 'pointer',
});
