-- Lab 37 full schema — CUSTOMER, ACCOUNT, ADDRESS, CUSTOMER_STATUS_HISTORY

CREATE TABLE customer (
                          customer_id        BIGSERIAL,
                          public_id          VARCHAR(36) NOT NULL,
                          full_name          VARCHAR(150) NOT NULL,
                          email_normalized   VARCHAR(254) NOT NULL,
                          phone              VARCHAR(30),
                          status             VARCHAR(20) DEFAULT 'PROSPECT' NOT NULL,
                          version_no         INTEGER DEFAULT 0 NOT NULL,
                          created_at         TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
                          updated_at         TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
                          CONSTRAINT pk_customer PRIMARY KEY (customer_id),
                          CONSTRAINT uk_customer_public UNIQUE (public_id),
                          CONSTRAINT uk_customer_email UNIQUE (email_normalized),
                          CONSTRAINT ck_customer_status CHECK (
                              status IN ('PROSPECT', 'ACTIVE', 'SUSPENDED', 'CLOSED')
                              )
);

CREATE TABLE account (
                         account_id     BIGSERIAL,
                         account_number VARCHAR(34) NOT NULL,
                         customer_id    BIGINT NOT NULL,
                         account_type   VARCHAR(20) NOT NULL,
                         status         VARCHAR(20) DEFAULT 'OPEN' NOT NULL,
                         balance        NUMERIC(19, 2) DEFAULT 0 NOT NULL,
                         currency       CHAR(3) DEFAULT 'CAD' NOT NULL,
                         opened_at      TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
                         CONSTRAINT pk_account PRIMARY KEY (account_id),
                         CONSTRAINT uk_account_number UNIQUE (account_number),
                         CONSTRAINT fk_account_customer FOREIGN KEY (customer_id)
                             REFERENCES customer (customer_id),
                         CONSTRAINT ck_account_type CHECK (
                             account_type IN ('CHECKING', 'SAVINGS', 'CREDIT')
                             ),
                         CONSTRAINT ck_account_status CHECK (
                             status IN ('OPEN', 'CLOSED', 'FROZEN')
                             )
);

CREATE TABLE address (
                         address_id   BIGSERIAL,
                         customer_id  BIGINT NOT NULL,
                         address_type VARCHAR(20) NOT NULL,
                         line1        VARCHAR(100) NOT NULL,
                         line2        VARCHAR(100),
                         city         VARCHAR(80) NOT NULL,
                         region       VARCHAR(80),
                         postal_code  VARCHAR(20),
                         country_code CHAR(2) DEFAULT 'CA' NOT NULL,
                         CONSTRAINT pk_address PRIMARY KEY (address_id),
                         CONSTRAINT fk_address_customer FOREIGN KEY (customer_id)
                             REFERENCES customer (customer_id),
                         CONSTRAINT ck_address_type CHECK (
                             address_type IN ('HOME', 'WORK', 'BILLING', 'OTHER')
                             )
);

CREATE TABLE customer_status_history (
                                         history_id     BIGSERIAL,
                                         customer_id    BIGINT NOT NULL,
                                         old_status     VARCHAR(20),
                                         new_status     VARCHAR(20) NOT NULL,
                                         changed_by     VARCHAR(100) NOT NULL,
                                         reason         VARCHAR(200),
                                         correlation_id VARCHAR(64),
                                         changed_at     TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                         CONSTRAINT pk_cust_status_hist PRIMARY KEY (history_id),
                                         CONSTRAINT fk_hist_customer FOREIGN KEY (customer_id)
                                             REFERENCES customer (customer_id)
);

CREATE INDEX ix_account_customer ON account (customer_id);
CREATE INDEX ix_address_customer ON address (customer_id);
CREATE INDEX ix_history_customer_time ON customer_status_history (customer_id, changed_at);