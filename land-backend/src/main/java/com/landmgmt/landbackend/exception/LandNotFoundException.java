package com.landmgmt.landbackend.exception;

public class LandNotFoundException extends RuntimeException {
    public LandNotFoundException(Long id) {
        super("Land not found with ID: " + id);
    }
}