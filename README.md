# Healthcare Application API

A comprehensive Spring Boot application providing REST APIs for healthcare management and user data. The application
includes secure endpoints for patient management and public endpoints for user data retrieved from JSONPlaceholder.
It also integrates with the Healthcare Utility library for event processing and audit logging.

## Tech Stack

- **Java 21**: Latest LTS version
- **Spring Boot 3.3.0**: Core framework for building the application
- **Spring Security**: For authentication and authorization
- **Spring Data JPA**: For data persistence
- **Spring Cache**: For caching external API responses
- **Spring REST Client**: For communicating with external APIs (Spring Boot 3.2+)
- **Lombok**: For reducing boilerplate code
- **Springdoc OpenAPI 2.5.0**: For API documentation
- **PostgreSQL**: For database (configured in production)
- **Healthcare Utility Library**: For event processing and audit logging
- **Apache Kafka**: For event-driven architecture

## Project Structure

```
src/main/java/com/healthcare/app/
├── config/                  # Configuration classes
│   ├── CacheConfig.java     # Cache configuration
│   ├── OpenAPIConfig.java   # API documentation configuration
│   ├── RestClientConfig.java # REST client configuration
│   └── SecurityConfig.java  # Security configuration
├── controller/              # REST controllers
│   ├── PatientController.java # Secured endpoints for patient management
│   └── UserController.java  # Public endpoints for user data
├── exception/               # Exception handling
│   ├── ErrorDetails.java    # Error response model
│   ├── GlobalExceptionHandler.java # Centralized exception handling
│   └── ResourceNotFoundException.java # Custom exception
├── model/                   # Data models
│   ├── Patient.java         # Patient entity
│   └── User.java            # User record (from JSONPlaceholder)
├── repository/              # Data access layer
│   └── PatientRepository.java # Patient repository
├── service/                 # Business logic
│   ├── PatientService.java  # Patient service
│   └── UserService.java     # User service with caching
└── HealthcareApplication.java # Main application class
```

## Features

### Patient Management (Secured Endpoints)

- **GET /api/v1/patients**: Get all patients (USER/ADMIN roles)
- **GET /api/v1/patients/{id}**: Get patient by ID (USER/ADMIN roles)
- **POST /api/v1/patients**: Create a new patient (USER/ADMIN roles)
- **PUT /api/v1/patients/{id}**: Update a patient (ADMIN role only)
- **DELETE /api/v1/patients/{id}**: Delete a patient (ADMIN role only)

### User Management (Public Endpoints)

- **GET /api/v1/users**: Get all users from JSONPlaceholder
- **GET /api/v1/users/{id}**: Get user by ID from JSONPlaceholder
- **POST /api/v1/users**: Create a new user via JSONPlaceholder
- **PUT /api/v1/users/{id}**: Update a user via JSONPlaceholder
- **DELETE /api/v1/users/{id}**: Delete a user via JSONPlaceholder

## Running the Application

```sh
# Clone the repository
git clone https://github.com/aarthi-a13/first-app-27527206.git
cd first-app-27527206

# Build the application
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on port 8080 by default.

## Testing the APIs

### Swagger UI

The easiest way to test the APIs is using the Swagger UI available at:

```
http://localhost:8080/swagger-ui.html
```

### Using cURL

#### Get all users

```sh
curl -X GET http://localhost:8080/api/v1/users
```

#### Get user by ID

```sh
curl -X GET http://localhost:8080/api/v1/users/1
```

#### Create a new user

```sh
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "username": "johndoe",
    "email": "john@example.com",
    "phone": "1-234-567-8901",
    "website": "johndoe.com"
  }'
```

#### Update a user

```sh
curl -X PUT http://localhost:8080/api/v1/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe Updated",
    "username": "johndoe",
    "email": "john@example.com",
    "phone": "1-234-567-8901",
    "website": "johndoe.com"
  }'
```

#### Delete a user

```sh
curl -X DELETE http://localhost:8080/api/v1/users/1
```

## Caching Behavior

The application implements caching for the JSONPlaceholder API calls:

- **GET requests**: Cached to avoid redundant API calls
- **POST/PUT/DELETE requests**: Invalidate or update the cache as appropriate

This improves performance significantly since the JSONPlaceholder data is static.

## Healthcare Utility Integration

This application integrates with the Healthcare Utility library to provide event processing and audit logging capabilities:

### Event Processing

- **User Events**: The application sends user-related events (CREATED, UPDATED, DELETED) to Kafka using the EventProducerService from the utility library
- **Event Mapping**: The application maps its User model to the utility library's User model using utility methods
- **Audit Trail**: All user operations are automatically logged to the audit system via the Kafka consumer in the utility library

### Benefits of Integration

- **Centralized Audit Logging**: All user activities are automatically captured in a centralized audit system
- **Event-Driven Architecture**: Enables other services to react to user-related events
- **Separation of Concerns**: Core business logic is separated from cross-cutting concerns like auditing

### Configuration

To enable the Healthcare Utility integration, ensure the following configuration is in your application.properties or application.yml:

```properties
# Kafka Configuration for Healthcare Utility
spring.kafka.producer.bootstrap-servers=localhost:9092
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer

# Topic Configuration
user.event.topic=user-topic
```
