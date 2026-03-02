package com.astrobookings.rockets;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RocketApiE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createWithValidPayloadStoresAndReturnsRocket() {
        ResponseEntity<RocketResponse> response = restTemplate.postForEntity(
                "/rockets",
                validRequest("Orion", "orbital", 4),
                RocketResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("Orion");
        assertThat(response.getBody().range()).isEqualTo("ORBITAL");
        assertThat(response.getBody().capacity()).isEqualTo(4);
    }

    @Test
    void missingNameOnCreateOrUpdateReturnsValidationError() {
        ResponseEntity<ErrorResponse> createResponse = restTemplate.postForEntity(
                "/rockets",
                Map.of("range", "orbital", "capacity", 3),
                ErrorResponse.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().errors()).contains("name is required");

        Long id = createRocket("Nova", "moon", 5);
        ResponseEntity<ErrorResponse> updateResponse = restTemplate.exchange(
                "/rockets/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(Map.of("range", "moon", "capacity", 5)),
                ErrorResponse.class
        );
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(updateResponse.getBody()).isNotNull();
        assertThat(updateResponse.getBody().errors()).contains("name is required");
    }

    @Test
    void invalidRangeOnCreateOrUpdateReturnsValidationError() {
        ResponseEntity<ErrorResponse> createResponse = restTemplate.postForEntity(
                "/rockets",
                validRequest("Astra", "interstellar", 3),
                ErrorResponse.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().errors()).contains("range must be one of: suborbital, orbital, moon, mars");

        Long id = createRocket("Nova", "moon", 5);
        ResponseEntity<ErrorResponse> updateResponse = restTemplate.exchange(
                "/rockets/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(validRequest("Nova", "interstellar", 5)),
                ErrorResponse.class
        );
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(updateResponse.getBody()).isNotNull();
        assertThat(updateResponse.getBody().errors()).contains("range must be one of: suborbital, orbital, moon, mars");
    }

    @Test
    void invalidCapacityOnCreateOrUpdateReturnsValidationError() {
        ResponseEntity<ErrorResponse> createResponse = restTemplate.postForEntity(
                "/rockets",
                validRequest("Astra", "suborbital", 0),
                ErrorResponse.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().errors()).contains("capacity must be between 1 and 10");

        Long id = createRocket("Nova", "moon", 5);
        ResponseEntity<ErrorResponse> updateResponse = restTemplate.exchange(
                "/rockets/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(validRequest("Nova", "moon", 11)),
                ErrorResponse.class
        );
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(updateResponse.getBody()).isNotNull();
        assertThat(updateResponse.getBody().errors()).contains("capacity must be between 1 and 10");
    }

    @Test
    void getByIdReturnsRocketDetailsWhenRocketExists() {
        Long id = createRocket("Voyager", "mars", 6);

        ResponseEntity<RocketResponse> response = restTemplate.getForEntity("/rockets/" + id, RocketResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(id);
        assertThat(response.getBody().name()).isEqualTo("Voyager");
        assertThat(response.getBody().range()).isEqualTo("MARS");
        assertThat(response.getBody().capacity()).isEqualTo(6);
    }

    @Test
    void readUpdateAndDeleteReturnNotFoundWhenRocketDoesNotExist() {
        ResponseEntity<ErrorResponse> readResponse = restTemplate.getForEntity("/rockets/999", ErrorResponse.class);
        assertThat(readResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(readResponse.getBody()).isNotNull();
        assertThat(readResponse.getBody().errors()).contains("rocket not found with id 999");

        ResponseEntity<ErrorResponse> updateResponse = restTemplate.exchange(
                "/rockets/999",
                HttpMethod.PUT,
                new HttpEntity<>(validRequest("Phantom", "moon", 2)),
                ErrorResponse.class
        );
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(updateResponse.getBody()).isNotNull();
        assertThat(updateResponse.getBody().errors()).contains("rocket not found with id 999");

        ResponseEntity<ErrorResponse> deleteResponse = restTemplate.exchange(
                "/rockets/999",
                HttpMethod.DELETE,
                null,
                ErrorResponse.class
        );
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(deleteResponse.getBody()).isNotNull();
        assertThat(deleteResponse.getBody().errors()).contains("rocket not found with id 999");
    }

    @Test
    void updateWithValidPayloadPersistsAndReturnsUpdatedRocket() {
        Long id = createRocket("Pioneer", "suborbital", 2);

        ResponseEntity<RocketResponse> response = restTemplate.exchange(
                "/rockets/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(validRequest("Pioneer X", "orbital", 8)),
                RocketResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(id);
        assertThat(response.getBody().name()).isEqualTo("Pioneer X");
        assertThat(response.getBody().range()).isEqualTo("ORBITAL");
        assertThat(response.getBody().capacity()).isEqualTo(8);
    }

    @Test
    void listEndpointReturnsAllStoredRockets() {
        Long firstId = createRocket("Atlas", "suborbital", 2);
        Long secondId = createRocket("Luna", "moon", 4);

        ResponseEntity<RocketResponse[]> response = restTemplate.getForEntity("/rockets", RocketResponse[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).extracting(RocketResponse::id).contains(firstId, secondId);
    }

    @Test
    void deleteRemovesExistingRocketAndReturnsNoContent() {
        Long id = createRocket("Tempest", "orbital", 3);

        ResponseEntity<Void> deleteResponse = restTemplate.exchange("/rockets/" + id, HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ErrorResponse> getResponse = restTemplate.getForEntity("/rockets/" + id, ErrorResponse.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private Long createRocket(String name, String range, int capacity) {
        ResponseEntity<RocketResponse> response = restTemplate.postForEntity(
                "/rockets",
                validRequest(name, range, capacity),
                RocketResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        return response.getBody().id();
    }

    private Map<String, Object> validRequest(String name, String range, int capacity) {
        return Map.of(
                "name", name,
                "range", range,
                "capacity", capacity
        );
    }

    private record RocketResponse(Long id, String name, String range, Integer capacity) {
    }

    private record ErrorResponse(List<String> errors) {
    }
}
