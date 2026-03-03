# Agents Instructions

## Product Overview
- AstroBookings Rockets API manages rocket records for booking operations.
- It exposes CRUD endpoints under `/rockets` using REST.
- It validates payload fields and returns consistent API errors.

## Technical Implementation

### Tech Stack
- Language: **Java 17**
- Framework: **Spring Boot 3.3.5**
- Database: **None (in-memory repository)**
- Security: **Input validation and global API error handling**
- Testing: **Spring Boot Test (JUnit 5)**
- Logging: **Spring Boot default logging (Logback)**

### Development workflow
```bash
# Set up the project
mvn -version

# Build/Compile the project
mvn clean compile

# Run the project
mvn spring-boot:run

# Test the project
mvn test

# Deploy the project
mvn clean package
java -jar target/rockets-api-0.1.0.jar
```

### Folder structure
```text
.                         # Project root
├── AGENTS.md             # This file with instructions for AI agents
├── README.md             # The main human documentation file
├── pom.xml               # Maven config and dependencies
├── CHANGELOG.md          # Release notes
├── prompts/              # Prompt files for project tasks
├── specs/                # Markdown specifications
├── instrucciones/        # Exercise instructions
├── src/main/java/        # Application source code
├── src/test/java/        # Test source code
└── target/               # Build output
```

## Environment
- Code and documentation must be in English.
- Chat responses must be in the language of the user prompt.
- Sacrifice grammar for conciseness in responses.
- This is a windows environment using git bash terminal.
- My default branch is `main`.
