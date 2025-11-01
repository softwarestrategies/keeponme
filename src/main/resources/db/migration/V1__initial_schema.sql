-- Initial schema creation for KeepOnMe application
-- This migration creates the users table

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    keycloak_id VARCHAR(255) UNIQUE,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_keycloak_id ON users(keycloak_id);

-- Add comments for documentation
COMMENT ON TABLE users IS 'Application users synchronized from Keycloak';
COMMENT ON COLUMN users.id IS 'Primary key - auto-generated';
COMMENT ON COLUMN users.username IS 'Username from Keycloak - unique';
COMMENT ON COLUMN users.email IS 'User email address - unique';
COMMENT ON COLUMN users.keycloak_id IS 'Reference to Keycloak user UUID';
COMMENT ON COLUMN users.first_name IS 'User first name from Keycloak';
COMMENT ON COLUMN users.last_name IS 'User last name from Keycloak';
COMMENT ON COLUMN users.created_at IS 'Timestamp when user was first synchronized';
COMMENT ON COLUMN users.updated_at IS 'Timestamp of last synchronization';

-- Spring Modulith Event Publication table
-- This table is required for Spring Modulith's event publication registry
CREATE TABLE event_publication (
                                   id UUID NOT NULL PRIMARY KEY,
                                   completion_date TIMESTAMP(6),
                                   event_type VARCHAR(512) NOT NULL,
                                   listener_id VARCHAR(512) NOT NULL,
                                   publication_date TIMESTAMP(6) NOT NULL,
                                   serialized_event TEXT NOT NULL
);

CREATE INDEX idx_event_publication_by_completion_date
    ON event_publication (completion_date);

CREATE INDEX idx_event_publication_by_event_and_listener_id
    ON event_publication (event_type, listener_id);
