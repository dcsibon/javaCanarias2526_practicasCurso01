package com.astrobookings.rockets.api;

import com.astrobookings.rockets.domain.Rocket;
import com.astrobookings.rockets.service.RocketService;
import jakarta.validation.Valid;
import java.util.List;
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

    private final RocketService service;

    public RocketController(RocketService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Rocket create(@Valid @RequestBody RocketRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public Rocket getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<Rocket> getAll() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    public Rocket update(@PathVariable Long id, @Valid @RequestBody RocketRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
