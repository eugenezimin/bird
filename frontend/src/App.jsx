import { BrowserRouter, Routes, Route } from 'react-router-dom';
import AppLayout      from './components/layout/AppLayout';
import UserSelectPage from './pages/UserSelectPage';
import FeedPage       from './pages/FeedPage';
import ExplorePage    from './pages/ExplorePage';
import ProfilePage    from './pages/ProfilePage';
import MessagesPage   from './pages/MessagesPage';
import ComposePage    from './pages/ComposePage';
export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* User picker — no sidebar */}
        <Route path="/" element={<UserSelectPage />} />

        {/* App shell — requires currentUser, redirects to / if not set */}
        <Route element={<AppLayout />}>
          <Route path="feed"            element={<FeedPage />} />
          <Route path="explore"         element={<ExplorePage />} />
          <Route path="profile/:userId" element={<ProfilePage />} />
          <Route path="messages"        element={<MessagesPage />} />
          <Route path="compose"         element={<ComposePage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
