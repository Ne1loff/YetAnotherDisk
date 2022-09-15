CREATE SEQUENCE IF NOT EXISTS history_sequence START WITH 1 INCREMENT BY 20;

CREATE TABLE IF NOT EXISTS system_item
(
    id        VARCHAR(255)             NOT NULL,
    type      INTEGER                  NOT NULL,
    date      TIMESTAMP with time zone NOT NULL,
    parent_id VARCHAR(255),
    url       VARCHAR(255),
    size      BIGINT                   NOT NULL,
    CONSTRAINT pk_system_item PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS system_item_history
(
    id         BIGINT                   NOT NULL,
    type       INTEGER                  NOT NULL,
    date       TIMESTAMP with time zone NOT NULL,
    item_id    VARCHAR(255)             NOT NULL,
    is_deleted BOOLEAN                  NOT NULL,
    url        VARCHAR(255),
    parent_id  VARCHAR(255),
    size       BIGINT,
    CONSTRAINT pk_system_item_history PRIMARY KEY (id)
);
