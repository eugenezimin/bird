/**
 * Twitter Service API client (port 9001)
 * UMS must be running before this service works.
 */
import axios from 'axios';

const twitterClient = axios.create({
  baseURL: import.meta.env.VITE_TWITTER_BASE_URL ?? 'http://localhost:9001',
  headers: { 'Content-Type': 'application/json' },
});

// ---- Messages ----
export const getMessageById          = (id)          => twitterClient.get(`/messages/message/${id}`);
export const getMessagesByProducer   = (producerId)  => twitterClient.get(`/messages/producer/${producerId}`);
export const getMessagesBySubscriber = (subscriberId)=> twitterClient.get(`/messages/subscriber/${subscriberId}`);
export const createMessage           = (payload)     => twitterClient.post('/messages/message', payload);
export const deleteMessage           = (id)          => twitterClient.delete(`/messages/message/${id}`);

// ---- Subscriptions ----
export const getSubscription         = (subscriberId)=> twitterClient.get(`/subscriptions/subscriber/${subscriberId}`);
export const createSubscription      = (payload)     => twitterClient.post('/subscriptions', payload);
export const updateSubscription      = (payload)     => twitterClient.put('/subscriptions', payload);
export const deleteSubscription      = (subscriberId)=> twitterClient.delete(`/subscriptions/subscriber/${subscriberId}`);
