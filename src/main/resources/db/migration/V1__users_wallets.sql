-- USERS: everyone who can log in
CREATE TABLE users (
    id                  BIGSERIAL PRIMARY KEY ,
    name                VARCHAR(100) NOT NULL,
    email               VARCHAR(255) NOT NULL UNIQUE,
    phone               VARCHAR(10) NOT NULL UNIQUE,
    password_hash       VARCHAR(100) NOT NULL,
    role                VARCHAR(20) NOT NULL
        CHECK (role IN ('CUSTOMER','MERCHANT','SUPPORT','ADMIN')),
    pin_hash            VARCHAR(100),
    pin_failed_attempts  INT     NOT NULL DEFAULT 0,
    pin_locked_until    TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT users_phone_ten_digits CHECK (phone ~ '^[0-9]{10}$')
);

--REFRESH TOKENS: renewal tickets, stored only as a hash
CREATE TABLE refresh_tokens (
                                id          BIGSERIAL PRIMARY KEY,
                                user_id     BIGINT      NOT NULL REFERENCES users (id),
                                token_hash  VARCHAR(64) NOT NULL UNIQUE,
                                expires_at  TIMESTAMPTZ NOT NULL,
                                revoked_at  TIMESTAMPTZ,
                                created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);

--MERCHANTS: extra details for shop accounts
CREATE TABLE merchants (
    id                     BIGSERIAL PRIMARY KEY,
    user_id                BIGINT NOT NULL UNIQUE REFERENCES users(id),
    business_name          VARCHAR(150) NOT NULL,
    category               VARCHAR(30) NOT NULL
                       CHECK (category IN('FOOD','TRAVEL','SHOPPING','BILLS','ENTERTAINMENT','HEALTH','EDUCATION','OTHER')),
    status                  VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                       CHECK (status IN('PENDING','APPROVED','REJECTED')),
    rejection_reason        VARCHAR(255),
    created_at               TIMESTAMPTZ NOT NULL DEFAULT now()
);

--WALLETS: where the balance lives(money is in PAISE, a whole number)
CREATE TABLE wallets(
    id                      BIGSERIAL PRIMARY KEY ,
    owner_user_id           BIGINT UNIQUE REFERENCES users(id),
    wallet_type             VARCHAR(20) NOT NULL
               CHECK(wallet_type IN ('CUSTOMER','MERCHANT','SYSTEM_BANK','SYSTEM_REVENUE')),
    balance                 BIGINT NOT NULL DEFAULT 0,
    status                  VARCHAR(10) NOT NULL DEFAULT 'ACTIVE'
               CHECK (status IN('ACTIVE','FROZEN','CLOSED')),
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT wallets_balance_not_negative
                    CHECK(balance >= 0 OR wallet_type = 'SYSTEM_BANK')
);