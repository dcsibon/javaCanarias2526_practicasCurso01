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
        return repository.save(toRocket(request, null));
    }

    public Rocket getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RocketNotFoundException(id));
    }

    public List<Rocket> getAll() {
        return repository.findAll();
    }

    public Rocket update(Long id, RocketRequest request) {
        requireExistingRocket(id);
        return repository.save(toRocket(request, id));
    }

    public void delete(Long id) {
        boolean deleted = repository.deleteById(id);
        if (!deleted) {
            throw new RocketNotFoundException(id);
        }
    }

    private void requireExistingRocket(Long id) {
        repository.findById(id).orElseThrow(() -> new RocketNotFoundException(id));
    }

    private Rocket toRocket(RocketRequest request, Long id) {
        return new Rocket(
                id,
                request.name().trim(),
                RocketRange.fromInput(request.range()),
                request.capacity()
        );
    }
}
