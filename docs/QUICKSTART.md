# ⚡ Quick Start Guide

Get KeepOnMe running in 10 minutes!

## ✅ Prerequisites Check

```bash
# Check Java version
java -version  # Must be 25

# Check Docker
docker --version
docker compose
```
## ✅ Step 1: Setup Keycloak configuration before starting any services or applications

I want to run Keycloak on port 7777 and my main application on port 8080.  So I need to adjust the docker-compose.yml file accordingly.

### Adjust values in the docker-compose.yml file
Set the Keycloak port to 7777
```bash
keycloak:
  ..
  ports:
    - "7777:8080"
```

### Adjust values in the /config/keycloak/keycloak-realm.json file
```bash

- Change "sslRequired" property from "none" when applicable

  Should be either "all" or "external" as soon as deployment is to a public facing environment.
  
- Change "redirectUris" to match your application's URL.  I want my application to run on port 8080 and so I have the following:

      "redirectUris": [
        "http://localhost:8080/*",
        "http://localhost:8080/login/oauth2/code/keycloak"
      ],
      "webOrigins": [
        "http://localhost:8080"
      ]
```

### Adjust values in the /keycloak/themes/keeponme/login/login.ftl file
I want my application to run on port 8080 and so I have the following:
```bash
  <button tabindex="8" class="${properties.kcButtonClass!} ${properties.kcButtonDefaultClass!} ${properties.kcButtonLargeClass!}"
    type="button"
    onclick="window.location.href='http://localhost:8080/'"
    style="width: 100%;">
    Cancel
  </button>
```

## ✅ Step 2: Startup Docker Compose-configured Services, which are PostgreSQL and Keycloak

```bash
docker compose up -d
```

Wait for services to be healthy:
```bash
docker-compose ps
```

## Step 3: Log into the Keycloak and configure some other settings

In your browser, navigate to http://localhost:7777 and log in with the default credentials noted in the docker-compose.yml file:  temp_admin/changeme

Once logged in, you will be presented with a screen that has this notification across the top:
```bash
You are logged in as a temporary admin user. To harden security, create a permanent admin account and delete the temporary one.
```

We need to create a permanent admin user.  And then we will delete the temporary one created during the initial Keycloak setup.

### Task #1: Create a permanent Keycloak "admin" user. 

- Navigate to Users -> Add User
  - Set "Email verified" to "Yes"  (Optional)
  - Set Username to "admin"  (whatever you want)
  - Set Email to "admin@keeponme.com"  (whatever it is)
  - Set the "First Name" and "Last Name"  (whatever it is)
  - Click "Create"
- Click on the "Credentials" table and then click on "Set Password"
  - Set Password and Confirmation Password
  - Set Temporary to "Off"
  - Click "Save"
- Set User Roles for "admin", clicking on the "Role Mappings" tab:
  - Click on "Assign Role" dropdown and first choose "Realm roles"
  - Check the whatever ones you want (or all) and click "Assign"
  - Click on "Assign Role" dropdown again and this time choose "Client roles"
  - Check the whatever ones you want (or all) and click "Assign"
- Get rid of the "temp_admin" user created during the initial Keycloak setup.
  - Click on the "Users" tab
    - Check the box next to the "temp_admin" user and click on the "Delete User" button.

### Task #2: Setup "keeponme" realm settings. 

- Click on "Manage Realms" and choose "keeponme"
- Click on "Clients" and choose "keeponme-client"
  - Click on the "Credentials" tab
  - For "Client Secret", click on "Regenerate" button and then copy the value and set it aside.
- Now we want to set the KeepOnMe realm's UI theme, which corresponds to what we set in the docker-compose.yml file.
- Click on "Realm Settings", then the "Themes" tab.
  - Under "login theme", choose "keycloak" and Save it.

## ✅ Step 4: Setup the KeepOnMe application configuration and then start the application

### Adjust values in the "application.yml" and "application-test.yml" files
The "server.port" property should match the port you set in the keycloak-realm-config.json file.  I have it set to 8080.
```bash
server:
  port: 8080
```

The "spring.security.oauth2.client.provider.keycloak.issuer-uri", which is in two places, needs to have the correct Keycloak port (I chose 7777, see above)
```bash
spring:
  ...
  security:
    oauth2:
      client:
        provider:
          keycloak:
            issuer-uri: http://localhost:8080/realms/keeponme
```

### Setup the environment variables for the application
Here are the environment variables that need to be set:
```bash
- KEYCLOAK_CLIENT_SECRET={ THE CLIENT SECRET YOU SET UP ABOVE IN KEYCLOAK };
- KEYCLOAK_CLIENT_ID=keeponme-client;
- POSTGRES_DB_PASSWORD=changeme;
- POSTGRES_DB_USER=keeponme;
- SPRING_PROFILES_ACTIVE=dev
```