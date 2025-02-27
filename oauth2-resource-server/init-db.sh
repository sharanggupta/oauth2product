#!/bin/bash
set -e

# Wait for PostgreSQL to be ready
until pg_isready -h "$POSTGRES_HOST" -U "$POSTGRES_USER"; do
  echo "Waiting for PostgreSQL to be ready..."
  sleep 2
done

# Connect to PostgreSQL and create the user and database if they don't exist
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "postgres" <<-EOSQL
  -- Check if user exists
  DO \$\$ BEGIN
      IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'productuser') THEN
          CREATE USER productuser WITH PASSWORD 'productpass';
      END IF;
  END \$\$;

  -- Check if database exists
  DO \$\$ BEGIN
      IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'productdb') THEN
          CREATE DATABASE productdb;
      END IF;
  END \$\$;

  -- Grant privileges
  GRANT ALL PRIVILEGES ON DATABASE productdb TO productuser;
EOSQL
