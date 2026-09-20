package com.example.parking.mapper;

import com.example.parking.dto.request.UpdateVehicleRequest;
import com.example.parking.dto.response.VehicleResponse;
import com.example.parking.entity.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {

    public VehicleResponse toResponse(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getPlateNumber(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getColor(),
                vehicle.getType(),
                vehicle.getType() != null ? vehicle.getType().getLabel() : null,
                vehicle.getOwner() != null ? vehicle.getOwner().getId() : null,
                vehicle.getOwner() != null ? vehicle.getOwner().getFullName() : null);
    }

    public UpdateVehicleRequest toUpdateRequest(Vehicle vehicle) {
        UpdateVehicleRequest request = new UpdateVehicleRequest();
        request.setPlateNumber(vehicle.getPlateNumber());
        request.setBrand(vehicle.getBrand());
        request.setModel(vehicle.getModel());
        request.setColor(vehicle.getColor());
        request.setType(vehicle.getType());
        request.setOwnerId(vehicle.getOwner() != null ? vehicle.getOwner().getId() : null);
        return request;
    }
}
