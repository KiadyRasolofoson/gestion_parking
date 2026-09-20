package com.example.parking.dto.request;

import com.example.parking.entity.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateVehicleRequest {

    @NotBlank(message = "L'immatriculation est obligatoire")
    @Size(min = 3, max = 20, message = "L'immatriculation doit contenir entre 3 et 20 caracteres")
    private String plateNumber;

    @Size(max = 50)
    private String brand;

    @Size(max = 50)
    private String model;

    @Size(max = 30)
    private String color;

    @NotNull(message = "Le type de vehicule est obligatoire")
    private VehicleType type;

    @NotNull(message = "Le proprietaire est obligatoire")
    private Long ownerId;

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}
