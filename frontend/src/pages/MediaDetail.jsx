import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getMediaById } from '../api.js';

export default function MediaDetail() {
  const { id } = useParams();
  const [item, setItem] = useState(null);

  useEffect(() => {
    getMediaById(id).then((data) => setItem(data));
  }, [id]);

  if (!item) return <p>Loading...</p>;

  return (
    <div>
      <Link to="/">← Back</Link>
      <h2>{item.title}</h2>
      <p>{item.type} · {item.genre} · {item.releaseDate}</p>
      <p>{item.description}</p>

      <h3>Tags</h3>
      <div>{(item.tags || []).join(', ') || 'none'}</div>

      <h3>Assets</h3>
      {(item.assets || []).map((a, i) => (
        <div className="card" key={i}>
          {a.type}: <a href={a.url}>{a.url}</a>
        </div>
      ))}
    </div>
  );
}
