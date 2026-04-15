import { useNavigate } from 'react-router-dom';
import { useUserStore } from '../store';
import { twitterApi } from '../api';
import { safeExtract } from '../utils';
import ComposeBox from '../components/messages/ComposeBox';
import PageHeader from '../components/layout/PageHeader';

export default function ComposePage() {
  const navigate    = useNavigate();
  const currentUser = useUserStore(s => s.currentUser);

  if (!currentUser?.roles?.some(r => r.role === 'PRODUCER')) {
    return <p style={{ padding: 24, color: 'var(--text-secondary)' }}>Only producers can post.</p>;
  }

  function handlePosted() {
    navigate('/feed');
  }

  return (
    <div>
      <PageHeader title="New Post" showBack />
      <ComposeBox onPosted={handlePosted} />
    </div>
  );
}