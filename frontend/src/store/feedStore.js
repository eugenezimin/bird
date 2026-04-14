/**
 * Feed / message list state for the current subscriber's timeline.
 */
import { create } from 'zustand';

export const useFeedStore = create((set) => ({
  messages:    [],
  isLoading:   false,
  error:       null,
  setMessages: (messages) => set({ messages }),
  setLoading:  (isLoading)=> set({ isLoading }),
  setError:    (error)    => set({ error }),
}));
