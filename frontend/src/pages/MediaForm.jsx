import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createMedia } from '../api.js';

export default function MediaForm() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    title: '',
    type: 'game',
    genre: '',
    releaseDate: '',
    description: '',
  });

  function update(field, value) {
    setForm((f) => ({ ...f, [field]: value }));
  }

  function submit(e) {
    e.preventDefault();
    createMedia(form).then((saved) => {
      navigate(`/media/${saved.id}`);
    });
  }

  return (
    <form onSubmit={submit}>
      <h2>Add media</h2>

      <label>Title</label>
      <input className="field" value={form.title}
             onChange={(e) => update('title', e.target.value)} />

      <label>Type</label>
      <select className="field" value={form.type}
              onChange={(e) => update('type', e.target.value)}>
        <option value="game">game</option>
        <option value="movie">movie</option>
        <option value="season">season</option>
      </select>

      <label>Genre</label>
      <input className="field" value={form.genre}
             onChange={(e) => update('genre', e.target.value)} />

      <label>Release date</label>
      <input className="field" type="date" value={form.releaseDate}
             onChange={(e) => update('releaseDate', e.target.value)} />

      <label>Description</label>
      <textarea className="field" rows="4" value={form.description}
                onChange={(e) => update('description', e.target.value)} />

      <div>
        <button type="submit">Create</button>
      </div>
    </form>
  );
}
