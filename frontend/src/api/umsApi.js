/**
 * UMS Service API client (port 9000)
 * All requests return the standard envelope: { code, message, data }
 */
import axios from 'axios';

const umsClient = axios.create({
  baseURL: import.meta.env.VITE_UMS_BASE_URL ?? 'http://localhost:9000',
  headers: { 'Content-Type': 'application/json' },
});

// ---- Users ----
export const getAllUsers        = ()           => umsClient.get('/users');
export const getUserById        = (id)         => umsClient.get(`/users/user/${id}`);
export const createUser         = (payload)    => umsClient.post('/users/user', payload);
export const updateUser         = (id, payload)=> umsClient.put(`/users/user/${id}`, payload);
export const deleteUser         = (id)         => umsClient.delete(`/users/user/${id}`);

// ---- Roles ----
export const getAllRoles         = ()           => umsClient.get('/roles');

// ---- Sessions ----
export const openSession        = (userId)     => umsClient.post(`/sessions/user/${userId}`);
export const closeSession       = (sessionId)  => umsClient.delete(`/sessions/${sessionId}`);
export const getLastSession     = (userId)     => umsClient.get(`/sessions/user/${userId}/last`);
