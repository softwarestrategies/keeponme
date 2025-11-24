# KeepOnMe 🔐

Production-ready Spring Boot application with Keycloak OAuth2/OIDC authentication, built with Spring Modulith architecture and HTMX-powered UI.

[Quick Start guide](docs/QUICKSTART.md#)


## 🚀 Technology Stack

- **Java 25** - Latest Java release
- **Spring Boot 3.5.7** - Application framework
- **Spring Modulith 1.3.1** - Modular monolith architecture
- **Spring Security** - OAuth2/OIDC authentication
- **Spring Cloud Gateway MVC** - Non-reactive API gateway
- **Keycloak 24.0** - Identity and Access Management
- **PostgreSQL 16** - Relational database (shared setup)
- **HTMX 1.9.10** - Dynamic UI interactions
- **Thymeleaf** - Server-side templating
- **Lombok 1.18.42** - Java 25 compatible code generation
- **Docker & Docker Compose** - Containerization
- **Maven** - Build tool

## ✨ Features

- ✅ OAuth2/OIDC authentication with Keycloak
- ✅ Spring Modulith modular architecture
- ✅ HTMX-powered dynamic UI
- ✅ User synchronization from Keycloak to application database
- ✅ Shared PostgreSQL setup (separate databases for Keycloak and application)
- ✅ Production-ready security configuration
- ✅ Docker Compose deployment
- ✅ Testcontainers for integration testing
- ✅ Health checks and monitoring endpoints
- ✅ Java 25 with Lombok 1.18.42 compatibility

## 📋 Prerequisites

- **Java 25** ([Download Eclipse Temurin](https://adoptium.net/))
- **Docker & Docker Compose** ([Download Docker](https://www.docker.com/get-started))
- **Maven 3.9+** (or use included Maven wrapper)

## 🔧 Java 25 Configuration

This project uses **Java 25** with specific configuration for compatibility:

### Critical Properties
```xml
<java.version>25</java.version>
<maven.compiler.release>${java.version}</maven.compiler.release>  <!-- CRITICAL -->
<lombok.version>1.18.42</lombok.version>  <!-- Java 25 compatible -->
```

### Why These Matter
- **maven.compiler.release**: Ensures proper Java 25 bytecode generation
- **Lombok 1.18.42**: First version with full Java 25 support (fixes "TypeTag :: UNKNOWN" errors)

See [JAVA25_UPGRADE.md](./docs/JAVA25_UPGRADE.md) and [JAVA25_QUICK_REFERENCE.md](./docs/JAVA25_QUICK_REFERENCE.md) for complete details.

## 🚀 Quick Start

### 1. Clone and Navigate
```bash
git clone <repository-url>
cd keeponme-orchestrator
```

### 2. Verify Java Installation
```bash
java -version  # Should show "25"
```

### 3. Start with Docker Compose
```bash
docker-compose up --build
```

This will start:
- **PostgreSQL** on port 5432 (shared database)
- **Keycloak** on port 8080
- **Application** on port 9090

### 4. Configure Keycloak

#### Access Keycloak Admin Console
- URL: http://localhost:8080
- Username: `admin`
- Password: `changeme`

#### Create Realm
1. Click **Create Realm**
2. Name: `keeponme`
3. Click **Create**

#### Create Client
1. Go to **Clients** → **Create Client**
2. Client ID: `keeponme-client`
3. Click **Next**
4. Enable **Client authentication**
5. Enable **Authorization**
6. Valid redirect URIs: `http://localhost:9090/*`
7. Web origins: `http://localhost:9090`
8. Click **Save**
9. Go to **Credentials** tab and copy the **Client Secret**

#### Update Application Configuration
Edit `src/main/resources/application.yml`:
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          keycloak:
            client-secret: <paste-your-client-secret-here>
```

#### Create Test User
1. Go to **Users** → **Create User**
2. Username: `testuser`
3. Email: `testuser@example.com`
4. First Name: `Test`
5. Last Name: `User`
6. Email Verified: **ON**
7. Click **Create**
8. Go to **Credentials** tab
9. Set Password: `password`
10. Temporary: **OFF**
11. Click **Set Password**

### 5. Access Application
- Application: http://localhost:9090
- Click **Sign In with Keycloak**
- Login with `testuser` / `password`

## 📁 Project Structure

```
keeponme-orchestrator/
├── src/
│   ├── main/
│   │   ├── java/com/keeponme/
│   │   │   ├── KeepOnMeApplication.java      # Main application
│   │   │   ├── config/                        # Configuration module
│   │   │   ├── security/                      # Security module
│   │   │   │   └── SecurityConfig.java        # OAuth2 configuration
│   │   │   ├── user/                          # User module
│   │   │   │   ├── User.java                  # User entity
│   │   │   │   ├── UserRepository.java        # Data access
│   │   │   │   └── UserService.java           # Business logic
│   │   │   ├── web/                           # Web module
│   │   │   │   └── HomeController.java        # Controllers
│   │   │   └── gateway/                       # Gateway module
│   │   │       └── GatewayConfig.java         # Gateway routes
│   │   └── resources/
│   │       ├── application.yml                # Main configuration
│   │       └── templates/                     # Thymeleaf templates
│   │           ├── index.html                 # Landing page
│   │           ├── dashboard.html             # User dashboard
│   │           └── profile.html               # User profile
│   └── test/
│       ├── java/com/keeponme/
│       │   └── KeepOnMeApplicationTests.java
│       └── resources/
│           └── application-test.yml
├── docker-compose.yml                         # Docker services
├── Dockerfile                                 # Application image
├── init-db.sql                               # Database initialization
├── pom.xml                                   # Maven configuration
└── README.md
```

## 🔨 Building

### Build JAR
```bash
./mvnw clean package
```

### Run Tests
```bash
./mvnw test
```

### Run Locally (without Docker)
```bash
./mvnw spring-boot:run
```

### Build Docker Image
```bash
docker build -t keeponme:latest .
```

## 🗄️ Database Architecture

### Shared PostgreSQL Setup
Single PostgreSQL instance with two separate databases:

1. **keycloak** - Keycloak data (user credentials, realms, clients)
2. **keeponme** - Application data (user profiles, business data)

### Benefits
- ✅ 300MB memory savings vs separate instances
- ✅ Simplified deployment
- ✅ Security isolation maintained
- ✅ Easy backup and management

### Database Users
- `postgres` - Superuser
- `keycloak` - Keycloak database owner
- `keeponme` - Application database owner

## 🔒 Security

### OAuth2/OIDC Flow
1. User clicks "Sign In"
2. Redirected to Keycloak
3. User authenticates
4. Keycloak issues tokens
5. Application validates tokens
6. User synced to application database
7. Session established

### Endpoints
- **Public**: `/`, `/public/**`, `/css/**`, `/js/**`, `/error`
- **Protected**: `/dashboard`, `/profile`, all others
- **Actuator**: `/actuator/health`, `/actuator/info`

## 📊 Monitoring

### Health Check
```bash
curl http://localhost:9090/actuator/health
```

### Application Info
```bash
curl http://localhost:9090/actuator/info
```

## 🧪 Testing

### Unit Tests
```bash
./mvnw test
```

### Integration Tests (with Testcontainers)
```bash
./mvnw verify
```

## 🐳 Docker Commands

### Start Services
```bash
docker-compose up -d
```

### View Logs
```bash
docker-compose logs -f app
```

### Stop Services
```bash
docker-compose down
```

### Rebuild and Restart
```bash
docker-compose up --build --force-recreate
```

### Clean Volumes
```bash
docker-compose down -v
```

## 🔍 Troubleshooting

### Issue: Lombok Compilation Errors
**Solution**: Ensure Lombok 1.18.42 is being used:
```bash
./mvnw dependency:tree | grep lombok
```

### Issue: Application Can't Connect to Keycloak
**Solution**: Check Keycloak is running:
```bash
docker-compose ps
curl http://localhost:8080/health
```

### Issue: Database Connection Failed
**Solution**: Check PostgreSQL is healthy:
```bash
docker-compose ps postgres
docker-compose logs postgres
```

### Issue: Port Already in Use
**Solution**: Change ports in `docker-compose.yml`:
```yaml
ports:
  - "9091:9090"  # Changed from 9090
```

## 📚 Documentation

- [Java 25 Upgrade Guide](./docs/JAVA25_UPGRADE.md)
- [Java 25 Quick Reference](./docs/JAVA25_QUICK_REFERENCE.md)
- [Spring Modulith Documentation](https://spring.io/projects/spring-modulith)
- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [HTMX Documentation](https://htmx.org/docs/)

## 🛠️ Development

### IDE Setup

#### IntelliJ IDEA
1. Install Lombok plugin
2. Enable annotation processing
3. Set Project SDK to Java 25

#### VS Code
1. Install Extension Pack for Java
2. Install Lombok extension
3. Configure Java runtime

### Code Style
- Use Lombok annotations where appropriate
- Follow Spring Boot conventions
- Keep modules loosely coupled
- Write tests for business logic

## 📝 License

This project is licensed under the MIT License.

## 🤝 Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## 📞 Support

For issues and questions:
- Open an issue on GitHub
- Check existing documentation
- Review troubleshooting guide

---

Built with ❤️ using Spring Boot and Keycloak
