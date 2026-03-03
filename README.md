# AstroBookings Rockets API

Spring Boot service for managing rocket records used by AstroBookings.

This is a simple example to test SKILLs.

## Requirements
- Java 17
- Maven 3.9+

## Run
```bash
mvn spring-boot:run
```

## Test
```bash
mvn test
```

## Rocket API
Base path: `/rockets`

- `POST /rockets`: create rocket
- `GET /rockets`: list rockets
- `GET /rockets/{id}`: get rocket by id
- `PUT /rockets/{id}`: update rocket
- `DELETE /rockets/{id}`: delete rocket

### Payload
```json
{
  "name": "Orion",
  "range": "orbital",
  "capacity": 4
}
```

### Validation rules
- `name`: required, non-blank
- `range`: one of `suborbital`, `orbital`, `moon`, `mars`
- `capacity`: integer between 1 and 10
