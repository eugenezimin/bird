import { useUsers, useSubscription } from '../hooks';
import { useUserStore } from '../store';
import { Spinner, EmptyState } from '../components/ui';
import UserCard from '../components/users/UserCard';
import PageHeader from '../components/layout/PageHeader';

export default function ExplorePage() {
  const currentUser = useUserStore(s => s.currentUser);
  const { users, loading, error } = useUsers();
  const { follow, unfollow, isFollowing } = useSubscription(currentUser?.id);

  const others = users.filter(u => u.id !== currentUser?.id);
  const producers   = others.filter(u => u.roles?.some(r => r.role === 'PRODUCER'));
  const subscribers = others.filter(u => !u.roles?.some(r => r.role === 'PRODUCER'));

  return (
    <div>
      <PageHeader title="Explore" subtitle={`${users.length} users`} />

      {loading && <Spinner />}
      {error   && <p style={{ color: 'var(--danger)', padding: 24 }}>{error}</p>}

      {!loading && !error && others.length === 0 && (
        <EmptyState icon="👥" title="No other users" subtitle="Add some users via the API to get started." />
      )}

      {producers.length > 0 && (
        <Section label={`Producers · ${producers.length}`}>
          {producers.map(u => (
            <UserCard
              key={u.id}
              user={u}
              isFollowing={isFollowing(u.id)}
              onFollow={() => follow(u.id)}
              onUnfollow={() => unfollow(u.id)}
            />
          ))}
        </Section>
      )}

      {subscribers.length > 0 && (
        <Section label={`Subscribers · ${subscribers.length}`}>
          {subscribers.map(u => (
            <UserCard key={u.id} user={u} />
          ))}
        </Section>
      )}
    </div>
  );
}

function Section({ label, children }) {
  return (
    <section style={{ marginBottom: 8 }}>
      <p style={{
        fontSize: 12, fontWeight: 700, letterSpacing: '0.08em',
        textTransform: 'uppercase', color: 'var(--text-secondary)',
        padding: '16px 16px 8px',
      }}>
        {label}
      </p>
      {children}
    </section>
  );
}
