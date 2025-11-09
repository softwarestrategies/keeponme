# Dev Notes

## Database

### Connect as superuser to default database
docker exec -it keeponme-postgres psql -U postgres -d postgres

### Connect to the keeponme database
docker exec -it keeponme-postgres psql -U keeponme -d keeponme

### Connect to the keycloak database
docker exec -it keeponme-postgres psql -U keycloak -d keycloak

## Environment Variables

- SPRING_PROFILES_ACTIVE: dev
- KEYCLOAK_CLIENT_ID: keeponme-client
- KEYCLOAK_CLIENT_SECRET: changeme
- POSTGRES_DB_USER: keeponme
- POSTGRES_DB_PASSWORD: changeme

## Odds-n-Ends

### Change the Postgres user password

We need to change the password for the keeponme-postgres user.  This is done by running the following commands:

- docker exec -it keeponme-postgres psql -U postgres -c "ALTER USER keeponme WITH PASSWORD 'some_other_password';

Make sure that it is reflected in the Environment variable that was set


