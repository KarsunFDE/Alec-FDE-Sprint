-- Seed fixture data (hand-written, no scraper)

INSERT INTO app_user (username, email, password_hash) VALUES
    ('alec',  'alec@example.com',  '$2a$10$Dq7Yx8Q1a9kZk5b3fVn0OeJ9wJt2r8m1qk0aBcDeFgHiJkLmNoPq'),
    ('sam',   'sam@example.com',   '$2a$10$Ab1Cd2Ef3Gh4Ij5Kl6Mn7Oe9wJt2r8m1qk0aBcDeFgHiJkLmNoPq');

INSERT INTO tv_series (title, genre) VALUES
    ('The Expanse', 'Sci-Fi'),
    ('Severance',   'Thriller');

-- media_item ids 1..8
INSERT INTO media_item (title, release_date, type, genre, description) VALUES
    ('Hollow Knight: Silksong', '2025-09-04', 'game',  'Metroidvania', 'Sequel to Hollow Knight.'),
    ('Grand Theft Auto VI',     '2025-11-19', 'game',  'Action',       'Open-world crime epic.'),
    ('The Legend of Zelda',     '2026-03-01', 'game',  'Adventure',    'New Zelda entry.'),
    ('Dune: Part Three',        '2026-12-18', 'movie', 'Sci-Fi',       'Conclusion of the Dune saga.'),
    ('Avatar 3',                '2025-12-19', 'movie', 'Sci-Fi',       'Return to Pandora.'),
    ('The Expanse S7',          '2026-01-15', 'season','Sci-Fi',       'Seventh season.'),
    ('Severance S3',            '2026-02-20', 'season','Thriller',     'Third season.'),
    ('Silent Hill f',           '2025-09-25', 'game',  'Horror',       'New Silent Hill.');

INSERT INTO season (id, season_number, num_episodes, series_id) VALUES
    (6, 7, 10, 1),
    (7, 3, 9,  2);

INSERT INTO tag (name) VALUES
    ('indie'), ('open-world'), ('sequel'), ('sci-fi'), ('horror');

INSERT INTO media_tag (tag_id, media_item_id) VALUES
    (1,1),(3,1),
    (2,2),(3,2),
    (3,3),
    (4,4),(3,4),
    (4,5),(3,5),
    (4,6),(4,7),
    (5,8);

INSERT INTO media_asset (url, type, media_item_id) VALUES
    ('https://cdn.example.com/silksong/hero.jpg', 'image',   1),
    ('https://cdn.example.com/gta6/trailer.mp4',  'trailer', 2),
    ('https://cdn.example.com/dune3/poster.jpg',  'image',   4);

INSERT INTO track (user_id, media_item_id, date_tracked, is_notified) VALUES
    (1, 1, '2025-06-01', false),
    (1, 4, '2025-06-02', false),
    (2, 2, '2025-06-03', false);
