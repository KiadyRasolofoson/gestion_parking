package com.example.parking.exception;

/**
 * Operation impossible dans l'etat courant de la ressource
 * (transition de statut interdite) -> HTTP 422.
 */
public class InvalidOperationException extends RuntimeException {

    public InvalidOperationException(String message) {
        super(message);
    }
}
