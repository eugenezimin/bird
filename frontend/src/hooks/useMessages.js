/**
 * Hook: fetch messages for a producer or subscriber.
 */
import { useState, useEffect } from 'react';
import { twitterApi } from '../api';

export function useMessagesForProducer(producerId) {
  const [messages, setMessages] = useState([]);
  const [loading,  setLoading]  = useState(false);
  const [error,    setError]    = useState(null);

  useEffect(() => {
    if (!producerId) return;
    setLoading(true);
    twitterApi.getMessagesByProducer(producerId)
      .then(res  => setMessages(res.data.data ?? []))
      .catch(err => setError(err))
      .finally(  () => setLoading(false));
  }, [producerId]);

  return { messages, loading, error };
}
