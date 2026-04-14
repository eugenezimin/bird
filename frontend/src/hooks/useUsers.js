/**
 * Hook: fetch the full user list from UMS.
 */
import { useState, useEffect } from 'react';
import { umsApi } from '../api';

export function useUsers() {
  const [users,   setUsers]   = useState([]);
  const [loading, setLoading] = useState(false);
  const [error,   setError]   = useState(null);

  useEffect(() => {
    setLoading(true);
    umsApi.getAllUsers()
      .then(res  => setUsers(res.data.data ?? []))
      .catch(err => setError(err))
      .finally(  () => setLoading(false));
  }, []);

  return { users, loading, error };
}
