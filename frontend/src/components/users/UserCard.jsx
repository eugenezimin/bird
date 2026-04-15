import { Link } from 'react-router-dom';
import { Avatar, RoleBadge } from '../ui';
import FollowButton from '../subscriptions/FollowButton';

export default function UserCard({ user, isFollowing, onFollow, onUnfollow, isCurrentUser }) {
  const isProducer = user.roles?.some(r => r.role === 'PRODUCER');

  return (
    <div style={card}>
      <Link to={`/profile/${user.id}`} style={left}>
        <Avatar name={user.name} size={44} />
        <div style={{ minWidth: 0 }}>
          <p style={name}>{user.name}</p>
          <p style={email}>{user.email}</p>
          <div style={{ display: 'flex', gap: 4, marginTop: 4, flexWrap: 'wrap' }}>
            {user.roles?.map(r => <RoleBadge key={r.role} role={r.role} />)}
          </div>
        </div>
      </Link>

      {isProducer && !isCurrentUser && (
        <FollowButton
          isFollowing={isFollowing}
          onFollow={onFollow}
          onUnfollow={onUnfollow}
        />
      )}
    </div>
  );
}

const card = {
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'space-between',
  gap: 12,
  padding: '12px 16px',
  borderBottom: '1px solid var(--border)',
  transition: 'background 0.15s',
};

const left = {
  display: 'flex',
  alignItems: 'center',
  gap: 12,
  textDecoration: 'none',
  flex: 1,
  minWidth: 0,
};

const name = {
  fontSize: 15,
  fontWeight: 700,
  color: 'var(--text-primary)',
  margin: 0,
  overflow: 'hidden',
  textOverflow: 'ellipsis',
  whiteSpace: 'nowrap',
};

const email = {
  fontSize: 13,
  color: 'var(--text-secondary)',
  margin: 0,
  overflow: 'hidden',
  textOverflow: 'ellipsis',
  whiteSpace: 'nowrap',
};
