package com.astrobookings.rockets.api;

import com.astrobookings.rockets.domain.Rocket;
import com.astrobookings.rockets.service.RocketService;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rockets")
public class RocketController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RocketController.class);

    private final RocketService service;

    public RocketController(RocketService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Rocket create(@Valid @RequestBody RocketRequest request) {
        LOGGER.info("POST /rockets - creating rocket name={}, range={}, capacity={}",
                request.name(), request.range(), request.capacity());
        return service.create(request);
    }

    @GetMapping("/{id}")
    public Rocket getById(@PathVariable Long id) {
        LOGGER.info("GET /rockets/{} - fetching rocket", id);
        return service.getById(id);
    }

    @GetMapping
    public List<Rocket> getAll() {
        LOGGER.info("GET /rockets - fetching all rockets");
        return service.getAll();
    }

    @PutMapping("/{id}")
    public Rocket update(@PathVariable Long id, @Valid @RequestBody RocketRequest request) {
        LOGGER.info("PUT /rockets/{} - updating rocket with name={}, range={}, capacity={}",
                id, request.name(), request.range(), request.capacity());
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        LOGGER.info("DELETE /rockets/{} - deleting rocket", id);
        service.delete(id);
    }
}
