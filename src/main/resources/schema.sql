DROP ALL OBJECTS;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name  VARCHAR(50)         NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS request
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description  VARCHAR(200)                NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    requester_id BIGINT REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL,
    description VARCHAR(200) NOT NULL,
    available   BOOLEAN DEFAULT FALSE,
    owner_id    BIGINT REFERENCES users (id) ON DELETE CASCADE,
    request_id  BIGINT REFERENCES request (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text      VARCHAR(200)                NOT NULL,
    created   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    author_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
    item_id   BIGINT REFERENCES items (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    start_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    status     VARCHAR(50)                 NOT NULL,
    booker_id  BIGINT REFERENCES users (id) ON DELETE CASCADE,
    item_id    BIGINT REFERENCES items (id) ON DELETE CASCADE
);