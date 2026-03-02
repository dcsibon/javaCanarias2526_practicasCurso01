package com.astrobookings.rockets.domain;

public enum RocketRange {
    SUBORBITAL,
    ORBITAL,
    MOON,
    MARS;

    public static RocketRange fromInput(String value) {
        return RocketRange.valueOf(value.trim().toUpperCase());
    }
}
