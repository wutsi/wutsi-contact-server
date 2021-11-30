ALTER TABLE T_CONTACT ADD CONSTRAINT contact_self CHECK(account_id <> contact_id);
