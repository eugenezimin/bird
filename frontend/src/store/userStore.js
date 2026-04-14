/**
 * Global user/auth state.
 * Tracks the currently "logged-in" user and their active session.
 */
import { create } from 'zustand';

export const useUserStore = create((set) => ({
  currentUser:   null,
  sessionId:     null,
  setCurrentUser: (user)      => set({ currentUser: user }),
  setSessionId:   (sessionId) => set({ sessionId }),
  logout:         ()          => set({ currentUser: null, sessionId: null }),
}));
