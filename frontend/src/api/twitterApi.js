import axios from 'axios';

const client = axios.create({
  baseURL: import.meta.env.VITE_TWITTER_BASE_URL ?? 'http://localhost:9001',
  headers: { 'Content-Type': 'application/json' },
});

export const getMessageById          = (id)           => client.get(`/messages/message/${id}`);
export const getMessagesByProducer   = (producerId)   => client.get(`/messages/producer/${producerId}`);
export const getMessagesBySubscriber = (subscriberId) => client.get(`/messages/subscriber/${subscriberId}`);
export const createMessage           = (payload)      => client.post('/messages/message', payload);
export const deleteMessage           = (id)           => client.delete(`/messages/message/${id}`);

export const getSubscription    = (subscriberId) => client.get(`/subscriptions/subscriber/${subscriberId}`);
export const createSubscription = (payload)      => client.post('/subscriptions', payload);
export const updateSubscription = (payload)      => client.put('/subscriptions', payload);
export const deleteSubscription = (subscriberId) => client.delete(`/subscriptions/subscriber/${subscriberId}`);
