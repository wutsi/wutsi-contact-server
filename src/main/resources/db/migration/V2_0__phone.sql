CREATE TABLE T_PHONE(
    id            SERIAL NOT NULL,
    account_id    BIGINT NOT NULL,
    tenant_id     BIGINT NOT NULL,
    number         VARCHAR(30) NOT NULL,
    created       TIMESTAMPTZ NOT NULL DEFAULT now(),

    UNIQUE(account_id, tenant_id, number),
    PRIMARY KEY (id)
);
