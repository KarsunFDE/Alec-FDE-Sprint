-- Release Radar schema (skeleton subset of the full 14-table ERD)

CREATE TABLE app_user (
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(80)  NOT NULL UNIQUE,
    email         VARCHAR(160) NOT NULL UNIQUE,
    password_hash VARCHAR(200) NOT NULL,
    date_joined   TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE TABLE tv_series (
    id    BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    genre VARCHAR(80)
);

CREATE TABLE media_item (
    id           BIGSERIAL PRIMARY KEY,
    title        VARCHAR(200) NOT NULL,
    release_date DATE,
    type         VARCHAR(40)  NOT NULL,
    genre        VARCHAR(80),
    description  TEXT
);

CREATE TABLE season (
    id            BIGINT PRIMARY KEY REFERENCES media_item(id),
    season_number INTEGER,
    num_episodes  INTEGER,
    series_id     BIGINT NOT NULL REFERENCES tv_series(id)
);

CREATE TABLE media_asset (
    id            BIGSERIAL PRIMARY KEY,
    url           VARCHAR(400) NOT NULL,
    type          VARCHAR(40)  NOT NULL,
    media_item_id BIGINT NOT NULL REFERENCES media_item(id)
);

CREATE TABLE tag (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(80) NOT NULL UNIQUE
);

CREATE TABLE media_tag (
    id            BIGSERIAL PRIMARY KEY,
    tag_id        BIGINT NOT NULL REFERENCES tag(id),
    media_item_id BIGINT NOT NULL REFERENCES media_item(id),
    CONSTRAINT uq_media_tag UNIQUE (tag_id, media_item_id)
);

CREATE TABLE track (
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT NOT NULL REFERENCES app_user(id),
    media_item_id BIGINT NOT NULL REFERENCES media_item(id),
    date_tracked  DATE NOT NULL DEFAULT CURRENT_DATE,
    is_notified   BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT uq_track UNIQUE (user_id, media_item_id)
);

CREATE INDEX idx_season_series      ON season(series_id);
CREATE INDEX idx_media_asset_item   ON media_asset(media_item_id);
CREATE INDEX idx_media_tag_tag      ON media_tag(tag_id);
CREATE INDEX idx_media_item_release ON media_item(release_date);
