# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

RDP Web Portal — a Spring Boot 3.4.2 web application for managing and launching Remote Desktop connections. Users see their assigned machines and download `.rdp` files (or `.bat` launchers with embedded credentials). Admins manage users, machines, and assignments.

## Build & Run Commands

```bash
# Build
mvn clean package

# Run (port 9090)
mvn spring-boot:run

# Run tests
mvn test

# Run a single test class
mvn test -Dtest=RdpFileServiceTest

# Docker image (via JIB)
mvn jib:dockerBuild

# Docker Compose
docker-compose up
```

## Architecture

**Stack**: Java 17, Spring Boot 3.4.2, Spring Security 6, Spring Data JPA, H2 (file-based at `./data/rdpportal`), Thymeleaf (login page only).

**Package structure** (`com.rdpportal`):
- `config/` — Security (form login, BCrypt, cookie CSRF, role-based access), AES-256/GCM encryption for RDP passwords, default admin user initialization
- `controller/` — `AuthController` (login page), `DashboardController` (user APIs), `RdpController` (RDP file download), `AdminApiController` (CRUD for users/machines/assignments)
- `service/` — `UserService`, `MachineService`, `RdpFileService`, `CustomUserDetailsService`
- `model/` — `User`, `Machine`, `UserMachineAssignment`, `Role` enum (USER, ADMIN)
- `dto/` — API request/response objects (UserDto, MachineDto, ConnectionDto, AssignmentDto, CreateUserRequest, CreateMachineRequest)
- `repository/` — Spring Data JPA interfaces

**Key data relationship**: User ←(many)→ UserMachineAssignment ←(many)→ Machine. Assignments hold RDP credentials (username, domain, encrypted password).

**API authorization**:
- `/api/admin/**` — ROLE_ADMIN only
- `/api/machines`, `/api/me`, `/api/assignments/{id}/rdp` — any authenticated user
- `/login`, `/css/**`, `/js/**` — public

**Frontend**: Self-contained HTML files with inline JS/CSS — `static/index.html` (user dashboard), `static/admin.html` (admin panel), `templates/login.html` (Thymeleaf). `static/js/api.js` provides a fetch wrapper with CSRF token handling.

## Key Patterns

- **Password encryption**: RDP passwords use `@Convert(converter = EncryptedStringConverter.class)` with AES-256/GCM. The encryption key is in `application.yml`.
- **RDP file generation**: `RdpFileService` generates either a plain `.rdp` file or a `.bat` launcher (when credentials include a password) that uses `cmdkey` to temporarily store credentials.
- **Schema management**: Hibernate `ddl-auto: update` — schema is auto-managed, no migration files.
- **Default admin**: `DataInitializer` creates an `admin`/`admin` user on startup if none exists.
- **Docker profile**: `application-docker.yml` disables H2 console; activated via `docker` Spring profile.
