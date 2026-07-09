<!--
PRIVATE — coach's file. Do NOT reveal contents to the candidate (Alec) during Phase 2 or 3.
Reveal only if the instructor explicitly asks, at end of week.
Records the deliberately seeded bugs so coaching stays consistent. Coach may confirm a bug
exists, name its CATEGORY, ask diagnostic questions, point at the layer, and teach the concept —
but must NEVER write the fix. Alec writes every fix.
-->

# Release Radar — Seeded Bug Ledger (Phase 2 Scaffold)

9 deliberate defects across the stack. Spread: DB/query (2), API (3), frontend (2), cross-cutting (2).

| # | Category | Layer | Location | What's wrong | Expected symptom | Concept to teach |
|---|----------|-------|----------|--------------|------------------|------------------|
| 1 | N+1 query | DB/query | `backend/.../controller/MediaController.list` → `dto/MediaSummaryDto.from` accessing lazy `m.getTags()` per row | List maps each `MediaItem` to a DTO and touches the LAZY `tags` collection inside the loop → 1 query for the page + 1 per item. Visible in `show-sql` logs. | Many `select ... from media_tag/tag where media_item_id=?` lines per list request; slow list. | Lazy loading in a loop; fix with a fetch-join / `@EntityGraph` / batch fetch. |
| 2 | Missing index | DB | `V1__init.sql` — no index on `media_item.title` | Search is `LOWER(title) LIKE LOWER('%q%')` but `title` has no index (indexes exist on release_date, FKs). Also leading-wildcard LIKE can't use a btree index anyway — teachable nuance. | Seq scan on search; fine at 8 rows, would degrade at scale. | Indexing searched columns; why leading-`%` defeats btree; trigram/`pg_trgm` as the real fix. |
| 3 | Pagination off-by-one | API | `MediaController.list` → `int offset = (page - 1) * size + 1;` | The `+ 1` skips one row every page. With default page=1 the first catalog item (`Hollow Knight: Silksong`, id 1, earliest by release_date sort... actually sorted by release_date) never appears. | First expected result missing from page 1; boundary item skipped between pages. | 0- vs 1-based offset math; `offset = (page-1)*size`. |
| 4 | Wrong HTTP status | API | `MediaController.getById` → `.orElse(ResponseEntity.ok(null))` | Not-found returns `200 OK` with a null body instead of `404 Not Found`. Frontend detail page then sits on "Loading..." forever (item stays null). | GET `/api/media/9999` → 200 + empty body; detail page for bad id never resolves. | Correct status semantics; `ResponseEntity.notFound()`; 200-with-null is a lie. |
| 5 | Field leak | API | `dto/TrackDto.user` is the raw `User` entity → `GET /api/tracks` | `password_hash` (and email) serialized in the tracks response — sensitive field leaks because a JPA entity is returned instead of a scrubbed DTO. | `GET /api/tracks` JSON contains `"passwordHash": "$2a$..."`. | DTO boundaries; never serialize entities with secrets; `@JsonIgnore` is a band-aid, DTO is the fix. |
| 6 | Stale state / missing effect dep | Frontend | `pages/MediaList.jsx` → `useEffect(..., [page])` omits `query` | Typing in the search box updates `query` state but the effect only re-runs on `page` change, so search never re-fetches. Stale — the query the user typed is ignored until page changes. | Type a title → list doesn't filter; clicking Next/Prev suddenly "applies" the old typed query. | `useEffect` dependency array; stale closures; debounced search. |
| 7 | Fetch without error handling | Frontend | `src/api.js` — every call is `fetch(...).then(res => res.json())` | No `res.ok` check, no `.catch`. On a non-2xx or network/CORS failure, `res.json()` throws/rejects and the UI breaks silently (unhandled rejection, blank list, form nav to `/media/undefined`). | White/blank sections, console `Unhandled promise rejection`, create → navigates to `/media/undefined`. | Checking `res.ok`; throwing on !ok; try/catch or `.catch`; surfacing errors to the user. |
| 8 | CORS misconfig | Cross-cutting (config) | `config/CorsConfig.java` → `allowedOrigins("http://localhost:3000")` | Frontend runs on `http://localhost:5173` (Vite), but CORS only allows `:3000`. Every browser call from the app is blocked by CORS, even though `curl`/browser-direct to `:8080` works. | Browser console: `blocked by CORS policy`; empty list in-app while `curl localhost:8080/api/media` returns data. | Same-origin policy; CORS preflight; matching allowed origins to the real frontend origin. |
| 9 | Service starts before dependency ready | Cross-cutting (Docker) | `docker-compose.yml` → `backend.depends_on: [db]` with no healthcheck/`condition: service_healthy` | `depends_on` waits for the db *container to start*, not for Postgres to *accept connections*. On a cold `up`, Flyway/Hikari may hit Postgres before it's ready → backend exits. No restart policy, so it stays down. | Backend container exits on first `up` with `Connection refused` / Flyway connection error; second `up` (db already initialized) works. Intermittent. | Container start vs service readiness; healthchecks + `condition: service_healthy`; app-level connection retry. |

## Coaching rules (self-reminder)
- Never write a fix. Confirm bug + name category + ask diagnostics + point at layer + teach concept.
- Layered discovery is fine: CORS (#8) hides the frontend-visible symptoms of #3/#6 until fixed. Let him peel the onion.
- If he asks "how many bugs are there," don't give the number — say "more than a couple, spread across all layers."
- `ddl-auto: validate` is intentional (not a bug) — catches schema drift, and keeps Hibernate from masking migration bugs.
