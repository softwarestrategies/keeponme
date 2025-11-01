# Flyway Quick Reference Card

## 🚀 Creating Migrations

```bash
# Create new migration file
cd src/main/resources/db/migration/
touch V2__description_here.sql

# Edit the file with your SQL
vim V2__description_here.sql
```

## 📝 Naming Format

```
V{version}__{description}.sql

✅ Good:
V1__initial_schema.sql
V2__add_user_roles.sql
V3__create_audit_table.sql
V2.1__fix_indexes.sql

❌ Bad:
v1_schema.sql         (lowercase v)
V1_schema.sql         (single underscore)
V1 schema.sql         (space in name)
2__add_roles.sql      (missing V)
```

## 🔍 Common Commands

```bash
# Check migration status
./mvnw flyway:info

# Validate migrations
./mvnw flyway:validate

# Clean database (⚠️ DEV ONLY!)
./mvnw flyway:clean

# Apply migrations manually
./mvnw flyway:migrate

# Repair failed migration
./mvnw flyway:repair
```

## 📊 Check Migration Status (SQL)

```sql
-- View all migrations
SELECT * FROM flyway_schema_history 
ORDER BY installed_rank DESC;

-- Current version
SELECT MAX(version) FROM flyway_schema_history 
WHERE success = true;

-- Failed migrations
SELECT * FROM flyway_schema_history 
WHERE success = false;

-- Last 5 migrations
SELECT version, description, installed_on, execution_time 
FROM flyway_schema_history 
ORDER BY installed_rank DESC 
LIMIT 5;
```

## 🛠️ Migration Templates

### Add Column
```sql
-- V2__add_phone_to_users.sql
ALTER TABLE users ADD COLUMN phone VARCHAR(20);
CREATE INDEX idx_users_phone ON users(phone);
```

### Create Table
```sql
-- V3__create_roles.sql
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Add Foreign Key
```sql
-- V4__add_user_role_relation.sql
ALTER TABLE users ADD COLUMN role_id BIGINT;
ALTER TABLE users ADD CONSTRAINT fk_user_role 
    FOREIGN KEY (role_id) REFERENCES roles(id);
```

### Data Migration
```sql
-- V5__populate_default_data.sql
INSERT INTO roles (name) VALUES 
    ('ADMIN'),
    ('USER'),
    ('MODERATOR');
```

### Drop Column
```sql
-- V6__remove_old_field.sql
ALTER TABLE users DROP COLUMN IF EXISTS old_field;
```

## 🚨 Emergency Procedures

### Migration Failed

```bash
# 1. Fix the SQL error in migration file
vim src/main/resources/db/migration/Vx__problematic.sql

# 2. Repair Flyway state
./mvnw flyway:repair

# 3. Try again
./mvnw spring-boot:run
```

### Checksum Mismatch

```bash
# ⚠️ Only if absolutely necessary (dev environment)
./mvnw flyway:repair

# Better: Create new migration to fix issue
touch V{next}__fix_previous_migration.sql
```

### Reset Database (DEV ONLY)

```bash
# ⚠️ DELETES ALL DATA!
docker-compose down -v
docker-compose up -d postgres
./mvnw spring-boot:run  # Migrations run fresh
```

## ✅ Best Practices

```
✅ One change per migration
✅ Test locally first  
✅ Add comments in SQL
✅ Use transactions
✅ Sequential versions
✅ Backup before prod deploy

❌ Never modify applied migrations
❌ Don't use flyway:clean in prod
❌ Don't skip version numbers
❌ Don't commit broken migrations
```

## 🔧 Configuration Quick Reference

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate  # Let Flyway manage schema
  
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
    validate-on-migrate: true
```

## 📱 During Development

```bash
# 1. Make schema change needed
# 2. Create migration
touch V{n}__change_description.sql

# 3. Write SQL
cat > V{n}__change_description.sql << 'EOF'
ALTER TABLE users ADD COLUMN new_field VARCHAR(100);
EOF

# 4. Test
./mvnw spring-boot:run

# 5. Verify
psql -U keeponme -d keeponme -c "\d users"

# 6. Commit
git add src/main/resources/db/migration/V{n}__*.sql
git commit -m "Add new_field to users table"
```

## 🎯 Production Deployment

```bash
# Before deployment
1. Backup database
2. Test migrations locally
3. Review with team

# Deploy
docker-compose up -d app  # Flyway runs automatically

# Verify
docker-compose logs app | grep Flyway
docker exec -it keeponme-postgres psql -U keeponme -d keeponme \
    -c "SELECT * FROM flyway_schema_history ORDER BY installed_rank DESC LIMIT 3;"

# If issues
# Create rollback migration Vx__rollback.sql
```

## 🔗 Useful Links

- Migration in project: `src/main/resources/db/migration/`
- History table: `flyway_schema_history`
- Full guide: `FLYWAY_GUIDE.md`
- Flyway docs: https://flywaydb.org/documentation/

---

**Quick Start**: `touch V2__my_change.sql` → write SQL → `./mvnw spring-boot:run` ✅
