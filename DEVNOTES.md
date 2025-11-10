# Dev Notes

## Stuff to document

### Environment Variables

KEYCLOAK_CLIENT_SECRET=changeme;KEYCLOAK_CLIENT_ID=keeponme-client;POSTGRES_DB_PASSWORD=changeme;POSTGRES_DB_USER=keeponme;SPRING_PROFILES_ACTIVE=dev

### Generate Access Token for Keycloak

% curl -X POST 'http://localhost:8080/realms/keeponme/protocol/openid-connect/token' \
-H 'Content-Type: application/x-www-form-urlencoded' \
-d 'client_id=keeponme-client' \
-d 'client_secret=changeme' \
-d 'username=testuser' \
-d 'password=changeme' \
-d 'grant_type=password'

### Get OpenID Config from Keycloak

curl http://localhost:8080/realms/keeponme/.well-known/openid-configuration

### Docker odds-n-ends

make clean | make verify

docker compose up -d

docker compose stop

docker compose down









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


