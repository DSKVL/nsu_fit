#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL

CREATE TABLE crmusers (
  userId             VARCHAR(16) NOT NULL,
  password           VARCHAR(16) NOT NULL,
  firstName          VARCHAR(16),
  lastName           VARCHAR(16),
  fullName           VARCHAR(32),
  description        VARCHAR(256),
  empNo              VARCHAR(32),
  accessLevel        VARCHAR(256),
  disabled           BOOLEAN,
  PRIMARY KEY (userId)
);

EOSQL

