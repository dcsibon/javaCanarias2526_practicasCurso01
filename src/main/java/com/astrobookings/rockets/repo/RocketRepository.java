package com.astrobookings.rockets.repo;

import com.astrobookings.rockets.domain.Rocket;
import java.util.List;
import java.util.Optional;

public interface RocketRepository {
    Rocket save(Rocket rocket);

    Optional<Rocket> findById(Long id);

    List<Rocket> findAll();

    boolean deleteById(Long id);
}
