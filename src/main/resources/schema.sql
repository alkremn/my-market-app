------------------------------------------------------------
-- SCHEMA: ITEMS TABLE
------------------------------------------------------------

CREATE TABLE IF NOT EXISTS items (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    img_path    VARCHAR(256),
    price       DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    count       INTEGER        NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

------------------------------------------------------------
-- INDEXES
------------------------------------------------------------

-- Index for case-insensitive title searches and sorting
CREATE INDEX IF NOT EXISTS idx_items_title_lower ON items(LOWER(title));

-- Index for price sorting
CREATE INDEX IF NOT EXISTS idx_items_price ON items(price);
