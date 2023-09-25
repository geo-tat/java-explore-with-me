DROP TABLE IF EXISTS stats;
CREATE TABLE IF NOT EXISTS stats(
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
app VARCHAR(100) NOT NULL,
uri VARCHAR(100) NOT NULL,
ip VARCHAR(100) NOT NULL,
date TIMESTAMP WITHOUT TIME ZONE NOT NULL
);