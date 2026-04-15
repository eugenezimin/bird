import { useUserStore } from '../store';
import { useFeed } from '../hooks';
import { ComposeBox, MessageList } from '../components/messages';
import PageHeader from '../components/layout/PageHeader';

export default function FeedPage() {
  const currentUser = useUserStore(s => s.currentUser);
  const { messages, loading, error, setMessages } = useFeed(currentUser?.id);

  function handlePosted(newMsg) {
    setMessages(prev => [newMsg, ...prev]);
  }

  function handleDelete(id) {
    setMessages(prev => prev.filter(m => m.id !== id));
  }

  return (
    <div>
      <PageHeader title="Home" />
      <ComposeBox onPosted={handlePosted} />
      <MessageList
        messages={messages}
        loading={loading}
        error={error}
        onDelete={handleDelete}
      />
    </div>
  );
}
