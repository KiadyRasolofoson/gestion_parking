package com.example.parking.exception;

/** Conflit de donnees (doublon, chevauchement de reservation) -> HTTP 409. */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
