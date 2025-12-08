# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a cloud storage backend management system (网盘后台管理系统) built with Spring Boot 3.4.12 and Java 17. It's a multi-module Maven project with three core modules:

- **common**: Shared utilities, annotations, caching, and exception handling
- **core**: Main application module with business logic, controllers, entities, and services
- **secruity** (note: typo in module name): Security configurations, JWT authentication, and encryption/decryption services

## Build & Run Commands

### Build the project
```bash
mvn clean install
```

### Run the application
```bash
# From the core module
cd core
mvn spring-boot:run
```

### Run tests
```bash
mvn test
```

### Build specific module
```bash
mvn clean install -pl common
mvn clean install -pl core
mvn clean install -pl secruity
```

### Skip tests during build
```bash
mvn clean install -DskipTests
```

## Architecture

### Module Dependencies
- **core** depends on both **common** and **secruity**
- **secruity** depends on **common**
- **common** is standalone with no internal dependencies

### Technology Stack
- Spring Boot 3.4.12 (Web, Validation, Security, Data Redis, AOP)
- MyBatis Plus 3.5.7 for ORM
- MySQL 8.0.33
- JWT (java-jwt 4.4.0) for authentication
- Redis for distributed caching (with Caffeine 3.1.8 as local fallback)
- Hutool 5.8.20 for utilities
- Swagger/OpenAPI 2.2.22 for API documentation
- Lombok 1.18.36

### Package Structure
```
com.tancong.core/
├── aspect/         # AOP aspects (logging)
├── config/         # Configuration classes (Jackson, WebMvc)
├── controller/     # REST controllers
├── entity/         # Domain entities, DTOs, VOs, enums
│   ├── dto/
│   ├── vo/
│   └── enums/
├── mapper/         # MyBatis Plus mappers
├── service/        # Service interfaces and implementations
│   └── impl/
└── utils/          # Utility classes

com.tancong.security/
├── annotation/     # @Encrypt, @Decrypt
├── aspect/         # Encryption/Decryption AOP
├── config/         # Security configuration, CORS
├── entity/         # AuthUser, ShareUser, TokenType
├── handler/        # Authentication handlers, method argument resolvers
├── service/        # BaseTokenService, EncryptService
└── utils/          # DefaultSecurityUtils

com.tancong.common/
├── annotation/     # @API, @LogRecord, @YAdmin
├── entity/         # Log, RespBody, enums
├── exception/      # CanShowException, SQLOperateException
├── service/        # LoggerService interface
└── utils/          # CacheManagers, LocalCacheUtil, RedisCacheUtil, ServletUtils
```

## Key Architectural Patterns

### Custom Annotations
- **@API**: Meta-annotation combining @RestController, @RequestMapping, and Swagger @Tag for REST controllers
- **@YAdmin**: Similar to @API but for admin console controllers, uses @Controller instead
- **@LogRecord**: Method-level annotation for automatic operation logging via AOP
- **@Encrypt/@Decrypt**: Method or parameter annotations for automatic encryption/decryption

### Caching Strategy
The system uses an abstracted caching layer (`CacheManagers`) that automatically selects between:
- Redis (primary, if configured)
- Local Caffeine cache (fallback if Redis unavailable)

Access cache via: `CacheManagers.set()`, `CacheManagers.get()`, `CacheManagers.del()`, etc.

### JWT Authentication Flow
1. JWT tokens are created via `BaseTokenService.createToken()` with UUID, TokenType, and AuthUser
2. Tokens stored in cache with expiration (configured in security.yml)
3. Token validation happens in `BaseTokenService.verifyToken()`
4. AuthUser retrieved from cache using UUID extracted from token
5. Token header: `Authorization: Bearer <token>`

### Security & Encryption
- Passwords encrypted with BCrypt (configured in SecurityConfig)
- AES encryption/decryption available via `EncryptService` (key in security.yml)
- Automatic request/response encryption using @Encrypt/@Decrypt annotations
- Security settings in `secruity/src/main/resources/security.yml`

### Logging System
- AOP-based logging via `LogAspect` for methods annotated with `@LogRecord`
- Logs captured: operation title, module, IP, user ID, method signature, parameters, results, errors, execution time
- Logs persisted via `LoggerService.insert()` (implemented by `DbLoggerServiceImpl`)

## Configuration Files

### application.yml (core module)
- Server port: 1227
- MySQL connection details
- Spring Boot configurations

### security.yml (secruity module)
- JWT configuration: header name, token prefix, secret key, expiration (30 minutes)
- AES encryption key
- **Important**: Secret keys should be externalized to environment variables in production

## Database
- MySQL connection string: `jdbc:mysql://101.42.242.33:3306/cloud_db`
- MyBatis Plus handles entity mappings
- Mappers: LogMapper, MenuMapper, RoleMapper, UserMapper

## Development Notes

### Component Scanning
The main application (`com.tancong.core.Application`) scans all packages under `com.tancong` via:
### java
@SpringBootApplication(scanBasePackages = "com.tancong")


### Entity Base Class
Most entities extend `BaseEntity` which likely contains common fields (id, create/update timestamps, etc.)

### Response Handling
Use `RespBody<T>` for standardized API responses with `RespStatus` enum for status codes.

### Exception Handling
- `CanShowException`: Exceptions safe to display to users
- `SQLOperateException`: Database operation exceptions

### Service Layer Pattern
Services extend `BaseService` for common CRUD operations. Implementations in `service/impl/`.

### YAML Configuration Loading
Custom `YamlSourceFactory` used to load .yml files via `@PropertySource` annotation.
