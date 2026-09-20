package com.example.parking.repository;

import com.example.parking.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {

    Optional<Vehicle> findByPlateNumberIgnoreCase(String plateNumber);

    boolean existsByPlateNumberIgnoreCase(String plateNumber);

    boolean existsByPlateNumberIgnoreCaseAndIdNot(String plateNumber, Long id);

    List<Vehicle> findByOwnerId(Long ownerId);
}
