-- Minimal, deterministic dataset for tests
DELETE FROM card;

INSERT INTO card (cardID, localID, lang, name, illustrator, rarity, category) VALUES
                                                                                  ('bw6-1', 1, 'en', 'Snivy', 'Kagemaru Himeno', 'Common',   0),
                                                                                  ('bw6-2', 2, 'en', 'Hoppip', 'Kouki Saitou',   'Common',   0),
                                                                                  ('bw6-3', 3, 'en', 'Oddish', 'Kanako Eo',      'Common',   0),
                                                                                  ('bw6-4', 4, 'en', 'Dratini','Masakazu Fukuda','Rare',     1),
                                                                                  ('sm6-1', 1, 'en', 'Exeggcute','Yuka Morii',   'Rare',     1),
                                                                                  ('sm6-2', 2, 'en', 'Bulbasaur','Mizue',        'Uncommon', 0),
                                                                                  ('sm6-3', 3, 'en', 'Caterpie','Sekio',         'Common',   0),
                                                                                  ('swsh1-1',1,'en','Rowlet','Mitsuhiro Arita',  'Rare',     1),
                                                                                  ('swsh1-2',2,'en','Weedle','Ken Sugimori',     'Common',   0),
                                                                                  ('swsh1-3',3,'en','Growlithe','Masako Yamashita','Uncommon',0);
