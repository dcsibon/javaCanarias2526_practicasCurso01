package com.astrobookings.rockets.api;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RocketRequest(
        @NotBlank(message = NAME_REQUIRED_MESSAGE)
        String name,
        @NotBlank(message = RANGE_REQUIRED_MESSAGE)
        @Pattern(regexp = ALLOWED_RANGE_PATTERN, message = RANGE_ALLOWED_MESSAGE)
        String range,
        @Min(value = 1, message = CAPACITY_BOUNDS_MESSAGE)
        @Max(value = 10, message = CAPACITY_BOUNDS_MESSAGE)
        Integer capacity
) {
    public static final String NAME_REQUIRED_MESSAGE = "name is required";
    public static final String RANGE_REQUIRED_MESSAGE = "range is required";
    public static final String RANGE_ALLOWED_MESSAGE = "range must be one of: suborbital, orbital, moon, mars";
    public static final String CAPACITY_BOUNDS_MESSAGE = "capacity must be between 1 and 10";
    private static final String ALLOWED_RANGE_PATTERN = "suborbital|orbital|moon|mars";
}
