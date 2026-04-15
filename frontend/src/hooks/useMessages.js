import { useState, useEffect, useCallback } from 'react';
import { twitterApi } from '../api';
import { safeExtract } from '../utils';

export function useFeed(subscriberId) {
  const [messages, setMessages] = useState([]);
  const [loading,  setLoading]  = useState(false);
  const [error,    setError]    = useState(null);

  const load = useCallback(() => {
    if (!subscriberId) return;
    setLoading(true);
    setError(null);
    twitterApi.getMessagesBySubscriber(subscriberId)
      .then(res  => setMessages(safeExtract(res, [])))
      .catch(err => setError(err.message))
      .finally(  () => setLoading(false));
  }, [subscriberId]);

  useEffect(() => { load(); }, [load]);

  return { messages, loading, error, reload: load, setMessages };
}

export function useProducerMessages(producerId) {
  const [messages, setMessages] = useState([]);
  const [loading,  setLoading]  = useState(false);
  const [error,    setError]    = useState(null);

  useEffect(() => {
    if (!producerId) return;
    setLoading(true);
    twitterApi.getMessagesByProducer(producerId)
      .then(res  => setMessages(safeExtract(res, [])))
      .catch(err => setError(err.message))
      .finally(  () => setLoading(false));
  }, [producerId]);

  return { messages, loading, error };
}
