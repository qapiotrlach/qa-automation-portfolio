-- Tabela produktow: podstawa katalogu w aplikacji demo.
CREATE TABLE products (
    id          BIGSERIAL      PRIMARY KEY,
    name        VARCHAR(120)   NOT NULL,
    description TEXT,
    price       NUMERIC(10, 2) NOT NULL CHECK (price >= 0),
    stock       INTEGER        NOT NULL DEFAULT 0 CHECK (stock >= 0),
    category    VARCHAR(60)    NOT NULL,
    active      BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ    NOT NULL DEFAULT now()
);

-- Indeks pod filtrowanie po kategorii (czeste zapytanie w katalogu).
CREATE INDEX idx_products_category ON products (category);
