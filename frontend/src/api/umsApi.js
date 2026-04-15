import axios from 'axios';

const client = axios.create({
  baseURL: import.meta.env.VITE_UMS_BASE_URL ?? 'http://localhost:9000',
  headers: { 'Content-Type': 'application/json' },
});

// intercept and unwrap the { code, message, data } envelope
client.interceptors.response.use((res) => res);

export const getAllUsers  = ()            => client.get('/users');
export const getUserById  = (id)          => client.get(`/users/user/${id}`);
export const createUser   = (payload)     => client.post('/users/user', payload);
export const updateUser   = (id, payload) => client.put(`/users/user/${id}`, payload);
export const deleteUser   = (id)          => client.delete(`/users/user/${id}`);

export const getAllRoles  = ()            => client.get('/roles');

export const openSession  = (userId)      => client.post(`/sessions/user/${userId}`);
export const closeSession = (sessionId)   => client.delete(`/sessions/${sessionId}`);
export const getLastSession = (userId)    => client.get(`/sessions/user/${userId}/last`);
