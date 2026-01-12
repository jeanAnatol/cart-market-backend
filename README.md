# cart-market-backend

Back end part of the car market application — an Online market for used vehicles.

This repository contains the backend service implemented in Java using Spring Boot and Gradle. The repo includes the Gradle wrapper so you can build without a system Gradle installation.

---

## Table of contents

- [Important notes (from repo)](#important-notes-from-repo)
- [Project overview](#project-overview)
- [Requirements / prerequisites](#requirements--prerequisites)
- [Quick start (build & run)](#quick-start-build--run)
- [Database initialization instructions](#database-initialization-instructions)
- [Project structure & key files](#project-structure--key-files)
- [Dependencies / build tool details](#dependencies--build-tool-details)


---

## Important notes (from repo)

The repository contains an upload of important startup notes. They are reproduced below — follow them carefully during local setup:

1) Be sure to:
1. Set your password and custom generated secret key in:
    - `src/main/resources/application.properties`
        - `app.admin.password=your_password`
        - `app.security.secret-key=your_generated_secret_key`

    - If admin password remains blank, the password will default to `admin`.
    - If `app.security.secret-key` remains blank or the key is less than 32 characters long the application will fail-fast and force a shutdown.
    - For simplicity a secret key is already provided in the repo.
    - You can create a new secret key by running:
      ```
      openssl rand -base64 32
      ```
      (or `-base64 48`) on a system that has OpenSSL installed.

2. Define your DATABASE information in:
    - `src/main/resources/application-dev.properties`
      ```
      spring.datasource.url=your_database
      spring.datasource.username=your_user
      spring.datasource.password=your_user_password
      ```
    - The project is configured to use MySQL by default (driver and default values reflect this).
    - There is an included PostgreSQL dependency in `build.gradle` commented out — you may enable it if you switch to Postgres.

3. After the first successful run, stop the application and uncomment in `application-dev.properties`:
   ```
   spring.sql.init.mode=always
   spring.sql.init.data-locations=classpath:sql/all_in_one_to_begin.sql
   ```
   then run the application again. This will populate tables with initial data (Roles, Vehicle Types, Makes/Models, etc.). 
   Stop and comment those variables back after the population run; otherwise the app will crash because IDs already exist.

---

## Project overview

- Language: Java (100%).
- Build tool: Gradle with an included wrapper (`gradlew`, `gradlew.bat`, `gradle/`).
- Framework: Spring Boot (configured in `build.gradle`).
- Persistence: Spring Data JPA (MySQL configured by default, optional Postgres/H2 options present).
- Security: Spring Security with JWT (secret key required).
- API docs: springdoc OpenAPI (Swagger UI configured).

This README documents how to configure/run the application, how to handle secrets safely, and important repository-specific notes.

---

## Requirements / prerequisites

- Java JDK 17+ / 21 recommended (project toolchain uses Java 21 in Gradle config).
- Git
- MySQL for default local configuration (or another DB if you reconfigure)
- OpenSSL (optional) — for generating a secure random secret
- Docker (optional) — for containerized DB and/or app runs

---

## Quick start (build & run)

Clone repository:
```bash
git clone https://github.com/jeanAnatol/cart-market-backend.git
cd cart-market-backend
```

Build:
- Linux / macOS:
  ```bash
  ./gradlew clean build
  ```
- Windows:
  ```powershell
  gradlew.bat clean build
  ```

Run tests:
```bash
./gradlew test
```

Run application:
- If using Spring Boot `bootRun`:
  ```bash
  ./gradlew bootRun
  ```
- Or run the produced JAR:
  ```bash
  java -jar build/libs/<artifact>-<version>.jar
  ```
  Replace `<artifact>-<version>.jar` with actual artifact name produced under `build/libs/`.

If your `application-dev.properties` points to MySQL, ensure the MySQL instance is running and the DB credentials match.

---

## Configuration — how to provide secrets & DB details

Config files present in the repository:
- `src/main/resources/application.properties` — global/default settings (including the provided secret key and admin password default).
    - [View application.properties](https://github.com/jeanAnatol/cart-market-backend/blob/main/src/main/resources/application.properties)
- `src/main/resources/application-dev.properties` — development profile DB settings and optional SQL init lines.
    - [View application-dev.properties](https://github.com/jeanAnatol/cart-market-backend/blob/main/src/main/resources/application-dev.properties)

IMPORTANT:
- The repository currently contains configuration files with default secrets/values. For production or shared repositories, do NOT keep real credentials in the repo.

Generate a secure secret key locally (do not commit it):
```bash
openssl rand -base64 32
```

Local developer flow:
1. Copy the example file to the real file:
   ```bash
   cp src/main/resources/application.example.properties src/main/resources/application-dev.properties
   ```
2. Edit `application-dev.properties` with your local DB credentials (file should be ignored by Git).

---

## Database initialization instructions

Follow the notes above for the intended DB initialization sequence:

- On first run use the normal config (do not enable `spring.sql.init.*`).
- After the app boots successfully and the DB schema is created, stop the app.
- Enable the SQL data loader by uncommenting:
  ```
  spring.sql.init.mode=always
  spring.sql.init.data-locations=classpath:sql/all_in_one_to_begin.sql
  ```
- Run the app again to execute initial data population script (populates Roles, Vehicle Types, Makes/Models).
- Stop the app and comment those properties back (to prevent duplicate inserts and errors).

The data script (referenced above) should live at `src/main/resources/sql/all_in_one_to_begin.sql` — confirm presence before enabling.

---

## Project structure & key files

Top-level files (present in repo root):
- `.gitattributes`
- `.gitignore`
- `build.gradle` — Gradle build configuration
- `settings.gradle`
- `gradlew`, `gradlew.bat`, `gradle/` — Gradle wrapper
- `README.md` — this file

Source directories:
- `src/main/java/` — application Java sources (controller, service, domain, repositories, security, etc.)
- `src/main/resources/` — config and resources:
    - `application.properties` — default application configuration
    - `application-dev.properties` — development profile config
    - `sql/` — (optional) initialization SQL (e.g. `all_in_one_to_begin.sql`)
- `src/test/` — unit/integration tests

You can inspect `build.gradle` for the exact Spring Boot and dependency versions:
- [View build.gradle](https://github.com/jeanAnatol/cart-market-backend/blob/main/build.gradle)

---

## Dependencies / build tool details

Highlights from `build.gradle`:
- Plugins:
    - Java plugin
    - Spring Boot plugin: `org.springframework.boot` version `4.0.1`
    - Spring dependency management plugin
- Java toolchain:
    - languageVersion: 21
    - vendor: Amazon (Corretto)
- Major dependencies:
    - Spring Boot Starter Web
    - Spring Boot Starter Data JPA
    - Spring Boot Starter Security
    - springdoc-openapi (Swagger UI)
    - Hibernate Spatial and JTS (geometry support)
    - MySQL Connector (runtime)
    - JJWT (JSON Web Token library)
    - Lombok (compileOnly + annotationProcessor)
    - Spring Boot Starter Test + security test

See `build.gradle` for full list and commented-out alternatives (Postgres, H2).

---
