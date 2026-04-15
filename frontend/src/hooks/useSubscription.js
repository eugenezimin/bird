import { useState, useEffect, useCallback } from 'react';
import { twitterApi } from '../api';
import { safeExtract } from '../utils';

export function useSubscription(subscriberId) {
  const [subscription, setSubscription] = useState(null);
  const [loading,      setLoading]      = useState(false);

  const load = useCallback(() => {
    if (!subscriberId) return;
    setLoading(true);
    twitterApi.getSubscription(subscriberId)
      .then(res => setSubscription(safeExtract(res, null)))
      .catch(() => setSubscription(null))
      .finally(() => setLoading(false));
  }, [subscriberId]);

  useEffect(() => { load(); }, [load]);

  const follow = async (producerId) => {
    const current = subscription?.producers ?? [];
    if (current.includes(producerId)) return;
    const next = [...current, producerId];
    if (current.length === 0) {
      await twitterApi.createSubscription({ subscriber: subscriberId, producers: next });
    } else {
      await twitterApi.updateSubscription({ subscriber: subscriberId, producers: next });
    }
    load();
  };

  const unfollow = async (producerId) => {
    const current = subscription?.producers ?? [];
    const next = current.filter(id => id !== producerId);
    await twitterApi.updateSubscription({ subscriber: subscriberId, producers: next });
    load();
  };

  const isFollowing = (producerId) =>
    subscription?.producers?.includes(producerId) ?? false;

  return { subscription, loading, follow, unfollow, isFollowing, reload: load };
}
