package com.astrobookings.rockets.exception;

public class RocketNotFoundException extends RuntimeException {

    public RocketNotFoundException(Long id) {
        super("rocket not found with id " + id);
    }
}
