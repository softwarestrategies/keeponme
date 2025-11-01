# ⚡ Quick Start Guide

Get KeepOnMe running in 5 minutes!

## Prerequisites Check

```bash
# Check Java version
java -version  # Must be 25

# Check Docker
docker --version
docker-compose --version
```

## Step 1: Start Services (2 min)

```bash
cd keeponme-orchestrator
docker-compose up -d
```

Wait for services to be healthy:
```bash
docker-compose ps
```

## Step 2: Configure Keycloak (2 min)

### Access Admin Console
- Open: http://localhost:8080
- Login: `admin` / `admin`

### Create Realm
1. Click **Create Realm**
2. Name: `keeponme`
3. **Create**

### Create Client
1. **Clients** → **Create Client**
2. Client ID: `keeponme-client`
3. **Next**
4. ✅ Client authentication
5. ✅ Authorization
6. **Save**
7. **Credentials** tab → Copy **Client Secret**

### Set Redirect URIs
1. Valid redirect URIs: `http://localhost:9090/*`
2. Web origins: `http://localhost:9090`
3. **Save**

### Create User
1. **Users** → **Create User**
2. Username: `testuser`
3. Email: `testuser@example.com`
4. ✅ Email Verified
5. **Create**
6. **Credentials** tab
7. Set Password: `password`
8. ❌ Temporary
9. **Set Password**

## Step 3: Update Client Secret (30 sec)

Edit `src/main/resources/application.yml`:

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          keycloak:
            client-secret: YOUR_COPIED_SECRET_HERE
```

## Step 4: Restart Application (30 sec)

```bash
docker-compose restart app
```

## Step 5: Login! 🎉

1. Open: http://localhost:9090
2. Click **Sign In with Keycloak**
3. Login: `testuser` / `password`
4. You're in!

## Verification

```bash
# Check all services
docker-compose ps

# Check application logs
docker-compose logs app

# Health check
curl http://localhost:9090/actuator/health
```

## Quick Commands

```bash
# View logs
docker-compose logs -f app

# Stop services
docker-compose down

# Rebuild everything
docker-compose up --build --force-recreate

# Clean volumes
docker-compose down -v
```

## Troubleshooting

### Application not starting?
```bash
docker-compose logs app
```

### Can't connect to Keycloak?
```bash
curl http://localhost:8080/health
```

### Port conflicts?
Edit `docker-compose.yml` and change ports.

## Next Steps

- Read [README.md](../README.md) for full documentation
- Check [JAVA25_UPGRADE.md](./docs/JAVA25_UPGRADE.md) for Java 25 details
- Explore the code structure

---

**Need Help?** Check the full README or open an issue!
