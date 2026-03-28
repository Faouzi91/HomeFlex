# HomeFlex

HomeFlex is a comprehensive real estate rental application featuring a modern Angular frontend and a robust Spring Boot backend.

## Architecture

- **Frontend**: Angular 17, Ionic, TailwindCSS
- **Backend**: Spring Boot 3, Java 21, PostgreSQL
- **Mobile**: Capacitor (Android & iOS)

## Getting Started (Docker)

The easiest way to run the application locally is using Docker Compose.

### Prerequisites

- Docker & Docker Compose installed

### Run

1.  Clone the repository.
2.  Run the following command in the root directory:
    ```bash
    docker-compose up --build
    ```
3.  Access the application:
    - **Frontend**: [http://localhost](http://localhost)
    - **Backend API**: [http://localhost:8080](http://localhost:8080)
    - **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Development

- **Frontend**: located in `rental-app-frontend`. Run `ng serve` to start locally (default port 4200).
- **Backend**: located in `rental-backend`. Run `mvn spring-boot:run` to start locally (default port 8080).

Note: Ensure you have a local PostgreSQL instance running if you choose to run without Docker.
