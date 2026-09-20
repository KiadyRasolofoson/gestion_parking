package com.example.parking.service;

import com.example.parking.entity.ParkingSpace;
import com.example.parking.entity.enums.UserCategory;

/**
 * Attribution automatique des places.
 * Cherche d'abord le type de place correspondant a la categorie de
 * l'utilisateur, puis applique les regles de substitution configurees.
 */
public interface SpaceAllocationService {

    /**
     * Trouve et verrouille une place libre compatible.
     *
     * @param category      categorie de l'utilisateur
     * @param preferredZone zone souhaitee (peut etre nulle)
     * @return la place verrouillee en base (verrou pessimiste)
     */
    ParkingSpace allocate(UserCategory category, String preferredZone);
}
