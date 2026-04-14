import { BrowserRouter, Routes, Route } from 'react-router-dom';
import AppLayout    from './components/layout/AppLayout';
import HomePage     from './pages/HomePage';
import FeedPage     from './pages/FeedPage';
import ProfilePage  from './pages/ProfilePage';
import MessagesPage from './pages/MessagesPage';
import LoginPage    from './pages/LoginPage';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route element={<AppLayout />}>
          <Route index       element={<HomePage />}     />
          <Route path="feed" element={<FeedPage />}     />
          <Route path="profile/:userId" element={<ProfilePage />} />
          <Route path="messages"        element={<MessagesPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
