package com.example.parking.repository;

import com.example.parking.entity.Reservation;
import com.example.parking.entity.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>,
        JpaSpecificationExecutor<Reservation> {

    Optional<Reservation> findByReference(String reference);

    List<Reservation> findByUserIdOrderByExpectedArrivalDesc(Long userId);

    long countByStatus(ReservationStatus status);

    long countBySpaceIdAndStatusIn(Long spaceId, Collection<ReservationStatus> statuses);

    /**
     * Detecte un chevauchement sur une place : deux reservations actives ne
     * peuvent pas se croiser sur le meme intervalle.
     */
    @Query("""
            select count(r) > 0 from Reservation r
            where r.space.id = :spaceId
              and r.status in :statuses
              and r.id <> :excludedId
              and r.expectedArrival < :to
              and :from < r.expectedDeparture
            """)
    boolean existsOverlapping(@Param("spaceId") Long spaceId,
                              @Param("from") LocalDateTime from,
                              @Param("to") LocalDateTime to,
                              @Param("statuses") Collection<ReservationStatus> statuses,
                              @Param("excludedId") Long excludedId);

    /** Reservations actives d'un vehicule sur un intervalle donne. */
    @Query("""
            select r from Reservation r
            where r.vehicle.id = :vehicleId
              and r.status in :statuses
              and r.expectedArrival < :to
              and :from < r.expectedDeparture
            """)
    List<Reservation> findVehicleOverlaps(@Param("vehicleId") Long vehicleId,
                                          @Param("from") LocalDateTime from,
                                          @Param("to") LocalDateTime to,
                                          @Param("statuses") Collection<ReservationStatus> statuses);

    /** Reservation honorable pour une entree (vehicule attendu maintenant). */
    @Query("""
            select r from Reservation r
            where r.vehicle.id = :vehicleId
              and r.status = :status
              and r.expectedArrival <= :upperBound
              and r.expectedDeparture > :now
            order by r.expectedArrival asc
            """)
    List<Reservation> findHonorableForEntry(@Param("vehicleId") Long vehicleId,
                                            @Param("status") ReservationStatus status,
                                            @Param("upperBound") LocalDateTime upperBound,
                                            @Param("now") LocalDateTime now);

    /** Reservations confirmees dont l'heure d'arrivee est depassee. */
    @Query("""
            select r from Reservation r
            where r.status = :status
              and r.expectedDeparture < :now
            """)
    List<Reservation> findStale(@Param("status") ReservationStatus status, @Param("now") LocalDateTime now);

    @Query("""
            select count(r) from Reservation r
            where r.status in :statuses
              and r.expectedArrival >= :from
              and r.expectedArrival < :to
            """)
    long countBetween(@Param("from") LocalDateTime from,
                      @Param("to") LocalDateTime to,
                      @Param("statuses") Collection<ReservationStatus> statuses);
}
