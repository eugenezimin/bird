import { create } from 'zustand';
import { persist } from 'zustand/middleware';

export const useUserStore = create(
  persist(
    (set) => ({
      currentUser: null,
      sessionId:   null,

      setCurrentUser: (user)      => set({ currentUser: user }),
      setSessionId:   (sessionId) => set({ sessionId }),
      logout:         ()          => set({ currentUser: null, sessionId: null }),
    }),
    { name: 'bird-user' }
  )
);
