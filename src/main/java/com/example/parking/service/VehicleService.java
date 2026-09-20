package com.example.parking.service;

import com.example.parking.dto.request.CreateVehicleRequest;
import com.example.parking.dto.request.UpdateVehicleRequest;
import com.example.parking.dto.response.VehicleResponse;
import com.example.parking.entity.Vehicle;
import com.example.parking.entity.enums.VehicleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VehicleService {

    Page<VehicleResponse> search(String term, VehicleType type, Long ownerId, Pageable pageable);

    List<VehicleResponse> findAll();

    List<VehicleResponse> findByOwner(Long ownerId);

    VehicleResponse getById(Long id);

    Vehicle getEntity(Long id);

    Vehicle getByPlateNumber(String plateNumber);

    VehicleResponse create(CreateVehicleRequest request);

    VehicleResponse update(Long id, UpdateVehicleRequest request);

    void delete(Long id);
}
