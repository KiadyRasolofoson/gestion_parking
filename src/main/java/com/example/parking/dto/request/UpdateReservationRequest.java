package com.example.parking.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class UpdateReservationRequest {

    /** Nouvelle place souhaitee (facultatif : conserve la place actuelle si vide). */
    private Long spaceId;

    @NotNull(message = "La date et heure d'arrivee prevue sont obligatoires")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime expectedArrival;

    @NotNull(message = "La date et heure de depart prevue sont obligatoires")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime expectedDeparture;

    @Size(max = 500)
    private String notes;

    public Long getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(Long spaceId) {
        this.spaceId = spaceId;
    }

    public LocalDateTime getExpectedArrival() {
        return expectedArrival;
    }

    public void setExpectedArrival(LocalDateTime expectedArrival) {
        this.expectedArrival = expectedArrival;
    }

    public LocalDateTime getExpectedDeparture() {
        return expectedDeparture;
    }

    public void setExpectedDeparture(LocalDateTime expectedDeparture) {
        this.expectedDeparture = expectedDeparture;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
