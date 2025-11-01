-- Create keycloak user and database
CREATE USER keycloak WITH PASSWORD 'changeme';
CREATE DATABASE keycloak OWNER keycloak;
GRANT ALL PRIVILEGES ON DATABASE keycloak TO keycloak;

-- Connect to keycloak database and grant schema permissions
\c keycloak
GRANT ALL ON SCHEMA public TO keycloak;
ALTER SCHEMA public OWNER TO keycloak;

-- Create keeponme user and database
CREATE USER keeponme WITH PASSWORD 'changeme';
CREATE DATABASE keeponme OWNER keeponme;
GRANT ALL PRIVILEGES ON DATABASE keeponme TO keeponme;

-- Connect to keeponme database and grant schema permissions
\c keeponme
GRANT ALL ON SCHEMA public TO keeponme;
ALTER SCHEMA public OWNER TO keeponme;