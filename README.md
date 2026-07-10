# Release Radar

A full-stack web app for tracking upcoming release dates of **games, movies, and TV seasons**.
A user searches the catalog, adds items to their personal tracks, and gets notified on the day
an item releases.

This repository is built during a one-week Revature FDE full-stack SWE sprint. It is a
**fundamentals** project — hand-written and defended, not AI-generated. Claude acts as a coach
(guides, questions, reviews) and never writes the solution code.

## What it does

- **Accounts** — register / log in (passwords stored hashed, never plaintext).
- **Search** — find media by release window ("what releases in July?") or find a TV series by
  name and drill into its seasons.
- **Track** — add any media item to your tracks; each track records when you added it and whether
  you've been notified.
- **Notify** — on an item's release date, notify the users tracking it (background/scheduled job).
- **Browse** — each media item has a detail page: description, images/trailers, and clickable
  tags that link to every other item sharing that tag.

## Data model

14 tables. Full schema in [`docs/erd.dbml`](docs/erd.dbml) (diagram: [`docs/erd.png`](docs/erd.png)).

Key design decisions:

- **`media_item` parent (class-table inheritance)** — `game`, `movie`, and `season` share a
  parent holding common columns (`title`, `release_date`, `genre`, `description`) and each child
  holds only type-specific columns. This gives every downstream link (tracks, tags, companies,
  assets) **one** table to reference with real referential integrity.
- **`tv_series` is not a media_item** — a series has no single release date; its *seasons* do. The
  series is the searchable grouping (`series 1 → many seasons`); the season is the dated,
  trackable unit.
- **`company` is one flat table** — a "developer" / "publisher" / "studio" is a role a company
  plays, not a different kind of company. The role lives on the `media_company` join, so one
  company (e.g. Nintendo) can develop one title and publish another.
- **Many-to-many via join tables** — `media_company` (media ↔ company + role),
  `game_platform` (game ↔ platform), `media_tag` (media ↔ tag), and `track` (user ↔ media,
  with its own attributes). Each has a unique constraint on its natural key.
- **Single source of truth** — facts about a thing live on that thing. `release_date` is on
  `media_item`, never copied onto `track`, so a date change updates one row and every tracker
  sees it.

## Stack

- **Backend:** Java + Spring Boot, REST API
- **Database:** PostgreSQL
- **Frontend:** built in two stages — (1) plain JS/HTML/CSS + `fetch` to prove the fundamentals,
  then (2) React
- **Notification service (stretch):** Python + FastAPI, as a *separate* service doing a distinct
  job (scheduled release-day email) — **not** a second framework re-serving Spring's endpoints.
  Polyglot microservice pattern; also the FastAPI reps the job interview tests.
- **Scraper service (extremely-unlikely stretch):** a second Python/FastAPI service that ingests
  release data from external APIs into Postgres. Only if the whole spine + notification service
  are done with time to spare.
- **Stretch (ranked):** FastAPI notification service; auth; tracking; media detail page;
  *(far tail)* FastAPI scraper service.

### On rate limiting

Rate limiting is a **feature**, not a service — it lives where the traffic is, not in a box of
its own.

- **Outbound (scraper being polite):** throttle the scraper's own calls to external APIs so we
  respect their limits and 429s (e.g. `httpx` + a token bucket / `asyncio.Semaphore`, or
  `slowapi`). This is the good, authentic place to *demonstrate* rate limiting for the interview.
  If we build the scraper and this turns out to be low-effort, add it there. Optional-within-a-
  far-tail-stretch — nice-to-have, never a blocker.
- **Inbound (protect our own API):** would guard the *Spring* user-facing endpoints, so it
  belongs in Spring (a filter) or a gateway in front of it — **not** in a FastAPI service.
  Standing up a FastAPI gateway just to throttle Spring is overkill for a solo app and edges
  toward the pass-through anti-pattern. Out of scope.

## Goals

1. Design a clean relational schema and defend every modeling decision.
2. Build and defend a Spring Boot + Postgres REST API.
3. Close the primary skill gap: a real **web** frontend — plain JS first, then React.
4. Practice real debugging against a seeded-bug scaffold.
5. Produce a chat transcript that proves understanding, not just a working repo.

## Repository layout

- `docs/` — sprint guide, prompts (phases 0–4), ERD, candidate reports
- `docs/erd.dbml`, `docs/erd.png` — the data model
- `backend/` — Spring Boot 3 REST API (Java 17, JPA, Flyway migrations)
- `frontend/` — React + Vite web app (list / detail / form views)
- `docker-compose.yml` — Postgres + backend + frontend, full stack in one command

## Running the stack locally

Prerequisites: Docker Desktop (with Docker Compose). Nothing else needs to be installed.

```bash
# from the repo root
cp .env.example .env      # or keep the committed .env for local dev
docker compose up --build
```

This starts three containers:

| Service  | URL                         | Notes                                  |
|----------|-----------------------------|----------------------------------------|
| Postgres | `localhost:5432`            | db `release_radar`, user `radar`       |
| Backend  | `http://localhost:8080`     | REST API under `/api`                  |
| Frontend | `http://localhost:5173`     | React app                              |

Flyway applies the schema (`V1__init.sql`) and seed data (`V2__seed.sql`) automatically on
backend startup.

Quick smoke checks:

```bash
# API — list media
curl "http://localhost:8080/api/media"

# API — one item
curl "http://localhost:8080/api/media/1"
```

Then open the frontend at **http://localhost:5173**.

To stop: `Ctrl+C`, then `docker compose down` (add `-v` to also wipe the database volume).

### API surface (skeleton)

| Method | Path                | Purpose                          |
|--------|---------------------|----------------------------------|
| GET    | `/api/media?q=&page=&size=` | search / list (walking skeleton) |
| GET    | `/api/media/{id}`   | media detail                     |
| POST   | `/api/media`        | create                           |
| PUT    | `/api/media/{id}`   | update                           |
| DELETE | `/api/media/{id}`   | delete                           |
| GET    | `/api/tracks`       | list tracks                      |

## Status & next steps

- [x] **Phase 1 — Explore:** *done.* Data model designed and defended (ERD committed);
      [`SCOPE.md`](SCOPE.md) written (brief, entities, walking skeleton, ranked stretch).
  - [x] Walking skeleton defined: search query → `GET /api/media?q=` → Postgres → list renders.
  - [x] `SCOPE.md` written — Phase 2 reads this file.
  - [ ] Get scope signed off by instructor.
- [x] **Phase 2 — Scaffold:** *done.* Full stack stands up with one `docker compose up --build`.
  - [x] Postgres in Docker with real Flyway migrations (`V1__init.sql`) + seed (`V2__seed.sql`) —
        8 tables (subset of the 14-table ERD): 1→M (`tv_series→season`, `media_item→media_asset`)
        and M→M (`media_tag`, `track`).
  - [x] Spring Boot 3 REST API (Java 17, JPA): `/api/media` search + CRUD (walking skeleton
        `GET /api/media?q=`), `/api/tracks`.
  - [x] React + Vite frontend: three routed views — list (search) → detail → create form, with
        real state + `fetch`.
  - [x] Wiring: CORS config, `.env` files, README run steps.
  - [x] Verified reachable: `curl localhost:8080/api/media` returns data; frontend loads at
        `localhost:5173`.
  - [x] Intentional bugs seeded across every layer (DB, API, frontend, config) for Phase 3 —
        deliberately not documented here.
- [ ] **Phase 3 — Build:** hunt and fix the seeded bugs (candidate writes every fix), then build
      features per the ranked stretch (accounts → track → detail/tags → notifications). Frontend
      progression: plain JS/`fetch` first, then React.
- [ ] **Phase 4 — Verify:** explain-to-commit review gate before each commit and end of each day.

### Immediate next step

Start Phase 3 bug hunt. Drive the running app with devtools **Console** + **Network** open,
compare `curl` output to the seed data, and watch `docker compose logs backend` (SQL is logged).
Report observations by category — the coach confirms and teaches; the candidate writes the fix.
