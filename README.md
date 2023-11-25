# Spring Boot RESTful API

This is a Java-based project using Spring Boot and Maven. The project is structured around a RESTful API and includes JWT authentication and custom response body advice.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

- Java 17 or higher
- Maven
- IntelliJ IDEA 2023.2.5 or any preferred IDE
- Docker Engine 20.10.8 or higher

### Installing

1. Clone the repository
2. Open the project in your IDE
3. Run `mvn clean install` to build the project

## Running the tests

Explain how to run the automated tests for this system.

## Built With

- [Java](https://www.java.com/) - The programming language used
- [Spring Boot](https://spring.io/projects/spring-boot) - The framework used
- [Maven](https://maven.apache.org/) - Dependency Management

## Authors

- CHORN Thorn

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

## Project Structure (tree)

```bash
├── LICENSE
├── README.md
├── docker-compose.yaml
├── mvnw
├── mvnw.cmd
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── khodedev
    │   │           └── app
    │   │               ├── Application.java
    │   │               ├── auth
    │   │               │   ├── AuthController.java
    │   │               │   ├── AuthService.java
    │   │               │   └── dto
    │   │               │       ├── LoginDto.java
    │   │               │       ├── LoginResDto.java
    │   │               │       └── RegisterDto.java
    │   │               ├── common
    │   │               │   ├── advice
    │   │               │   │   └── CustomResponseBodyAdvice.java
    │   │               │   ├── annotations
    │   │               │   │   ├── PreAuthz.java
    │   │               │   │   ├── Public.java
    │   │               │   │   └── SkipResponseWrapper.java
    │   │               │   ├── config
    │   │               │   │   ├── ApplicationConfig.java
    │   │               │   │   └── SecurityConfig.java
    │   │               │   ├── constants
    │   │               │   │   └── Constants.java
    │   │               │   ├── controllers
    │   │               │   │   └── CustomErrorController.java
    │   │               │   ├── exceptions
    │   │               │   │   ├── BadRequestException.java
    │   │               │   │   ├── ForbiddenException.java
    │   │               │   │   ├── GlobalExceptionHandler.java
    │   │               │   │   ├── InternalServerErrorException.java
    │   │               │   │   ├── NotFoundException.java
    │   │               │   │   ├── UnauthorizedException.java
    │   │               │   │   └── UnprocessableEntityException.java
    │   │               │   ├── filters
    │   │               │   │   ├── JwtAuthenticationFilter.java
    │   │               │   │   ├── PreAuthzFilter.java
    │   │               │   │   └── PublicAccessFilter.java
    │   │               │   ├── services
    │   │               │   │   ├── JwtTokenValidator.java
    │   │               │   │   └── KeycloakAuthorizationService.java
    │   │               │   └── types
    │   │               │       ├── ErrorResponse.java
    │   │               │       ├── ResponseWrapper.java
    │   │               │       └── Scope.java
    │   │               └── user
    │   │                   ├── UserController.java
    │   │                   ├── UserRepository.java
    │   │                   ├── UserService.java
    │   │                   ├── dto
    │   │                   │   ├── CreateUserDto.java
    │   │                   │   └── UpdateUserDto.java
    │   │                   └── entities
    │   │                       └── User.java
    │   └── resources
    │       └── application.properties
    └── test
        └── java
            └── com
                └── khodedev
                    └── app
                        └── ApplicationTests.java

```