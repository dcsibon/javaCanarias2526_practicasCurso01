package com.astrobookings.rockets.service;

import com.astrobookings.rockets.api.RocketRequest;
import com.astrobookings.rockets.domain.Rocket;
import com.astrobookings.rockets.domain.RocketRange;
import com.astrobookings.rockets.exception.RocketNotFoundException;
import com.astrobookings.rockets.repo.RocketRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RocketService {

    private final RocketRepository repository;

    public RocketService(RocketRepository repository) {
        this.repository = repository;
    }

    public Rocket create(RocketRequest request) {
        Rocket rocket = new Rocket(null, request.name().trim(), RocketRange.fromInput(request.range()), request.capacity());
        return repository.save(rocket);
    }

    public Rocket getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RocketNotFoundException(id));
    }

    public List<Rocket> getAll() {
        return repository.findAll();
    }

    public Rocket update(Long id, RocketRequest request) {
        if (repository.findById(id).isEmpty()) {
            throw new RocketNotFoundException(id);
        }

        Rocket updated = new Rocket(id, request.name().trim(), RocketRange.fromInput(request.range()), request.capacity());
        return repository.save(updated);
    }

    public void delete(Long id) {
        boolean deleted = repository.deleteById(id);
        if (!deleted) {
            throw new RocketNotFoundException(id);
        }
    }
}
