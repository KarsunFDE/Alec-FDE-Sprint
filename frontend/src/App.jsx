import { Routes, Route, Link } from 'react-router-dom';
import MediaList from './pages/MediaList.jsx';
import MediaDetail from './pages/MediaDetail.jsx';
import MediaForm from './pages/MediaForm.jsx';

export default function App() {
  return (
    <div className="container">
      <h1>Release Radar</h1>
      <nav>
        <Link to="/">Browse</Link>
        <Link to="/new">Add media</Link>
      </nav>
      <hr />
      <Routes>
        <Route path="/" element={<MediaList />} />
        <Route path="/media/:id" element={<MediaDetail />} />
        <Route path="/new" element={<MediaForm />} />
      </Routes>
    </div>
  );
}
