package com.example.parking.service;

import com.example.parking.dto.request.CreateSpaceRequest;
import com.example.parking.dto.request.UpdateSpaceRequest;
import com.example.parking.dto.response.ParkingSpaceResponse;
import com.example.parking.entity.ParkingSpace;
import com.example.parking.entity.enums.SpaceStatus;
import com.example.parking.entity.enums.SpaceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ParkingSpaceService {

    Page<ParkingSpaceResponse> search(String term, String zone, SpaceType type, SpaceStatus status,
                                      Integer floor, Pageable pageable);

    List<ParkingSpaceResponse> findAvailable(SpaceType type, String zone);

    /** Toutes les places triees par zone puis numero : utilise par le plan du parking. */
    List<ParkingSpaceResponse> findAllForMap();

    /** Places regroupees par zone, pour la representation visuelle du parking. */
    java.util.Map<String, List<ParkingSpaceResponse>> findGroupedByZone();

    List<String> findZones();

    ParkingSpaceResponse getById(Long id);

    ParkingSpace getEntity(Long id);

    ParkingSpaceResponse create(CreateSpaceRequest request);

    ParkingSpaceResponse update(Long id, UpdateSpaceRequest request);

    ParkingSpaceResponse setOutOfService(Long id, String reason);

    ParkingSpaceResponse setInService(Long id);

    void delete(Long id);
}
