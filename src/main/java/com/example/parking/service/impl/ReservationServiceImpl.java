package com.example.parking.service.impl;

import com.example.parking.config.ParkingProperties;
import com.example.parking.dto.request.CreateReservationRequest;
import com.example.parking.dto.request.UpdateReservationRequest;
import com.example.parking.dto.response.ReservationResponse;
import com.example.parking.entity.ParkingSpace;
import com.example.parking.entity.Reservation;
import com.example.parking.entity.User;
import com.example.parking.entity.Vehicle;
import com.example.parking.entity.enums.ReservationStatus;
import com.example.parking.entity.enums.SessionStatus;
import com.example.parking.entity.enums.SpaceStatus;
import com.example.parking.exception.BusinessException;
import com.example.parking.exception.ConflictException;
import com.example.parking.exception.InvalidOperationException;
import com.example.parking.exception.ResourceNotFoundException;
import com.example.parking.mapper.ReservationMapper;
import com.example.parking.repository.ParkingSessionRepository;
import com.example.parking.repository.ParkingSpaceRepository;
import com.example.parking.repository.ReservationRepository;
import com.example.parking.repository.UserRepository;
import com.example.parking.repository.VehicleRepository;
import com.example.parking.repository.specification.ReservationSpecifications;
import com.example.parking.service.ReservationService;
import com.example.parking.service.SpaceAllocationService;
import com.example.parking.util.ReferenceGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReservationServiceImpl implements ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationServiceImpl.class);

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingSpaceRepository spaceRepository;
    private final ParkingSessionRepository sessionRepository;
    private final SpaceAllocationService allocationService;
    private final ReservationMapper reservationMapper;
    private final ParkingProperties properties;

    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                 UserRepository userRepository,
                                 VehicleRepository vehicleRepository,
                                 ParkingSpaceRepository spaceRepository,
                                 ParkingSessionRepository sessionRepository,
                                 SpaceAllocationService allocationService,
                                 ReservationMapper reservationMapper,
                                 ParkingProperties properties) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.spaceRepository = spaceRepository;
        this.sessionRepository = sessionRepository;
        this.allocationService = allocationService;
        this.reservationMapper = reservationMapper;
        this.properties = properties;
    }

    @Override
    public Page<ReservationResponse> search(String term, ReservationStatus status, Long userId,
                                            LocalDateTime from, LocalDateTime to, Pageable pageable) {
        Specification<Reservation> spec = Specification.where(ReservationSpecifications.search(term))
                .and(ReservationSpecifications.hasStatus(status))
                .and(ReservationSpecifications.hasUser(userId))
                .and(ReservationSpecifications.arrivalFrom(from))
                .and(ReservationSpecifications.arrivalTo(to));
        return reservationRepository.findAll(spec, pageable).map(reservationMapper::toResponse);
    }

    @Override
    public ReservationResponse getById(Long id) {
        return reservationMapper.toResponse(getEntity(id));
    }

    @Override
    public Reservation getEntity(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));
    }

    /**
     * Operation transactionnelle : validation de la periode, verification de la
     * disponibilite de la place, attribution automatique si besoin, creation de
     * la reservation puis changement du statut de la place.
     */
    @Override
    @Transactional
    public ReservationResponse create(CreateReservationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", request.getUserId()));
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", request.getVehicleId()));

        validatePeriod(request.getExpectedArrival(), request.getExpectedDeparture());
        validateOwnership(user, vehicle);
        validateVehicleNotBusy(vehicle, request.getExpectedArrival(), request.getExpectedDeparture());

        ParkingSpace space = resolveSpace(request.getSpaceId(), user,
                request.getExpectedArrival(), request.getExpectedDeparture(), null);

        Reservation reservation = new Reservation();
        reservation.setReference(ReferenceGenerator.reservation());
        reservation.setUser(user);
        reservation.setVehicle(vehicle);
        reservation.setSpace(space);
        reservation.setExpectedArrival(request.getExpectedArrival());
        reservation.setExpectedDeparture(request.getExpectedDeparture());
        reservation.setStatus(ReservationStatus.CONFIRMEE);
        reservation.setNotes(request.getNotes());
        Reservation saved = reservationRepository.save(reservation);

        // Changement de statut de la place dans la meme transaction
        if (space.getStatus() == SpaceStatus.LIBRE) {
            space.setStatus(SpaceStatus.RESERVEE);
            spaceRepository.save(space);
        }

        log.info("Reservation {} creee sur la place {} pour {}", saved.getReference(), space.getNumber(),
                vehicle.getPlateNumber());
        return reservationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ReservationResponse update(Long id, UpdateReservationRequest request) {
        Reservation reservation = getEntity(id);
        if (reservation.getStatus() != ReservationStatus.CONFIRMEE) {
            throw new InvalidOperationException("Seule une reservation confirmee peut etre modifiee (statut actuel : "
                    + reservation.getStatus().getLabel() + ")");
        }
        validatePeriod(request.getExpectedArrival(), request.getExpectedDeparture());
        validateVehicleNotBusyExcluding(reservation, request.getExpectedArrival(), request.getExpectedDeparture());

        ParkingSpace previousSpace = reservation.getSpace();
        Long targetSpaceId = request.getSpaceId() != null ? request.getSpaceId() : previousSpace.getId();
        ParkingSpace space = resolveSpace(targetSpaceId, reservation.getUser(),
                request.getExpectedArrival(), request.getExpectedDeparture(), reservation.getId());

        reservation.setSpace(space);
        reservation.setExpectedArrival(request.getExpectedArrival());
        reservation.setExpectedDeparture(request.getExpectedDeparture());
        reservation.setNotes(request.getNotes());
        Reservation saved = reservationRepository.save(reservation);

        if (!previousSpace.getId().equals(space.getId())) {
            if (space.getStatus() == SpaceStatus.LIBRE) {
                space.setStatus(SpaceStatus.RESERVEE);
                spaceRepository.save(space);
            }
            releaseSpaceIfIdle(previousSpace);
        }
        return reservationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ReservationResponse cancel(Long id, String reason) {
        Reservation reservation = getEntity(id);
        if (reservation.getStatus().isFinal()) {
            throw new InvalidOperationException("Cette reservation est deja " + reservation.getStatus().getLabel());
        }
        if (reservation.getStatus() == ReservationStatus.EN_COURS) {
            throw new InvalidOperationException(
                    "Le vehicule est deja dans le parking : enregistrez une sortie au lieu d'annuler");
        }
        reservation.setStatus(ReservationStatus.ANNULEE);
        reservation.setCancelledAt(LocalDateTime.now());
        reservation.setCancelReason(StringUtils.hasText(reason) ? reason.trim() : "Annulee par l'utilisateur");
        Reservation saved = reservationRepository.save(reservation);
        releaseSpaceIfIdle(reservation.getSpace());
        return reservationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public int expireStaleReservations() {
        List<Reservation> stale = reservationRepository.findStale(ReservationStatus.CONFIRMEE, LocalDateTime.now());
        for (Reservation reservation : stale) {
            reservation.setStatus(ReservationStatus.EXPIREE);
            reservationRepository.save(reservation);
            releaseSpaceIfIdle(reservation.getSpace());
        }
        if (!stale.isEmpty()) {
            log.info("{} reservation(s) passee(s) en EXPIREE", stale.size());
        }
        return stale.size();
    }

    // ------------------------------------------------------------------ regles

    private void validatePeriod(LocalDateTime arrival, LocalDateTime departure) {
        if (arrival == null || departure == null) {
            throw new BusinessException("Les dates d'arrivee et de depart sont obligatoires");
        }
        if (!arrival.isBefore(departure)) {
            throw new BusinessException("La date de depart doit etre posterieure a la date d'arrivee");
        }
        long minutes = Duration.between(arrival, departure).toMinutes();
        ParkingProperties.Reservation limits = properties.getReservation();
        if (minutes < limits.getMinDurationMinutes()) {
            throw new BusinessException("La duree minimale d'une reservation est de "
                    + limits.getMinDurationMinutes() + " minutes");
        }
        if (minutes > (long) limits.getMaxDurationHours() * 60) {
            throw new BusinessException("La duree maximale d'une reservation est de "
                    + limits.getMaxDurationHours() + " heures");
        }
    }

    private void validateOwnership(User user, Vehicle vehicle) {
        if (vehicle.getOwner() == null || !vehicle.getOwner().getId().equals(user.getId())) {
            throw new BusinessException("Le vehicule " + vehicle.getPlateNumber()
                    + " n'appartient pas a " + user.getFullName());
        }
    }

    private void validateVehicleNotBusy(Vehicle vehicle, LocalDateTime from, LocalDateTime to) {
        List<Reservation> overlaps = reservationRepository.findVehicleOverlaps(vehicle.getId(), from, to,
                ReservationStatus.activeStatuses());
        if (!overlaps.isEmpty()) {
            throw new ConflictException("Le vehicule " + vehicle.getPlateNumber()
                    + " a deja une reservation sur cette periode (" + overlaps.get(0).getReference() + ")");
        }
    }

    private void validateVehicleNotBusyExcluding(Reservation reservation, LocalDateTime from, LocalDateTime to) {
        List<Reservation> overlaps = reservationRepository.findVehicleOverlaps(
                reservation.getVehicle().getId(), from, to, ReservationStatus.activeStatuses());
        boolean conflict = overlaps.stream().anyMatch(other -> !other.getId().equals(reservation.getId()));
        if (conflict) {
            throw new ConflictException("Le vehicule " + reservation.getVehicle().getPlateNumber()
                    + " a deja une reservation sur cette periode");
        }
    }

    /**
     * Retourne la place a reserver : celle demandee si elle est compatible et
     * libre sur la periode, sinon une place attribuee automatiquement.
     */
    private ParkingSpace resolveSpace(Long spaceId, User user, LocalDateTime from, LocalDateTime to,
                                      Long excludedReservationId) {
        long excluded = excludedReservationId != null ? excludedReservationId : -1L;
        if (spaceId == null) {
            ParkingSpace allocated = allocationService.allocate(user.getCategory(), null);
            if (reservationRepository.existsOverlapping(allocated.getId(), from, to,
                    ReservationStatus.activeStatuses(), excluded)) {
                throw new ConflictException("La place attribuee automatiquement est deja reservee sur cette periode");
            }
            return allocated;
        }

        ParkingSpace space = spaceRepository.findByIdForUpdate(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Place de parking", spaceId));
        if (space.getStatus() == SpaceStatus.HORS_SERVICE) {
            throw new BusinessException("La place " + space.getNumber() + " est hors service");
        }
        if (space.getStatus() == SpaceStatus.OCCUPEE) {
            throw new ConflictException("La place " + space.getNumber() + " est actuellement occupee");
        }
        if (reservationRepository.existsOverlapping(spaceId, from, to,
                ReservationStatus.activeStatuses(), excluded)) {
            throw new ConflictException("La place " + space.getNumber()
                    + " est deja reservee sur la periode demandee");
        }
        return space;
    }

    /**
     * Remet la place a LIBRE si aucune session n'est en cours et si aucune
     * autre reservation active ne l'utilise.
     */
    private void releaseSpaceIfIdle(ParkingSpace space) {
        if (space == null) {
            return;
        }
        if (sessionRepository.findFirstBySpaceIdAndStatus(space.getId(), SessionStatus.EN_COURS).isPresent()) {
            return;
        }
        long activeReservations = reservationRepository.countBySpaceIdAndStatusIn(space.getId(),
                ReservationStatus.activeStatuses());
        if (activeReservations > 0) {
            space.setStatus(SpaceStatus.RESERVEE);
        } else if (space.getStatus() != SpaceStatus.HORS_SERVICE) {
            space.setStatus(SpaceStatus.LIBRE);
        }
        spaceRepository.save(space);
    }
}
