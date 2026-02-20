------------------------------------------------------------
-- SCHEMA: balances table
------------------------------------------------------------

CREATE TABLE IF NOT EXISTS balances (
    user_id         BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    balance         DECIMAL(10, 2) NOT NULL DEFAULT 0.0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);