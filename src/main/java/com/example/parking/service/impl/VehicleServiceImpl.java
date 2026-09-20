package com.example.parking.service.impl;

import com.example.parking.dto.request.CreateVehicleRequest;
import com.example.parking.dto.request.UpdateVehicleRequest;
import com.example.parking.dto.response.VehicleResponse;
import com.example.parking.entity.User;
import com.example.parking.entity.Vehicle;
import com.example.parking.entity.enums.SessionStatus;
import com.example.parking.entity.enums.VehicleType;
import com.example.parking.exception.BusinessException;
import com.example.parking.exception.ConflictException;
import com.example.parking.exception.ResourceNotFoundException;
import com.example.parking.mapper.VehicleMapper;
import com.example.parking.repository.ParkingSessionRepository;
import com.example.parking.repository.UserRepository;
import com.example.parking.repository.VehicleRepository;
import com.example.parking.repository.specification.VehicleSpecifications;
import com.example.parking.service.VehicleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final ParkingSessionRepository sessionRepository;
    private final VehicleMapper vehicleMapper;

    public VehicleServiceImpl(VehicleRepository vehicleRepository,
                              UserRepository userRepository,
                              ParkingSessionRepository sessionRepository,
                              VehicleMapper vehicleMapper) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.vehicleMapper = vehicleMapper;
    }

    @Override
    public Page<VehicleResponse> search(String term, VehicleType type, Long ownerId, Pageable pageable) {
        Specification<Vehicle> spec = Specification.where(VehicleSpecifications.search(term))
                .and(VehicleSpecifications.hasType(type))
                .and(VehicleSpecifications.hasOwner(ownerId));
        return vehicleRepository.findAll(spec, pageable).map(vehicleMapper::toResponse);
    }

    @Override
    public List<VehicleResponse> findAll() {
        return vehicleRepository.findAll().stream().map(vehicleMapper::toResponse).toList();
    }

    @Override
    public List<VehicleResponse> findByOwner(Long ownerId) {
        return vehicleRepository.findByOwnerId(ownerId).stream().map(vehicleMapper::toResponse).toList();
    }

    @Override
    public VehicleResponse getById(Long id) {
        return vehicleMapper.toResponse(getEntity(id));
    }

    @Override
    public Vehicle getEntity(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", id));
    }

    @Override
    public Vehicle getByPlateNumber(String plateNumber) {
        String normalized = normalize(plateNumber);
        return vehicleRepository.findByPlateNumberIgnoreCase(normalized)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aucun vehicule enregistre avec l'immatriculation " + normalized));
    }

    @Override
    @Transactional
    public VehicleResponse create(CreateVehicleRequest request) {
        String plate = normalize(request.getPlateNumber());
        if (vehicleRepository.existsByPlateNumberIgnoreCase(plate)) {
            throw new ConflictException("Un vehicule immatricule " + plate + " existe deja");
        }
        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", request.getOwnerId()));

        Vehicle vehicle = new Vehicle();
        vehicle.setPlateNumber(plate);
        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setColor(request.getColor());
        vehicle.setType(request.getType());
        vehicle.setOwner(owner);
        return vehicleMapper.toResponse(vehicleRepository.save(vehicle));
    }

    @Override
    @Transactional
    public VehicleResponse update(Long id, UpdateVehicleRequest request) {
        Vehicle vehicle = getEntity(id);
        String plate = normalize(request.getPlateNumber());
        if (vehicleRepository.existsByPlateNumberIgnoreCaseAndIdNot(plate, id)) {
            throw new ConflictException("Un vehicule immatricule " + plate + " existe deja");
        }
        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", request.getOwnerId()));

        vehicle.setPlateNumber(plate);
        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setColor(request.getColor());
        vehicle.setType(request.getType());
        vehicle.setOwner(owner);
        return vehicleMapper.toResponse(vehicleRepository.save(vehicle));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Vehicle vehicle = getEntity(id);
        if (sessionRepository.findFirstByVehicleIdAndStatus(id, SessionStatus.EN_COURS).isPresent()) {
            throw new BusinessException("Suppression impossible : ce vehicule est actuellement dans le parking");
        }
        vehicleRepository.delete(vehicle);
    }

    private String normalize(String plateNumber) {
        return plateNumber == null ? null : plateNumber.trim().toUpperCase().replace(" ", "");
    }
}
