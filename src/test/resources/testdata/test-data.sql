-- PokeScan test dataset (SQLite) -------------------------------------------
-- Designed to work with Hibernate ddl-auto=create-drop.
-- Add missing physical column 'series_name' on table 'sets' for native queries.

-- Ensure clean state
DELETE FROM cards;
DELETE FROM sets;
DELETE FROM series;

-- Add extra column required by native queries (no-op if already exists)
-- SQLite supports IF NOT EXISTS for ADD COLUMN since 3.35; if your local
-- SQLite is older, comment this out after first run.
ALTER TABLE sets ADD COLUMN series_name TEXT;

-- Seed series ---------------------------------------------------------------
INSERT INTO series (series_id, name, release_year, lang) VALUES
                                                             ('base-en', 'Base', 1999, 'en'),
                                                             ('neo-en',  'Neo',  2000, 'en');

-- Seed sets -----------------------------------------------------------------
-- Note: 'id' is the localized set id (set + "-" + lang), e.g. "base1-en"
--       'series_id' links to series(series_id)
INSERT INTO sets (id, name, release_date, series_id, series_name) VALUES
                                                                      ('base1-en', 'Base Set',       '1999-01-09', 'base-en', 'Base'),
                                                                      ('base2-en', 'Jungle',         '1999-06-16', 'base-en', 'Base'),
                                                                      ('neo1-en',  'Neo Genesis',    '2000-12-16', 'neo-en',  'Neo');

-- Seed cards ----------------------------------------------------------------
-- Minimal fields used by the application + JSON blobs used by mapping
-- variants_json keys: normal,holo,reverse,firstEdition,wPromo
-- set_info_json keys: name, cardCount{official,total}
INSERT INTO cards (
    card_id, category, illustrator, local_id, name, rarity,
    variants_json, dex_id, type, language,
    set_info_json, set_id, set_loc_id
) VALUES
      -- Base Set (base1-en)
      ('base1-1-en', 1, 'Ken Sugimori', 1,  'Alakazam',  'Rare Holo',
       '{"normal":false,"holo":true,"reverse":false,"firstEdition":false,"wPromo":false}',
       65, 'Psychic', 'en',
       '{"name":"Base Set","cardCount":{"official":102,"total":102}}',
       'base1', 'base1-en'),
      ('base1-2-en', 0, 'Mitsuhiro Arita', 2, 'Blastoise', 'Rare Holo',
       '{"normal":false,"holo":true,"reverse":false,"firstEdition":false,"wPromo":false}',
       9, 'Water', 'en',
       '{"name":"Base Set","cardCount":{"official":102,"total":102}}',
       'base1', 'base1-en'),
      ('base1-45-en', 0, 'Keiji Kinebuchi', 45, 'Magnemite', 'Common',
       '{"normal":true,"holo":false,"reverse":false,"firstEdition":false,"wPromo":false}',
       81, 'Metal', 'en',
       '{"name":"Base Set","cardCount":{"official":102,"total":102}}',
       'base1', 'base1-en'),

      -- Jungle (base2-en)
      ('base2-1-en', 1, 'Ken Sugimori', 1, 'Clefable', 'Rare Holo',
       '{"normal":false,"holo":true,"reverse":false,"firstEdition":false,"wPromo":false}',
       36, 'Colorless', 'en',
       '{"name":"Jungle","cardCount":{"official":64,"total":64}}',
       'base2', 'base2-en'),
      ('base2-15-en', 0, 'Mitsuhiro Arita', 15, 'Scyther', 'Rare Holo',
       '{"normal":false,"holo":true,"reverse":false,"firstEdition":false,"wPromo":false}',
       123, 'Grass', 'en',
       '{"name":"Jungle","cardCount":{"official":64,"total":64}}',
       'base2', 'base2-en'),
      ('base2-48-en', 0, 'Atsuko Nishida', 48, 'Meowth', 'Common',
       '{"normal":true,"holo":false,"reverse":false,"firstEdition":false,"wPromo":false}',
       52, 'Colorless', 'en',
       '{"name":"Jungle","cardCount":{"official":64,"total":64}}',
       'base2', 'base2-en'),

      -- Neo Genesis (neo1-en)
      ('neo1-1-en', 1, 'Ken Sugimori', 1, 'Ampharos', 'Rare Holo',
       '{"normal":false,"holo":true,"reverse":false,"firstEdition":false,"wPromo":false}',
       181, 'Lightning', 'en',
       '{"name":"Neo Genesis","cardCount":{"official":111,"total":111}}',
       'neo1', 'neo1-en'),
      ('neo1-17-en', 0, 'Mitsuhiro Arita', 17, 'Togetic', 'Rare',
       '{"normal":true,"holo":false,"reverse":false,"firstEdition":false,"wPromo":false}',
       176, 'Colorless', 'en',
       '{"name":"Neo Genesis","cardCount":{"official":111,"total":111}}',
       'neo1', 'neo1-en'),
      ('neo1-60-en', 0, 'Kagemaru Himeno', 60, 'Cyndaquil', 'Common',
       '{"normal":true,"holo":false,"reverse":false,"firstEdition":false,"wPromo":false}',
       155, 'Fire', 'en',
       '{"name":"Neo Genesis","cardCount":{"official":111,"total":111}}',
       'neo1', 'neo1-en');
