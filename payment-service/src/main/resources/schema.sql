------------------------------------------------------------
-- SCHEMA: balance table
------------------------------------------------------------

CREATE TABLE IF NOT EXISTS balance (
    user_id         UUID PRIMARY KEY,
    balance         DECIMAL(10, 2) NOT NULL DEFAULT 0.0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);