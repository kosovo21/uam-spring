# User Access Management (UAM) - Spring Boot JWT Authentication Service

A secure, production-ready REST API for user authentication and management built with Spring Boot, Kotlin, and JWT (JSON Web Tokens).

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [API Documentation (Swagger)](#api-documentation-swagger)
- [Configuration](#configuration)
- [API Endpoints](#api-endpoints)
- [Security Features](#security-features)
- [Project Structure](#project-structure)
- [Usage Examples](#usage-examples)
- [Development](#development)

## 🎯 Overview

UAM is a comprehensive user authentication and management service that provides secure JWT-based authentication. It follows Spring Boot best practices and implements industry-standard security measures including password hashing with Argon2, token validation, and proper error handling.

## ✨ Features

- **JWT Authentication**: Secure token-based authentication
- **User Registration**: Create new user accounts with email validation
- **User Management**: Retrieve user information (password-protected)
- **Password Security**: Argon2PasswordEncoder for secure password hashing
- **Input Validation**: Email and password validation with proper error messages
- **Exception Handling**: Comprehensive error handling with appropriate HTTP status codes
- **CORS Support**: Configured for frontend integration
- **Audit Trail**: Automatic timestamp tracking (createdAt, updatedAt)
- **Role-Based**: User roles support (extensible for future RBAC)
- **API Documentation**: Interactive Swagger/OpenAPI documentation

## 🛠 Technology Stack

- **Language**: Kotlin 1.9.25
- **Framework**: Spring Boot 3.5.7
- **Java Version**: 21
- **Database**: PostgreSQL
- **Security**: Spring Security with JWT
- **Password Encoding**: Argon2PasswordEncoder
- **JWT Library**: jjwt 0.13.0
- **API Documentation**: SpringDoc OpenAPI 2.3.0 (Swagger UI)
- **Build Tool**: Gradle (Kotlin DSL)

## 📦 Prerequisites

- Java 21 or higher
- PostgreSQL 12+ (or Docker with PostgreSQL)
- Gradle 7.6+ (or use Gradle Wrapper)

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd uam-spring
```

### 2. Database Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE uam;
CREATE USER myuser WITH PASSWORD 'mypassword';
GRANT ALL PRIVILEGES ON DATABASE uam TO myuser;
```

Or use Docker Compose (if configured):

```bash
docker-compose up -d
```

### 3. Configuration

Update `src/main/resources/application.properties` with your database credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/uam
spring.datasource.username=myuser
spring.datasource.password=mypassword
```

**Important**: Set the `JWT_SECRET` environment variable in production:

```bash
export JWT_SECRET=your-very-long-and-secure-secret-key-at-least-64-characters
```

### 4. Build and Run

Using Gradle Wrapper:

```bash
# Windows
.\gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

Or build and run the JAR:

```bash
./gradlew build
java -jar build/libs/uam-0.0.1-SNAPSHOT.jar
```

The service will start on `http://localhost:8080/uam` (context path: `/uam`)

## 📚 API Documentation (Swagger)

The API documentation is automatically generated using SpringDoc OpenAPI (Swagger UI) and is accessible via your web browser.

### Access Swagger UI

Once the application is running, navigate to:

**Swagger UI:** `http://localhost:8080/uam/swagger-ui.html`

**OpenAPI JSON:** `http://localhost:8080/uam/api-docs`

**OpenAPI YAML:** `http://localhost:8080/uam/api-docs.yaml`

### Features

- **Interactive API Testing**: Test all endpoints directly from the browser
- **JWT Authentication Support**: Use the "Authorize" button to add your JWT token
- **Request/Response Examples**: See example requests and responses for each endpoint
- **Schema Documentation**: View detailed data models and validation rules
- **Try It Out**: Execute API calls directly from the Swagger UI

### Using JWT Authentication in Swagger

1. First, register a user or login to get a JWT token
2. Click the **"Authorize"** button at the top of the Swagger UI
3. Enter your token in the format: `Bearer <your-token>` or just `<your-token>`
4. Click **"Authorize"** and then **"Close"**
5. Now you can test protected endpoints directly from Swagger UI

### Swagger UI Screenshots

The Swagger UI provides:
- Complete API endpoint documentation
- Request/response schemas
- Authentication configuration
- Interactive testing interface

**Note:** Swagger UI endpoints are publicly accessible (no authentication required) for development purposes. In production, you may want to restrict access to these endpoints.

## ⚙️ Configuration

### Application Properties

| Property | Description | Default |
|----------|-------------|---------|
| `jwt.secret` | JWT signing secret (use env var in production) | Default dev secret |
| `jwt.expiration` | Token expiration in milliseconds | 3600000 (1 hour) |
| `spring.jpa.hibernate.ddl-auto` | Database schema management | update |
| `server.servlet.context-path` | Application context path | /uam |
| `springdoc.api-docs.path` | OpenAPI JSON endpoint path | /api-docs |
| `springdoc.swagger-ui.path` | Swagger UI endpoint path | /swagger-ui.html |

### Environment Variables

- `JWT_SECRET`: Secret key for JWT signing (required in production)
- `JWT_EXPIRATION`: Token expiration time in milliseconds (optional)

## 📡 API Endpoints

### Authentication

#### Register User
```http
POST /uam/user
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:** `201 Created`
```json
{
  "email": "user@example.com",
  "roles": "USER",
  "createdAt": "2024-01-01T12:00:00",
  "updatedAt": "2024-01-01T12:00:00"
}
```

#### Login
```http
POST /uam/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### User Management (Requires Authentication)

#### Get All Users
```http
GET /uam/user
Authorization: Bearer <token>
```

**Response:** `200 OK`
```json
[
  {
    "email": "user@example.com",
    "roles": "USER",
    "createdAt": "2024-01-01T12:00:00",
    "updatedAt": "2024-01-01T12:00:00"
  }
]
```

#### Get User by Email
```http
GET /uam/user/{email}
Authorization: Bearer <token>
```

**Response:** `200 OK`
```json
{
  "email": "user@example.com",
  "roles": "USER",
  "createdAt": "2024-01-01T12:00:00",
  "updatedAt": "2024-01-01T12:00:00"
}
```

### Error Responses

All endpoints return structured error responses:

```json
{
  "message": "Error description",
  "status": 400,
  "timestamp": 1704110400000
}
```

**Common Status Codes:**
- `200 OK` - Success
- `201 Created` - Resource created
- `400 Bad Request` - Validation error
- `401 Unauthorized` - Invalid credentials or missing token
- `404 Not Found` - User not found
- `409 Conflict` - User already exists
- `500 Internal Server Error` - Server error

## 🔒 Security Features

### Authentication
- **JWT Tokens**: Stateless authentication using JSON Web Tokens
- **Token Expiration**: Configurable token expiration (default: 1 hour)
- **Token Validation**: Comprehensive validation including expiration, signature, and format checks

### Password Security
- **Argon2 Hashing**: Industry-standard Argon2PasswordEncoder
- **No Password Exposure**: Passwords are never returned in API responses
- **Password Requirements**: Minimum 8 characters (configurable)

### Input Validation
- **Email Validation**: RFC-compliant email format validation
- **Password Strength**: Minimum length enforcement
- **Request Validation**: All inputs validated before processing

### Security Headers
- **CORS Configuration**: Configurable CORS for frontend integration
- **CSRF Protection**: Disabled for stateless API (can be enabled if needed)

## 📁 Project Structure

```
src/main/kotlin/io/github/kosovo21/uam/
├── config/
│   ├── JwtAuthFilter.kt          # JWT authentication filter
│   └── SecurityConfig.kt         # Spring Security configuration
├── controller/
│   ├── AuthController.kt         # Authentication endpoints
│   └── UserController.kt         # User management endpoints
├── dto/
│   └── Login.kt                  # Data Transfer Objects
├── entity/
│   └── User.kt                   # User entity (JPA)
├── exception/
│   ├── GlobalExceptionHandler.kt # Global exception handling
│   ├── InvalidCredentialsException.kt
│   ├── UserAlreadyExistsException.kt
│   └── UserNotFoundException.kt
├── repository/
│   └── UserRepository.kt         # Data access layer
├── service/
│   ├── AuthService.kt            # Authentication logic
│   ├── JwtService.kt             # JWT token operations
│   └── UserService.kt            # User management logic
└── UamApplication.kt             # Main application class
```

## 💡 Usage Examples

### Using cURL

#### Register a User
```bash
curl -X POST http://localhost:8080/uam/user \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "securepass123"
  }'
```

#### Login
```bash
curl -X POST http://localhost:8080/uam/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "securepass123"
  }'
```

#### Get All Users (with token)
```bash
curl -X GET http://localhost:8080/uam/user \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Using JavaScript (Fetch API)

```javascript
// Register
const register = async (email, password) => {
  const response = await fetch('http://localhost:8080/uam/user', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password })
  });
  return response.json();
};

// Login
const login = async (email, password) => {
  const response = await fetch('http://localhost:8080/uam/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password })
  });
  const data = await response.json();
  localStorage.setItem('token', data.token);
  return data;
};

// Get Users
const getUsers = async () => {
  const token = localStorage.getItem('token');
  const response = await fetch('http://localhost:8080/uam/user', {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  return response.json();
};
```

## 🔧 Development

### Running Tests

```bash
./gradlew test
```

### Building the Project

```bash
./gradlew build
```

### Development Mode

The project includes Spring Boot DevTools for hot reloading during development.

### Database Schema

The database schema is automatically managed by Hibernate (`ddl-auto=update`). The `users` table includes:
- `email` (Primary Key)
- `password` (hashed)
- `roles`
- `created_at`
- `updated_at`

## 📝 Notes

- **Production Deployment**: 
  - Set `JWT_SECRET` environment variable
  - Update CORS origins in `SecurityConfig.kt`
  - Change `spring.jpa.hibernate.ddl-auto` to `validate` or use Flyway/Liquibase
  - Use strong database credentials
  - Enable HTTPS

- **Token Management**: 
  - Tokens expire after 1 hour by default
  - Store tokens securely on the client side
  - Implement refresh token mechanism for production use

- **Password Policy**: 
  - Currently enforces minimum 8 characters
  - Can be extended with additional validation rules

## 📄 License

[Specify your license here]

## 👥 Contributors

[Add contributors here]

---

**Built with ❤️ using Spring Boot and Kotlin**

