const BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080';

export function getMedia(q, page) {
  return fetch(`${BASE}/api/media?q=${encodeURIComponent(q)}&page=${page}`)
    .then((res) => res.json());
}

export function getMediaById(id) {
  return fetch(`${BASE}/api/media/${id}`).then((res) => res.json());
}

export function createMedia(body) {
  return fetch(`${BASE}/api/media`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  }).then((res) => res.json());
}
