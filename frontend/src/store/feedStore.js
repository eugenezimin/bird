import { create } from 'zustand';

export const useFeedStore = create((set, get) => ({
  messages:  [],
  isLoading: false,
  error:     null,

  setMessages: (messages)  => set({ messages }),
  prependMessage: (msg)    => set({ messages: [msg, ...get().messages] }),
  removeMessage: (id)      => set({ messages: get().messages.filter(m => m.id !== id) }),
  setLoading:  (isLoading) => set({ isLoading }),
  setError:    (error)     => set({ error }),
  clear:       ()          => set({ messages: [], error: null }),
}));
