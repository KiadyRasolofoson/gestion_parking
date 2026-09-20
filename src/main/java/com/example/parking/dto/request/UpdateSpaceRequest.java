package com.example.parking.dto.request;

import com.example.parking.entity.enums.SpaceStatus;
import com.example.parking.entity.enums.SpaceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class UpdateSpaceRequest {

    @NotBlank(message = "Le numero de place est obligatoire")
    @Size(max = 15)
    private String number;

    @NotBlank(message = "La zone est obligatoire")
    @Size(max = 30)
    private String zone;

    @NotNull(message = "L'etage est obligatoire")
    @PositiveOrZero(message = "L'etage doit etre positif ou nul")
    private Integer floor;

    @NotNull(message = "Le type de place est obligatoire")
    private SpaceType type;

    /** Optionnel : changement de statut administratif (LIBRE / HORS_SERVICE). */
    private SpaceStatus status;

    @Size(max = 255)
    private String outOfServiceReason;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public SpaceType getType() {
        return type;
    }

    public void setType(SpaceType type) {
        this.type = type;
    }

    public SpaceStatus getStatus() {
        return status;
    }

    public void setStatus(SpaceStatus status) {
        this.status = status;
    }

    public String getOutOfServiceReason() {
        return outOfServiceReason;
    }

    public void setOutOfServiceReason(String outOfServiceReason) {
        this.outOfServiceReason = outOfServiceReason;
    }
}
