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
