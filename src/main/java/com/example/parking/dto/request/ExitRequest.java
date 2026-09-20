package com.example.parking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/** Enregistrement d'une sortie de vehicule. Declenche la facturation. */
public class ExitRequest {

    @NotBlank(message = "L'immatriculation est obligatoire")
    @Size(min = 3, max = 20)
    private String plateNumber;

    /** Horodatage de sortie. Si absent, l'heure courante est utilisee. */
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime exitTime;

    /** Marquer la facture comme reglee immediatement. */
    private boolean markAsPaid = true;

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public boolean isMarkAsPaid() {
        return markAsPaid;
    }

    public void setMarkAsPaid(boolean markAsPaid) {
        this.markAsPaid = markAsPaid;
    }
}
