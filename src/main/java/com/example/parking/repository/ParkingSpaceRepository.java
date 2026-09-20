package com.example.parking.repository;

import com.example.parking.entity.ParkingSpace;
import com.example.parking.entity.enums.SpaceStatus;
import com.example.parking.entity.enums.SpaceType;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingSpaceRepository extends JpaRepository<ParkingSpace, Long>,
        JpaSpecificationExecutor<ParkingSpace> {

    Optional<ParkingSpace> findByNumberIgnoreCase(String number);

    boolean existsByNumberIgnoreCase(String number);

    boolean existsByNumberIgnoreCaseAndIdNot(String number, Long id);

    List<ParkingSpace> findAllByOrderByZoneAscNumberAsc();

    List<ParkingSpace> findByStatus(SpaceStatus status, Sort sort);

    long countByStatus(SpaceStatus status);

    long countByType(SpaceType type);

    long countByTypeAndStatus(SpaceType type, SpaceStatus status);

    long countByZoneAndStatus(String zone, SpaceStatus status);

    long countByZone(String zone);

    @Query("select distinct s.zone from ParkingSpace s order by s.zone")
    List<String> findDistinctZones();

    /** Premiere place libre du type demande, eventuellement restreinte a une zone. */
    Optional<ParkingSpace> findFirstByTypeAndStatusOrderByNumberAsc(SpaceType type, SpaceStatus status);

    Optional<ParkingSpace> findFirstByTypeAndStatusAndZoneIgnoreCaseOrderByNumberAsc(SpaceType type,
                                                                                     SpaceStatus status,
                                                                                     String zone);

    /**
     * Verrou pessimiste : deux entrees simultanees ne peuvent pas prendre la
     * meme place. Utilise a l'interieur d'une transaction.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from ParkingSpace s where s.id = :id")
    Optional<ParkingSpace> findByIdForUpdate(@Param("id") Long id);
}
