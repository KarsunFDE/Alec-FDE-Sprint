# SCOPE — Release Radar

Phase-1 (Explore) output. Phase 2 reads this to scaffold.

## Product brief

Release Radar is a full-stack web app for tracking upcoming release dates of games, movies, and
TV seasons. A user searches a catalog of media, opens an item's detail page, and adds it to their
personal tracks; on the day an item releases, the app emails the users tracking it. It's a
fundamentals project — Java/Spring Boot REST API, PostgreSQL, and a web frontend built plain-JS
first then React — with no AI in the core.

## Core entities + relationships

Full schema in [`docs/erd.dbml`](docs/erd.dbml). 14 tables.

- **`user`** — accounts; `password_hash`, never plaintext. Has many `track`.
- **`media_item`** — parent table (class-table inheritance) for anything dated and trackable.
  Common columns: `title`, `release_date`, `type`, `genre`, `description`.
  - **`game` / `movie` / `season`** — children; share the parent's PK (is-a, 1:1). Each holds
    only type-specific columns (`game`: num_players, online/offline flags; `movie`: runtime;
    `season`: season_number, num_episodes, series_id).
- **`tv_series`** — searchable grouping; NOT a media_item, has no release date. One series has
  many seasons (1→M); the season is the dated, trackable unit.
- **`company`** — one flat table. Role (developer/publisher/studio) is not stored here.
- **`platform`** — lookup (PS5, Xbox Series X, …).
- **`tag`** — lookup; a tag exists once and is shared.
- **`media_asset`** — images/trailers; many per media_item (M→1 back to media_item).

Relationships:

| Relationship | Cardinality | Mechanism |
|---|---|---|
| user ↔ media_item | many-to-many | `track` join (with attributes: date_tracked, is_notified) |
| media_item → game/movie/season | one-to-one | shared primary key (inheritance) |
| tv_series → season | one-to-many | FK `season.series_id` |
| media_item ↔ company | many-to-many | `media_company` join (+ `role`) |
| game ↔ platform | many-to-many | `game_platform` join |
| media_item ↔ tag | many-to-many | `media_tag` join |
| media_item → media_asset | one-to-many | FK `media_asset.media_item_id` |

Design principles held: single source of truth (release_date lives only on media_item);
many-to-many always via a join table with a unique constraint on its natural key; role lives on
the relationship (join), not the entity.

## Walking skeleton

The one thinnest end-to-end flow that proves the stack is wired, built first, before any feature:

> **User types a search query in the web UI → `GET /api/media?q=<query>` → Spring queries
> Postgres → JSON list of media items renders on screen.**

Touches frontend → REST API → database → frontend. Depends on nothing else (no auth, no
tracking, no notifications). Everything else hangs off this proven pipe.

## Ranked stretch (deliberately NOT first)

In priority order — pull in only after the skeleton and core search/list work:

1. **FastAPI notification service** — a Python/FastAPI worker that owns a *distinct* job:
   scheduled email on release day. It reads `track` rows where `release_date == today` and
   `is_notified == false`, emails those users, and flips the flag. It does **not** re-serve
   Spring's endpoints — it's a separate service meeting Spring only at Postgres (or via an
   internal call). This is the legitimate polyglot pattern (see rationale below) and gives real
   FastAPI reps, which the job interview specifically tests. Top stretch for that reason.
2. **Accounts** — register / log in (BCrypt hash, simplest session). Brutally scoped: no
   "remember me", no email verification. Enables tracking. Adds forms — real frontend reps.
3. **Track** — add/remove a media_item to a user's tracks; view "my tracks".
4. **Media detail page** — description, assets (images/trailers), clickable tags → tag page
   listing all media sharing that tag.
5. **(far tail) FastAPI scraper service** — a second Python/FastAPI service ingesting release
   data from external APIs into Postgres. Only if the spine + notification service are done with
   time to spare. If built, add **outbound rate limiting** (throttle our calls to external APIs,
   respect their 429s — `httpx` + token bucket / `asyncio.Semaphore` or `slowapi`) *if* it's
   low-effort; it's the authentic place to demonstrate rate limiting for the interview.

The catalog is seeded with hand-written fixture data — the scraper is a far-tail stretch, not
core.

### Rate limiting — a feature, not a service

- **Outbound** (scraper being polite to external APIs) → lives *inside* the scraper. See stretch
  #5. Good, authentic demo; optional even within that far-tail stretch.
- **Inbound** (protect our own API) → guards the *Spring* user-facing endpoints, so it belongs in
  Spring (a filter) or a gateway in front of it — **not** a FastAPI service. A FastAPI gateway
  just to throttle Spring is overkill for a solo app and edges toward the pass-through
  anti-pattern. Out of scope.

## Polyglot rule — where FastAPI is (and isn't) allowed

FastAPI is included **only** for a job Spring shouldn't own. The rule, drawn from a reference
microservices repo (`contract-payment-flow`: Spring gateway + Spring domain services +
FastAPI `ai-orchestrator`, routed by path):

- ✅ **Allowed:** FastAPI as a *separate service* with its own responsibility (notifications,
  scraping/ingestion, an AI/RAG slice). Services meet at the database or through an internal
  HTTP call. Each language does what it's best at.
- ❌ **Not allowed:** FastAPI serving the *same* `/api/media` endpoints Spring already serves.
  Two web frameworks for one API = duplicated routing/logic, two runtimes, zero capability
  gained. An interviewer would flag it.

**Sequencing guard:** Spring + Postgres + React spine ships first. FastAPI is stretch, added only
once the spine works. The week's primary gap is the **web frontend** — the FastAPI service must
not steal frontend time. Adding it shifts the sprint's emphasis (frontend-only → frontend + a
real FastAPI service); flag this to the instructor.

## Next step

Get this scope signed off by instructor, then run Phase 2 (Scaffold).
