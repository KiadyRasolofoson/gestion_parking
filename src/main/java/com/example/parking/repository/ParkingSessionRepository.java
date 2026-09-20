package com.example.parking.repository;

import com.example.parking.entity.ParkingSession;
import com.example.parking.entity.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingSessionRepository extends JpaRepository<ParkingSession, Long>,
        JpaSpecificationExecutor<ParkingSession> {

    Optional<ParkingSession> findByReference(String reference);

    Optional<ParkingSession> findFirstByVehicleIdAndStatus(Long vehicleId, SessionStatus status);

    Optional<ParkingSession> findFirstBySpaceIdAndStatus(Long spaceId, SessionStatus status);

    List<ParkingSession> findByStatusOrderByEntryTimeAsc(SessionStatus status);

    long countByStatus(SessionStatus status);

    @Query("""
            select count(s) from ParkingSession s
            where s.entryTime >= :from and s.entryTime < :to
            """)
    long countEntriesBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("""
            select count(s) from ParkingSession s
            where s.exitTime >= :from and s.exitTime < :to
            """)
    long countExitsBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("""
            select s from ParkingSession s
            where s.status = :status and s.exitTime >= :from and s.exitTime < :to
            """)
    List<ParkingSession> findCompletedBetween(@Param("status") SessionStatus status,
                                              @Param("from") LocalDateTime from,
                                              @Param("to") LocalDateTime to);
}
