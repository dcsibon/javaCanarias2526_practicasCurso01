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

    private static final String ROCKETS_ENDPOINT = "/rockets";
    private static final long MISSING_ROCKET_ID = 999L;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createWithValidPayloadStoresAndReturnsRocket() {
        ResponseEntity<RocketResponse> response = restTemplate.postForEntity(
                ROCKETS_ENDPOINT,
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
                ROCKETS_ENDPOINT,
                Map.of("range", "orbital", "capacity", 3),
                ErrorResponse.class
        );
        assertBadRequestContains(createResponse, "name is required");

        Long id = createRocket("Nova", "moon", 5);
        ResponseEntity<ErrorResponse> updateResponse = restTemplate.exchange(
                rocketPath(id),
                HttpMethod.PUT,
                new HttpEntity<>(Map.of("range", "moon", "capacity", 5)),
                ErrorResponse.class
        );
        assertBadRequestContains(updateResponse, "name is required");
    }

    @Test
    void invalidRangeOnCreateOrUpdateReturnsValidationError() {
        ResponseEntity<ErrorResponse> createResponse = restTemplate.postForEntity(
                ROCKETS_ENDPOINT,
                validRequest("Astra", "interstellar", 3),
                ErrorResponse.class
        );
        assertBadRequestContains(createResponse, "range must be one of: suborbital, orbital, moon, mars");

        Long id = createRocket("Nova", "moon", 5);
        ResponseEntity<ErrorResponse> updateResponse = restTemplate.exchange(
                rocketPath(id),
                HttpMethod.PUT,
                new HttpEntity<>(validRequest("Nova", "interstellar", 5)),
                ErrorResponse.class
        );
        assertBadRequestContains(updateResponse, "range must be one of: suborbital, orbital, moon, mars");
    }

    @Test
    void invalidCapacityOnCreateOrUpdateReturnsValidationError() {
        ResponseEntity<ErrorResponse> createResponse = restTemplate.postForEntity(
                ROCKETS_ENDPOINT,
                validRequest("Astra", "suborbital", 0),
                ErrorResponse.class
        );
        assertBadRequestContains(createResponse, "capacity must be between 1 and 10");

        Long id = createRocket("Nova", "moon", 5);
        ResponseEntity<ErrorResponse> updateResponse = restTemplate.exchange(
                rocketPath(id),
                HttpMethod.PUT,
                new HttpEntity<>(validRequest("Nova", "moon", 11)),
                ErrorResponse.class
        );
        assertBadRequestContains(updateResponse, "capacity must be between 1 and 10");
    }

    @Test
    void getByIdReturnsRocketDetailsWhenRocketExists() {
        Long id = createRocket("Voyager", "mars", 6);

        ResponseEntity<RocketResponse> response = restTemplate.getForEntity(rocketPath(id), RocketResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(id);
        assertThat(response.getBody().name()).isEqualTo("Voyager");
        assertThat(response.getBody().range()).isEqualTo("MARS");
        assertThat(response.getBody().capacity()).isEqualTo(6);
    }

    @Test
    void readUpdateAndDeleteReturnNotFoundWhenRocketDoesNotExist() {
        ResponseEntity<ErrorResponse> readResponse = restTemplate.getForEntity(rocketPath(MISSING_ROCKET_ID), ErrorResponse.class);
        assertNotFoundContains(readResponse, "rocket not found with id 999");

        ResponseEntity<ErrorResponse> updateResponse = restTemplate.exchange(
                rocketPath(MISSING_ROCKET_ID),
                HttpMethod.PUT,
                new HttpEntity<>(validRequest("Phantom", "moon", 2)),
                ErrorResponse.class
        );
        assertNotFoundContains(updateResponse, "rocket not found with id 999");

        ResponseEntity<ErrorResponse> deleteResponse = restTemplate.exchange(
                rocketPath(MISSING_ROCKET_ID),
                HttpMethod.DELETE,
                null,
                ErrorResponse.class
        );
        assertNotFoundContains(deleteResponse, "rocket not found with id 999");
    }

    @Test
    void updateWithValidPayloadPersistsAndReturnsUpdatedRocket() {
        Long id = createRocket("Pioneer", "suborbital", 2);

        ResponseEntity<RocketResponse> response = restTemplate.exchange(
                rocketPath(id),
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

        ResponseEntity<RocketResponse[]> response = restTemplate.getForEntity(ROCKETS_ENDPOINT, RocketResponse[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).extracting(RocketResponse::id).contains(firstId, secondId);
    }

    @Test
    void deleteRemovesExistingRocketAndReturnsNoContent() {
        Long id = createRocket("Tempest", "orbital", 3);

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(rocketPath(id), HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ErrorResponse> getResponse = restTemplate.getForEntity(rocketPath(id), ErrorResponse.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private Long createRocket(String name, String range, int capacity) {
        ResponseEntity<RocketResponse> response = restTemplate.postForEntity(
                ROCKETS_ENDPOINT,
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

    private String rocketPath(Long id) {
        return ROCKETS_ENDPOINT + "/" + id;
    }

    private void assertBadRequestContains(ResponseEntity<ErrorResponse> response, String expectedError) {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().errors()).contains(expectedError);
    }

    private void assertNotFoundContains(ResponseEntity<ErrorResponse> response, String expectedError) {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().errors()).contains(expectedError);
    }

    private record RocketResponse(Long id, String name, String range, Integer capacity) {
    }

    private record ErrorResponse(List<String> errors) {
    }
}
