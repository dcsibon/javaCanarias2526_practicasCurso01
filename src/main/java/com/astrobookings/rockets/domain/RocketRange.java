package com.astrobookings.rockets.domain;

import java.util.Locale;

public enum RocketRange {
    SUBORBITAL,
    ORBITAL,
    MOON,
    MARS;

    public static RocketRange fromInput(String value) {
        return RocketRange.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
