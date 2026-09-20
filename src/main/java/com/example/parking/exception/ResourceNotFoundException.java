package com.example.parking.exception;

/** Ressource demandee introuvable -> HTTP 404. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " introuvable (identifiant : " + id + ")");
    }
}
