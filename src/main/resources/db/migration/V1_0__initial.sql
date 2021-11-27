CREATE TABLE T_CONTACT(
    id            SERIAL NOT NULL,
    account_id    BIGINT NOT NULL,
    contact_id    BIGINT NOT NULL,
    tenant_id     BIGINT NOT NULL,
    created       TIMESTAMPTZ NOT NULL DEFAULT now(),

    UNIQUE(account_id, contact_id, tenant_id),
    PRIMARY KEY (id)
);
