package com.astrobookings.rockets.repo;

import com.astrobookings.rockets.domain.Rocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryRocketRepository implements RocketRepository {

    private final ConcurrentHashMap<Long, Rocket> rockets = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public Rocket save(Rocket rocket) {
        Long id = rocket.id() == null ? idGenerator.incrementAndGet() : rocket.id();
        Rocket persisted = new Rocket(id, rocket.name(), rocket.range(), rocket.capacity());
        rockets.put(id, persisted);
        return persisted;
    }

    @Override
    public Optional<Rocket> findById(Long id) {
        return Optional.ofNullable(rockets.get(id));
    }

    @Override
    public List<Rocket> findAll() {
        return new ArrayList<>(rockets.values());
    }

    @Override
    public boolean deleteById(Long id) {
        return rockets.remove(id) != null;
    }
}
