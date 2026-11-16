CREATE TABLE transactions(
    id                          BIGSERIAL PRIMARY KEY,
    type                        VARCHAR(20) NOT NULL
           CHECK(type IN('TOPUP','P2P','MERCHANT_PAYMENT','REFUND','REQUEST_PAYMENT','REVERSAL','FEE')),
    status                      VARCHAR(10) NOT NULL
           CHECK ( status IN('PENDING','SUCCESS','FAILED','REVERSED') ),
    amount                      BIGINT NOT NULL CHECK(amount > 0),
    from_wallet_id              BIGINT REFERENCES wallets(id),
    to_wallet_id                BIGINT REFERENCES wallets(id),
    note                        VARCHAR(100),
    order_reference             VARCHAR(100),
    failure_reason              VARCHAR(100),
    original_txn_id             BIGINT REFERENCES transactions(id),
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_txn_from_created ON transactions (from_wallet_id, created_at DESC);
CREATE INDEX idx_txn_to_created ON transactions (to_wallet_id,created_at DESC);
CREATE INDEX idx_txn_original ON transactions (original_txn_id);

CREATE TABLE ledger_entries(
    id                         BIGSERIAL PRIMARY KEY,
    transaction_id             BIGINT NOT NULL REFERENCES transactions(id),
    wallet_id                  BIGINT NOT NULL REFERENCES wallets(id),
    amount                     BIGINT NOT NULL CHECK( amount <> 0),
    balance_after              BIGINT NOT NULL,
    created_at                 TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_ledger_wallet_created ON ledger_entries(wallet_id,created_at DESC);
CREATE INDEX idx_ledger_txn ON ledger_entries(transaction_id);

CREATE TABLE idempotency_keys(
    id                         BIGSERIAL PRIMARY KEY ,
    user_id                    BIGINT NOT NULL REFERENCES users(id),
    idem_key                   VARCHAR(100) NOT NULL,
    request_hash               VARCHAR(64) NOT NULL,
    response_status            INT,
    response_body              TEXT,
    created_at                 TIMESTAMPTZ NOT NULL DEFAULT now(),
    expires_at                 TIMESTAMPTZ NOT NULL,
    CONSTRAINT uq_idem_user_key UNIQUE(user_id,idem_key)
);

CREATE OR REPLACE FUNCTION forbid_ledger_change() RETURNS trigger AS $$
BEGIN
    RAISE EXCEPTION 'ledger_entries is insert-only';
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_ledger_no_update
    BEFORE UPDATE OR DELETE ON ledger_entries
    FOR EACH ROW EXECUTE FUNCTION forbid_ledger_change();
