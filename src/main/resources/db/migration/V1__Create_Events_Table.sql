CREATE TABLE ACCOUNT_EVENTS (
    id              UUID                DEFAULT uuid_generate_v4(),
    account_number   UUID                NOT NULL,
    event_type      VARCHAR(50)         NOT NULL,
    event_payload   JSONB               NOT NULL,

    PRIMARY KEY (id)
)
