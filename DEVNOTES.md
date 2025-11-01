# Dev Notes

## Database

### Connect as superuser to default database
docker exec -it keeponme-postgres psql -U postgres -d postgres

### Connect to the keeponme database
docker exec -it keeponme-postgres psql -U keeponme -d keeponme

### Connect to the keycloak database
docker exec -it keeponme-postgres psql -U keycloak -d keycloak