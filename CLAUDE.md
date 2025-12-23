# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot 3.4.12 cloud file management system with JWT authentication, RBAC authorization, and Tencent Cloud COS integration. The project uses a multi-module Maven architecture with three core modules: `common` (shared utilities), `core` (main application), and `secruity` (security framework - note the typo in module name).

**Key Technologies**: Spring Boot 3, Spring Security, MyBatis-Plus, MySQL 8.0, Redis, Caffeine Cache, Tencent Cloud COS, Java-JWT 4.4.0

## Build and Run Commands

### Build the project
```bash
mvn clean install
```

### Run the application (from core module)
```bash
cd core
mvn spring-boot:run
```

Or run the packaged JAR:
```bash
java -jar core/target/core-0.0.1-SNAPSHOT.jar
```

### Run tests
```bash
mvn test
```

### Run single test class
```bash
mvn test -Dtest=YourTestClassName
```

### Database initialization
```bash
cd sql
init_database.bat  # Windows only
```

Or manually execute SQL files in order: 01-06 table creation scripts, then `init_data.sql`

## Architecture and Code Structure

### Multi-Module Organization

- **common**: Shared library (NO Spring Boot packaging)
  - Base entities, exceptions, annotations
  - Cache managers (hybrid Redis/Caffeine)
  - Utility classes (RedisCacheUtil, etc.)

- **core**: Main executable Spring Boot application (port 1227)
  - Controllers, Services, Mappers
  - Business logic for file management and authentication
  - Application entry point: `com.tancong.core.Application`

- **secruity** (note typo): Security library
  - JWT token service and filters
  - Spring Security configuration
  - Authentication handlers (401/403)

### Authentication Flow

The system uses **stateless JWT authentication** (NO sessions):

1. **Login**: POST `/auth/login` with username/password
   - Returns JWT token valid for 30 minutes
   - Token stored in Redis (or Caffeine if Redis unavailable)
   - Response format: `Bearer {token}`

2. **Request Authentication**:
   - All requests include `Authorization: Bearer {token}` header
   - `JwtAuthenticationTokenFilter` intercepts before UsernamePasswordAuthenticationFilter
   - Token validated via `BaseTokenService.verifyToken()`
   - User loaded from cache and placed in SecurityContext

3. **Token Configuration**: Located in `security.yml`
   - Secret key: `my-super-secret-key-12345678901234`
   - Expiration: 30 minutes
   - Algorithm: HMAC256

### File Management System

**Upload Flow**:
- POST `/files/upload` with MultipartFile (max 100MB)
- MD5 hash calculated for deduplication
- "Quick upload" (秒传) if file already exists
- Otherwise uploads to Tencent Cloud COS
- Metadata stored in database with user_id isolation

**Key Operations**:
- Download: `/files/{id}/download` - generates 1-hour time-limited COS URL
- Rename: `/files/{id}/rename`
- Move: `/files/{id}/move` - changes folder_id
- Soft delete: `/files/{id}` - sets status=0
- Permanent delete: `/files/{id}/permanent` - removes from DB and COS
- Folder tree: `/files/folders/{id}/tree` - uses MySQL 8.0 WITH RECURSIVE CTE

**File Hierarchy**:
- Files support parent-child relationships via `folder_id`
- Root files have `folder_id = 0`
- Recursive operations use Common Table Expressions (CTE)

### Database Access Patterns

**MyBatis-Plus Configuration**:
- XML mappers: `core/src/main/resources/mapper/*.xml`
- Type aliases: `com.tancong.**.entity`
- Camel case mapping enabled
- Lazy loading enabled

**Key Custom Queries** (FileMapper.xml):
- `selectAllFilesRecursively`: WITH RECURSIVE CTE for folder traversal
- `softDeleteFileAndChildren`: Recursive soft delete
- `permanentDeleteFileAndChildren`: Recursive physical deletion
- All queries filtered by `user_id` for user isolation

**Database Tables**:
- `user`: Stores BCrypt-hashed passwords, UUID identifiers
- `role`: RBAC roles (ROLE_ADMIN, ROLE_GUEST)
- `menu`: Tree structure with parent_menu_id self-reference
- `user_role`, `role_menu`: Many-to-many relationships with CASCADE DELETE
- `log`: Operation audit trail via @LogRecord AOP
- `file`: File metadata with MD5 hash, COS path, folder hierarchy

### Caching Strategy

**Hybrid Cache Manager** (`CacheManagers.java`):
- Automatically selects Redis (preferred) or Caffeine (fallback)
- Used for JWT tokens and user session data
- Graceful degradation when Redis unavailable

### Cross-Cutting Concerns

**Logging and Auditing**:
- `@LogRecord` annotation triggers AOP aspect
- Captures operation details, execution time, client IP, user ID
- Persists to `log` table via LoggerService
- Automatic exception logging

**Exception Handling**:
- Global `@RestControllerAdvice` handler
- `CanShowException` for user-friendly error messages
- Standardized response format: `RespBody<T>` with code/msg/data

**Security Handlers**:
- `AuthenticationEntryPointImpl`: 401 responses (unauthenticated)
- `AccessDeniedHandlerImpl`: 403 responses (unauthorized)
- Custom JSON responses instead of HTML error pages

## Important Configuration Details

### Application Configuration (application.yml)
- Server port: **1227**
- Database: `jdbc:mysql://101.42.242.33:3306/cloud_db`
- Redis: `101.42.242.33:6378`
- Max file size: **100MB**

### COS Configuration
Environment variables required:
- `COS_SECRET_ID`
- `COS_SECRET_KEY`
- `COS_REGION`
- `COS_BUCKET_NAME`

COS URLs expire after **3600 seconds** (1 hour)

### Default User Accounts
- Admin: username `admin`, password `admin123`, role ROLE_ADMIN
- Guest: username `guest`, password `123456`, role ROLE_GUEST
- Passwords hashed with BCrypt (rounds=10)

## Key Design Patterns

1. **User Isolation**: All queries filtered by authenticated user's ID
2. **Soft Deletes**: Status field instead of physical deletion (preserves audit trail)
3. **File Deduplication**: MD5-based quick upload to avoid redundant storage
4. **Recursive Queries**: MySQL 8.0 CTE for hierarchical folder structures
5. **Stateless API**: No session storage, JWT in request headers
6. **AOP-Based Auditing**: Declarative logging via annotations
7. **Configuration-Driven**: Environment variables for sensitive credentials

## Module Dependencies

When working with modules:
- **common** and **secruity** are libraries (packaging: jar, repackage disabled)
- **core** depends on both common and secruity
- Only **core** produces executable JAR
- Lombok annotation processor configured for all modules

## Security Notes

- CSRF disabled (stateless API)
- Session creation policy: STATELESS
- Form login disabled (JWT only)
- BCrypt for password hashing
- AES encryption service available (key: `tancong629zyr218`)
- Optional `@Decrypt` annotation for request body decryption

## Common Development Tasks

### Adding a new file operation endpoint
1. Add method to `FileController` with appropriate mapping
2. Implement business logic in `FileService/FileServiceImpl`
3. Add custom SQL query to `FileMapper.xml` if needed (especially for recursive operations)
4. Ensure user_id filtering for security
5. Consider adding `@LogRecord` for audit trail

### Modifying authentication/authorization
1. Security config in `secruity/src/main/java/com/tancong/security/config/SecurityConfig.java`
2. Token settings in `security.yml`
3. JWT filter: `JwtAuthenticationTokenFilter`
4. Token service: `BaseTokenService`

### Database schema changes
1. Create/modify SQL scripts in `sql/` directory
2. Update corresponding entity in `core/src/main/java/com/tancong/core/entity/`
3. Update mapper XML if custom queries needed
4. Consider migration impact on existing data

### Working with file types and statuses
Use enums located in `core/src/main/java/com/tancong/core/entity/enums/`:
- `FileTypeEnum`: FILE (1), FOLDER (2)
- File status: DELETED (0), NORMAL (1), PENDING (2)

## Notes on Code Quality

- The module name "secruity" is intentionally misspelled throughout the codebase
- Do not rename it without updating all Maven dependencies
- All user-facing error messages should use `CanShowException` for consistency
- Always include user_id in queries to maintain multi-tenant data isolation
