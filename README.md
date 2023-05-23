# GitHub Repository API

This project is an API that allows users to retrieve information about GitHub repositories based on a user's username. It provides a simple and straightforward way to list all non-forked repositories for a given user, including branch details.

## Features

- Retrieve a list of non-forked repositories for a GitHub user.
- Get repository information, including repository name, owner login, and branch details.
- Return appropriate status codes (404 and 406) for error scenarios.

## Prerequisites

Before running the application, make sure you have the following prerequisites installed:
* Java 17
* Docker (optional, if you want to run the application in a Docker container)

## Installation

1. Clone the repository:
```bash
git clone https://github.com/your-username/github-repo-api.git
```
2. Build the project
```bash
./mvnw clean install
```

3. After the build is complete, you can run the application in two ways: using Maven or a Docker container.

### Running with Maven
```bash
./mvnw spring-boot:run
```

### Running with Docker (optional)

1. Build the Docker image using the following command:
```bash
docker build -t my-application .
```

2. Run the Docker container using the following command:
```bash
 docker run -p 8080:8080 my-application
```

3. Once the application is running, you can access it in your browser using the following URL:
```bash
 http://localhost:8080
```
## Usage

Retrieve repositories for a user (replace {username} with the GitHub username): 
```bash
    curl -H "Accept: application/json" http://localhost:8080/repositories/{username} 
  ```

## API Specification

For detailed information about the API endpoints, request format, and response format, please refer to the Swagger YAML file.

## Testing
To run the unit tests, use the following command:
```bash
    mvn test 
  ```
