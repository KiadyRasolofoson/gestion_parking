package com.example.parking.dto.request;

import com.example.parking.entity.enums.SpaceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class CreateSpaceRequest {

    @NotBlank(message = "Le numero de place est obligatoire")
    @Size(max = 15, message = "Le numero ne peut pas depasser 15 caracteres")
    private String number;

    @NotBlank(message = "La zone est obligatoire")
    @Size(max = 30)
    private String zone;

    @NotNull(message = "L'etage est obligatoire")
    @PositiveOrZero(message = "L'etage doit etre positif ou nul")
    private Integer floor = 0;

    @NotNull(message = "Le type de place est obligatoire")
    private SpaceType type = SpaceType.STANDARD;

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
}
