# Flyway Database Migration Guide

## 🎯 What is Flyway?

Flyway is a database migration tool that allows you to version control your database schema. Instead of manually running SQL scripts or relying on Hibernate's `ddl-auto`, Flyway manages schema changes in a controlled, versioned manner.

## ✅ Why Use Flyway?

### Benefits
- ✅ **Version Control**: Database schema is versioned like code
- ✅ **Reproducible**: Same migrations run in every environment
- ✅ **Team Collaboration**: No conflicts with concurrent schema changes
- ✅ **Audit Trail**: Complete history of all database changes
- ✅ **Safety**: Validates migrations before applying
- ✅ **Rollback Support**: Can create rollback scripts
- ✅ **CI/CD Ready**: Integrates seamlessly with deployment pipelines

### vs Hibernate DDL Auto
| Feature | Flyway | Hibernate DDL Auto |
|---------|--------|-------------------|
| Version Control | ✅ Yes | ❌ No |
| Production Safe | ✅ Yes | ❌ No |
| Custom SQL | ✅ Yes | ❌ Limited |
| Data Migrations | ✅ Yes | ❌ No |
| Team Friendly | ✅ Yes | ⚠️ Conflicts |
| Audit Trail | ✅ Yes | ❌ No |

## 📁 Project Structure

```
src/main/resources/db/migration/
├── V1__initial_schema.sql           # Initial schema
├── V2__add_phone_number.sql.example # Example future migration
└── (future migrations here)
```

## 🔢 Naming Convention

Flyway uses a strict naming convention:

### Versioned Migrations
```
V{version}__{description}.sql

Examples:
V1__initial_schema.sql
V2__add_user_roles.sql
V3__create_audit_log.sql
V2.1__add_indexes.sql  (sub-version)
```

### Repeatable Migrations
```
R__{description}.sql

Examples:
R__create_views.sql
R__update_functions.sql
```

**Rules:**
- **V** = Versioned (runs once, in order)
- **R** = Repeatable (runs every time checksum changes)
- **Double underscore** (__) separates version from description
- **Version numbers** must be unique and sequential
- **Description** uses underscores, not spaces

## 🚀 How It Works

### First Startup

1. **Flyway creates tracking table**: `flyway_schema_history`
2. **Scans migration files**: Finds `V1__initial_schema.sql`
3. **Applies migration**: Executes SQL
4. **Records in history**: Marks V1 as applied

### Subsequent Startups

1. **Checks history**: V1 already applied
2. **Scans for new migrations**: Finds V2, V3, etc.
3. **Applies in order**: V2 → V3 → ...
4. **Updates history**: Records each migration

### The History Table

```sql
SELECT * FROM flyway_schema_history;

 installed_rank | version |      description      | type | script                    | checksum    | installed_by | installed_on        | execution_time | success 
----------------+---------+-----------------------+------+---------------------------+-------------+--------------+---------------------+----------------+---------
              1 | 1       | initial schema        | SQL  | V1__initial_schema.sql    | -1234567890 | keeponme     | 2025-11-01 12:00:00 |             45 | t       
              2 | 2       | add user roles        | SQL  | V2__add_user_roles.sql    |   123456789 | keeponme     | 2025-11-02 10:30:00 |             12 | t       
```

## 📝 Creating Migrations

### Step 1: Create Migration File

```bash
# Format: V{next_version}__{description}.sql
cd src/main/resources/db/migration/
touch V2__add_user_roles.sql
```

### Step 2: Write SQL

```sql
-- V2__add_user_roles.sql
-- Add roles functionality to users

CREATE TABLE user_roles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_name VARCHAR(50) NOT NULL,
    granted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_user_role UNIQUE (user_id, role_name)
);

CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_name ON user_roles(role_name);
```

### Step 3: Test Locally

```bash
# Start application
./mvnw spring-boot:run

# Flyway will automatically apply the migration
# Check logs for:
# Migrating schema `public` to version "2 - add user roles"
```

### Step 4: Verify

```sql
-- Check migration was applied
SELECT * FROM flyway_schema_history;

-- Check table was created
\d user_roles
```

## 🎨 Migration Examples

### Add Column

```sql
-- V3__add_user_phone.sql
ALTER TABLE users ADD COLUMN phone_number VARCHAR(20);
CREATE INDEX idx_users_phone ON users(phone_number);
```

### Create Table

```sql
-- V4__create_audit_log.sql
CREATE TABLE audit_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT,
    old_value TEXT,
    new_value TEXT,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_user ON audit_log(user_id);
CREATE INDEX idx_audit_entity ON audit_log(entity_type, entity_id);
```

### Data Migration

```sql
-- V5__populate_default_roles.sql
INSERT INTO user_roles (user_id, role_name)
SELECT id, 'USER' FROM users
WHERE NOT EXISTS (
    SELECT 1 FROM user_roles WHERE user_roles.user_id = users.id
);
```

### Modify Column

```sql
-- V6__change_username_length.sql
ALTER TABLE users ALTER COLUMN username TYPE VARCHAR(500);
```

### Add Constraint

```sql
-- V7__add_email_constraint.sql
ALTER TABLE users 
ADD CONSTRAINT check_email_format 
CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}$');
```

## 🔄 Repeatable Migrations

For views, functions, procedures that need to be updated:

```sql
-- R__create_user_statistics_view.sql
CREATE OR REPLACE VIEW user_statistics AS
SELECT 
    COUNT(*) as total_users,
    COUNT(CASE WHEN created_at > NOW() - INTERVAL '7 days' THEN 1 END) as new_users_week,
    COUNT(CASE WHEN created_at > NOW() - INTERVAL '30 days' THEN 1 END) as new_users_month
FROM users;
```

This runs every time the checksum changes.

## 🛠️ Flyway Configuration

### In application.yml

```yaml
spring:
  flyway:
    enabled: true                    # Enable Flyway
    baseline-on-migrate: true        # Handle existing databases
    locations: classpath:db/migration # Where to find migrations
    schemas: public                  # Target schema
    validate-on-migrate: true        # Validate checksums
    out-of-order: false             # Enforce order (recommended)
    placeholder-replacement: true    # Enable placeholders
    placeholders:                    # Define placeholders
      app-name: keeponme
```

### Environment-Specific

```yaml
# application-dev.yml
spring:
  flyway:
    clean-disabled: false  # Allow clean in dev

# application-prod.yml
spring:
  flyway:
    clean-disabled: true   # Never clean in production!
    validate-on-migrate: true
```

## 🧪 Testing Migrations

### Local Testing

```bash
# Start with clean database
docker-compose down -v
docker-compose up -d postgres

# Run application
./mvnw spring-boot:run

# Check logs
# Look for: "Migrating schema `public` to version X"
```

### Integration Tests

```java
@SpringBootTest
@Testcontainers
class MigrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");
    
    @Test
    void flywayMigrationsSucceed() {
        // Flyway runs automatically before test
        // If migrations fail, test fails
        assertThat(dataSource).isNotNull();
    }
}
```

## 🔍 Flyway Commands

### Check Migration Status

```bash
./mvnw flyway:info
```

### Validate Migrations

```bash
./mvnw flyway:validate
```

### Clean Database (DEV ONLY!)

```bash
# ⚠️ DANGEROUS: Drops all objects!
./mvnw flyway:clean

# Then migrate from scratch
./mvnw flyway:migrate
```

### Repair Metadata

```bash
# If migration failed and marked as failed
./mvnw flyway:repair
```

## 🚨 Common Issues & Solutions

### Issue 1: Migration Failed

**Symptom:**
```
Migration V2__add_user_roles.sql failed
```

**Solution:**
```bash
# 1. Fix the SQL in the migration file
# 2. Repair Flyway metadata
./mvnw flyway:repair

# 3. Try again
./mvnw spring-boot:run
```

### Issue 2: Checksum Mismatch

**Symptom:**
```
Migration checksum mismatch for migration version 1
```

**Solution:**
```bash
# Option A: Never modify applied migrations! Create new migration instead.

# Option B: If you MUST fix it (dev only):
./mvnw flyway:repair
```

### Issue 3: Out of Order Migration

**Symptom:**
```
Detected resolved migration not applied to database: V1.5
```

**Solution:**
```yaml
# Allow out-of-order (not recommended for production)
spring:
  flyway:
    out-of-order: true
```

### Issue 4: Baseline Existing Database

**Symptom:**
```
Found non-empty schema without schema history table
```

**Solution:**
```yaml
spring:
  flyway:
    baseline-on-migrate: true
```

## 📋 Best Practices

### DO ✅

1. **Version everything**: Every schema change gets a migration
2. **Test locally first**: Run migration on local DB before commit
3. **One change per migration**: Keep migrations focused
4. **Use transactions**: Migrations are transactional by default
5. **Add comments**: Document why changes are made
6. **Version control**: Commit migrations with code
7. **Backup before**: Always backup production before major migrations

### DON'T ❌

1. **Never modify applied migrations**: Create new migration instead
2. **Don't use Hibernate ddl-auto**: Set to `validate` or `none`
3. **Don't skip versions**: Keep versions sequential
4. **Don't use flyway:clean in prod**: Data loss!
5. **Don't commit broken migrations**: Test first
6. **Don't share version numbers**: Each migration = unique version

## 🔐 Production Deployment

### Pre-Deployment Checklist

- [ ] All migrations tested locally
- [ ] Migrations reviewed by team
- [ ] Database backed up
- [ ] Rollback plan documented
- [ ] Downtime window (if needed) scheduled
- [ ] Migration files committed to git

### Deployment Process

```bash
# 1. Backup database
pg_dump -U keeponme keeponme > backup.sql

# 2. Deploy application (migrations run automatically)
docker-compose up -d app

# 3. Check logs
docker-compose logs -f app | grep Flyway

# 4. Verify migrations
docker exec -it keeponme-postgres psql -U keeponme -d keeponme -c "SELECT * FROM flyway_schema_history ORDER BY installed_rank DESC LIMIT 5;"
```

### Rollback Strategy

```sql
-- If migration causes issues, create rollback migration
-- V8__rollback_feature_x.sql
ALTER TABLE users DROP COLUMN phone_number;
DROP TABLE user_roles;
```

## 📊 Monitoring Flyway

### Check Current Version

```sql
SELECT MAX(version) as current_version 
FROM flyway_schema_history 
WHERE success = true;
```

### View Migration History

```sql
SELECT 
    installed_rank,
    version,
    description,
    script,
    installed_on,
    execution_time || ' ms' as duration,
    success
FROM flyway_schema_history
ORDER BY installed_rank DESC;
```

### Failed Migrations

```sql
SELECT * 
FROM flyway_schema_history 
WHERE success = false;
```

## 🔗 Integration with CI/CD

### GitHub Actions

```yaml
- name: Run Flyway Migrations
  run: ./mvnw flyway:migrate

- name: Validate Migrations
  run: ./mvnw flyway:validate
```

### Docker Build

```dockerfile
# Migrations run automatically when app starts
# No special steps needed!
```

## 📚 Additional Resources

- [Flyway Documentation](https://flywaydb.org/documentation/)
- [SQL Best Practices](https://flywaydb.org/documentation/concepts/migrations#sql-based-migrations)
- [Version Numbering](https://flywaydb.org/documentation/concepts/migrations#versioned-migrations)

## 🎯 Summary

### Current Setup

```
✅ Flyway enabled
✅ Initial schema (V1) created
✅ Example migration provided
✅ Hibernate set to validate
✅ Production-ready configuration
```

### Next Steps

1. Run application: `./mvnw spring-boot:run`
2. Verify migration: Check `flyway_schema_history` table
3. Create new migration when schema changes needed
4. Follow naming convention: `V{n}__{description}.sql`

---

**Remember**: With Flyway, your database schema is now code! 🎉
