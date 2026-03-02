package com.astrobookings.rockets.api;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RocketRequest(
        @NotBlank(message = "name is required")
        String name,
        @NotBlank(message = "range is required")
        @Pattern(regexp = "suborbital|orbital|moon|mars", message = "range must be one of: suborbital, orbital, moon, mars")
        String range,
        @Min(value = 1, message = "capacity must be between 1 and 10")
        @Max(value = 10, message = "capacity must be between 1 and 10")
        Integer capacity
) {
}
