import { useEffect, useState } from 'react';
import { umsApi } from '../../api';
import { safeExtract } from '../../utils';
import MessageCard from './MessageCard';
import { Spinner, EmptyState } from '../ui';

export default function MessageList({ messages, loading, error, onDelete }) {
  const [authorMap, setAuthorMap] = useState({});

  // Resolve unique author IDs → names
  useEffect(() => {
    const ids = [...new Set(messages.map(m => m.author).filter(Boolean))];
    const unknown = ids.filter(id => !authorMap[id]);
    if (unknown.length === 0) return;

    unknown.forEach(id => {
      umsApi.getUserById(id)
        .then(res => {
          const user = safeExtract(res, null);
          if (user?.name) {
            setAuthorMap(prev => ({ ...prev, [id]: user.name }));
          }
        })
        .catch(() => {});
    });
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [messages]);

  if (loading) return <Spinner />;
  if (error)   return <p style={{ color: 'var(--danger)', padding: 24, textAlign: 'center' }}>{error}</p>;
  if (messages.length === 0) return (
    <EmptyState
      icon="🐦"
      title="Nothing here yet"
      subtitle="Posts from people you follow will appear here."
    />
  );

  return (
    <div>
      {messages.map(msg => (
        <MessageCard
          key={msg.id}
          message={msg}
          authorName={authorMap[msg.author]}
          onDelete={onDelete}
        />
      ))}
    </div>
  );
}
