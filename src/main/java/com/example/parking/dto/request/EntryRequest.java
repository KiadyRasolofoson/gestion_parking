package com.example.parking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/** Enregistrement d'une entree de vehicule dans le parking. */
public class EntryRequest {

    @NotBlank(message = "L'immatriculation est obligatoire")
    @Size(min = 3, max = 20)
    private String plateNumber;

    /**
     * Place imposee par l'operateur. Si elle est absente, le systeme attribue
     * automatiquement la meilleure place compatible.
     */
    private Long spaceId;

    /** Zone preferee pour l'attribution automatique (facultatif). */
    @Size(max = 30)
    private String preferredZone;

    /** Horodatage d'entree. Si absent, l'heure courante est utilisee. */
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime entryTime;

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public Long getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(Long spaceId) {
        this.spaceId = spaceId;
    }

    public String getPreferredZone() {
        return preferredZone;
    }

    public void setPreferredZone(String preferredZone) {
        this.preferredZone = preferredZone;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }
}
