package com.example.parking.mapper;

import com.example.parking.dto.request.CreateSpaceRequest;
import com.example.parking.dto.request.UpdateSpaceRequest;
import com.example.parking.dto.response.ParkingSpaceResponse;
import com.example.parking.entity.ParkingSpace;
import org.springframework.stereotype.Component;

@Component
public class ParkingSpaceMapper {

    public ParkingSpaceResponse toResponse(ParkingSpace space) {
        if (space == null) {
            return null;
        }
        return new ParkingSpaceResponse(
                space.getId(),
                space.getNumber(),
                space.getZone(),
                space.getFloor(),
                space.getType(),
                space.getType() != null ? space.getType().getLabel() : null,
                space.getStatus(),
                space.getStatus() != null ? space.getStatus().getLabel() : null,
                space.getOutOfServiceReason());
    }

    public ParkingSpace toEntity(CreateSpaceRequest request) {
        ParkingSpace space = new ParkingSpace();
        space.setNumber(request.getNumber().trim().toUpperCase());
        space.setZone(request.getZone().trim().toUpperCase());
        space.setFloor(request.getFloor());
        space.setType(request.getType());
        return space;
    }

    public UpdateSpaceRequest toUpdateRequest(ParkingSpace space) {
        UpdateSpaceRequest request = new UpdateSpaceRequest();
        request.setNumber(space.getNumber());
        request.setZone(space.getZone());
        request.setFloor(space.getFloor());
        request.setType(space.getType());
        request.setStatus(space.getStatus());
        request.setOutOfServiceReason(space.getOutOfServiceReason());
        return request;
    }
}
