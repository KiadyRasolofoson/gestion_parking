package com.example.parking.service;

import com.example.parking.dto.request.EntryRequest;
import com.example.parking.dto.request.ExitRequest;
import com.example.parking.dto.response.ParkingSessionResponse;
import com.example.parking.entity.enums.SessionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/** Coeur metier : entrees et sorties du parking. */
public interface ParkingService {

    /**
     * Enregistre une entree. Operation transactionnelle :
     * verification de disponibilite, attribution de la place, changement de
     * statut de la place, creation de la session, consommation de la
     * reservation eventuelle.
     */
    ParkingSessionResponse registerEntry(EntryRequest request, String operator);

    /**
     * Enregistre une sortie. Operation transactionnelle :
     * cloture de la session, liberation de la place, calcul du tarif et
     * emission de la facture (depassement inclus).
     */
    ParkingSessionResponse registerExit(ExitRequest request, String operator);

    Page<ParkingSessionResponse> search(String term, SessionStatus status, String zone,
                                        LocalDateTime from, LocalDateTime to, Pageable pageable);

    ParkingSessionResponse getById(Long id);

    List<ParkingSessionResponse> findOngoing();
}
