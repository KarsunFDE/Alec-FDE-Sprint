import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getMedia } from '../api.js';

export default function MediaList() {
  const [query, setQuery] = useState('');
  const [page, setPage] = useState(1);
  const [items, setItems] = useState([]);

  useEffect(() => {
    getMedia(query, page).then((data) => setItems(data));
  }, [page]);

  return (
    <div>
      <h2>Browse media</h2>
      <input
        className="field"
        placeholder="Search by title..."
        value={query}
        onChange={(e) => setQuery(e.target.value)}
      />

      <div style={{ margin: '0.5rem 0' }}>
        <button onClick={() => setPage((p) => Math.max(1, p - 1))}>Prev</button>
        <span style={{ margin: '0 0.75rem' }}>Page {page}</span>
        <button onClick={() => setPage((p) => p + 1)}>Next</button>
      </div>

      {items.map((m) => (
        <div className="card" key={m.id}>
          <Link to={`/media/${m.id}`}>
            <strong>{m.title}</strong>
          </Link>
          <div>{m.type} · {m.genre} · {m.releaseDate}</div>
          <div>{(m.tags || []).join(', ')}</div>
        </div>
      ))}

      {items.length === 0 && <p>No results.</p>}
    </div>
  );
}
