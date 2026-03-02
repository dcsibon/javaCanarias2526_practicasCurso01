package com.astrobookings.rockets.domain;

public record Rocket(Long id, String name, RocketRange range, Integer capacity) {
}
