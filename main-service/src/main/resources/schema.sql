DROP TABLE IF EXISTS users, events, categories, locations, requests, events_compilations, compilations CASCADE;
CREATE TABLE IF NOT EXISTS users (
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name  varchar(250) NOT NULL,
    email varchar(254) NOT NULL,
    UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories (
    id  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS locations (
    id  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    lat FLOAT,
    lon FLOAT
);

CREATE TABLE IF NOT EXISTS events (
    id  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title              varchar(120),
    description        varchar(7000),
    annotation         varchar(2000),
    state              varchar(16),
    category_id        int REFERENCES categories (id),
    created            TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    event_date         TIMESTAMP,
    published          TIMESTAMP,
    confirmed_requests int,
    location_id        BIGINT REFERENCES locations (id),
    initiator_id       BIGINT REFERENCES users (id),
    paid               boolean,
    participant_limit  int,
    request_moderation boolean
);

CREATE TABLE IF NOT EXISTS requests (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  event_id bigint NOT NULL REFERENCES events (id),
  requester_id bigint NOT NULL REFERENCES users (id),
  state varchar(255) NOT NULL DEFAULT 'PENDING',
  created TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT requests_pk PRIMARY KEY  (requester_id, event_id)
);

CREATE TABLE IF NOT EXISTS compilations (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
  pinned boolean NOT NULL,
  title varchar(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS events_compilations (
 compilation_id bigint REFERENCES compilations (id) NOT NULL,
 event_id bigint REFERENCES events (id) NOT NULL,
 CONSTRAINT pk_events_compilations PRIMARY KEY(compilation_id,event_id)
);
