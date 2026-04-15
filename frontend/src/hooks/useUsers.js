import { useState, useEffect } from 'react';
import { umsApi } from '../api';
import { safeExtract } from '../utils';

export function useUsers() {
  const [users,   setUsers]   = useState([]);
  const [loading, setLoading] = useState(false);
  const [error,   setError]   = useState(null);

  useEffect(() => {
    setLoading(true);
    umsApi.getAllUsers()
      .then(res  => setUsers(safeExtract(res, [])))
      .catch(err => setError(err.message))
      .finally(  () => setLoading(false));
  }, []);

  return { users, loading, error };
}
