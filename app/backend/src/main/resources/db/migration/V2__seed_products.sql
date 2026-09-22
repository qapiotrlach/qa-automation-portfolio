-- Dane startowe katalogu. Stale, zeby testy mogly na nich polegac.
INSERT INTO products (name, description, price, stock, category, active) VALUES
    ('Klawiatura mechaniczna K80', 'Przelaczniki brazowe, podswietlenie RGB', 349.00, 12, 'peryferia', TRUE),
    ('Mysz bezprzewodowa M12',     'Sensor 16000 DPI, 6 przyciskow',          159.99,  40, 'peryferia', TRUE),
    ('Monitor 27 cali QHD',        'Matryca IPS, 165 Hz, 1 ms',              1299.00,   7, 'monitory',  TRUE),
    ('Monitor 24 cale FHD',        'Matryca VA, 75 Hz',                       699.50,   0, 'monitory',  TRUE),
    ('Sluchawki nauszne H9',       'Redukcja szumow, 30 h pracy',             549.00,  15, 'audio',     TRUE),
    ('Mikrofon pojemnosciowy S1',  'USB-C, statyw w zestawie',                429.00,   3, 'audio',     TRUE),
    ('Podkladka XXL',              'Powierzchnia 900x400 mm',                  89.00, 120, 'akcesoria', TRUE),
    ('Hub USB-C 7w1',              'HDMI, 2x USB-A, czytnik kart',            219.00,  25, 'akcesoria', FALSE);
