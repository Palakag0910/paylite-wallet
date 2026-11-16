CREATE TABLE payment_requests(
    id                             BIGSERIAL PRIMARY KEY,
    requester_user_id              BIGINT NOT NULL REFERENCES  users(id),
    payer_user_id                  BIGINT NOT NULL REFERENCES  users(id),
    amount                         BIGINT NOT NULL CHECK(amount > 0),
    note                           VARCHAR(100),
    status                         VARCHAR(10) NOT NULL DEFAULT 'PENDING'
           CHECK(status IN('PENDING','PAID','DECLINED','CANCELLED','EXPIRED')),
    transaction_id                  BIGINT REFERENCES transactions(id),
    expires_at                      TIMESTAMPTZ NOT NULL,
    created_at                      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT payment_requests_not_self CHECK(requester_user_id <> payer_user_id)
);

CREATE INDEX idx_pr_payer_status ON payment_requests(payer_user_id,status);
CREATE INDEX idx_pr_requester_status ON payment_requests(requester_user_id,status);
CREATE INDEX idx_pr_pending_expiry ON payment_requests(expires_at) WHERE status = 'PENDING';

CREATE TABLE audit_log(
    id                             BIGSERIAL PRIMARY KEY ,
    actor_user_id                  BIGINT NOT NULL REFERENCES users(id),
    action                         VARCHAR(50) NOT NULL,
    target_type                    VARCHAR(30) NOT NULL,
    target_id                      BIGINT,
    reason                         VARCHAR(255),
    request_id                     VARCHAR(64),
    created_at                     TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_audit_created ON audit_log(created_at DESC);
CREATE INDEX idx_audit_actor ON audit_log(actor_user_id,created_at DESC);

CREATE TABLE merchant_webhooks(
    id                            BIGSERIAL PRIMARY KEY ,
    merchant_id                   BIGINT NOT NULL UNIQUE REFERENCES merchants(id),
    url                           VARCHAR(500) NOT NULL,
    secret                        VARCHAR(100) NOT NULL,
    created_at                    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE webhooks_deliveries(
    id                              BIGSERIAL PRIMARY KEY ,
    merchant_id                     BIGINT NOT NULL REFERENCES merchants(id),
    event_type                      VARCHAR(30) NOT NULL ,
    transaction_id                  BIGINT NOT NULL REFERENCES transactions(id),
    payload                         TEXT NOT NULL ,
    status                          VARCHAR(10) NOT NULL DEFAULT 'PENDING'
          CHECK ( status IN ('PENDING','DELIVERED','FAILED') ),
    attempts                        INT NOT NULL DEFAULT 0,
    next_attempt_at                 TIMESTAMPTZ,
    last_response                   VARCHAR(255),
    created_at                      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_wd_due ON webhooks_deliveries(next_attempt_at) WHERE status = 'PENDING';

CREATE TABLE transaction_categories(
    id                                 BIGSERIAL PRIMARY KEY ,
    transaction_id                     BIGINT NOT NULL UNIQUE REFERENCES transactions(id),
    user_id                            BIGINT NOT NULL REFERENCES users(id),
    category                           VARCHAR(20) NOT NULL ,
            CHECK ( category IN ('FOOD','TRAVEL','SHOPPING','BILLS','ENTERTAINMENT','TRANSFER','OTHER','UNCATEGORIZED') ),
    source                             VARCHAR(10) NOT NULL DEFAULT 'AI'
            CHECK ( source IN ('AI','USER') ),
    model_name                         VARCHAR(50),
    prompt_version                     VARCHAR(20),
    raw_response                       TEXT,
    created_at                         TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_tc_user ON transaction_categories(user_id);

CREATE TABLE spending_insights(
    id                                 BIGSERIAL PRIMARY KEY ,
    user_id                            BIGINT NOT NULL REFERENCES users(id),
    month                              CHAR(7) NOT NULL,
    numbers                            TEXT NOT NULL ,
    summary                            TEXT NOT NULL ,
    model_name                         VARCHAR(50),
    created_at                         TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_insight_user_month UNIQUE (user_id, month)
);

INSERT INTO wallets(owner_user_id, wallet_type,balance,status)
VALUES (NULL,'SYSTEM_BANK',0,'ACTIVE'),
       (NULL,'SYSTEM_REVENUE',0,'ACTIVE');
