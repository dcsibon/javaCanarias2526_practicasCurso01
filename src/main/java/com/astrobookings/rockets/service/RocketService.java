package com.astrobookings.rockets.service;

import com.astrobookings.rockets.api.RocketRequest;
import com.astrobookings.rockets.domain.Rocket;
import com.astrobookings.rockets.domain.RocketRange;
import com.astrobookings.rockets.exception.RocketNotFoundException;
import com.astrobookings.rockets.repo.RocketRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RocketService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RocketService.class);

    private final RocketRepository repository;

    public RocketService(RocketRepository repository) {
        this.repository = repository;
    }

    public Rocket create(RocketRequest request) {
        Rocket created = repository.save(toRocket(request, null));
        LOGGER.info("Created rocket id={}, name={}, range={}, capacity={}",
                created.id(), created.name(), created.range(), created.capacity());
        return created;
    }

    public Rocket getById(Long id) {
        Rocket rocket = repository.findById(id).orElseThrow(() -> notFound(id));
        LOGGER.debug("Found rocket id={}", id);
        return rocket;
    }

    public List<Rocket> getAll() {
        List<Rocket> rockets = repository.findAll();
        LOGGER.debug("Found {} rockets", rockets.size());
        return rockets;
    }

    public Rocket update(Long id, RocketRequest request) {
        requireExistingRocket(id);
        Rocket updated = repository.save(toRocket(request, id));
        LOGGER.info("Updated rocket id={}, name={}, range={}, capacity={}",
                updated.id(), updated.name(), updated.range(), updated.capacity());
        return updated;
    }

    public void delete(Long id) {
        boolean deleted = repository.deleteById(id);
        if (!deleted) {
            throw notFound(id);
        }
        LOGGER.info("Deleted rocket id={}", id);
    }

    private void requireExistingRocket(Long id) {
        repository.findById(id).orElseThrow(() -> notFound(id));
    }

    private RocketNotFoundException notFound(Long id) {
        LOGGER.warn("Rocket not found id={}", id);
        return new RocketNotFoundException(id);
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
