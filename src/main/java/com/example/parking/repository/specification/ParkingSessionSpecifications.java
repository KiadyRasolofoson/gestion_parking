package com.example.parking.repository.specification;

import com.example.parking.entity.ParkingSession;
import com.example.parking.entity.enums.SessionStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/** Filtres dynamiques pour l'historique des entrees / sorties. */
public final class ParkingSessionSpecifications {

    private ParkingSessionSpecifications() {
    }

    public static Specification<ParkingSession> search(String term) {
        if (!StringUtils.hasText(term)) {
            return null;
        }
        String pattern = "%" + term.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("reference")), pattern),
                cb.like(cb.lower(root.get("vehicle").get("plateNumber")), pattern),
                cb.like(cb.lower(root.get("space").get("number")), pattern),
                cb.like(cb.lower(root.get("user").get("lastName")), pattern));
    }

    public static Specification<ParkingSession> hasStatus(SessionStatus status) {
        if (status == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<ParkingSession> hasZone(String zone) {
        if (!StringUtils.hasText(zone)) {
            return null;
        }
        return (root, query, cb) -> cb.equal(cb.lower(root.get("space").get("zone")), zone.trim().toLowerCase());
    }

    public static Specification<ParkingSession> entryFrom(LocalDateTime from) {
        if (from == null) {
            return null;
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("entryTime"), from);
    }

    public static Specification<ParkingSession> entryTo(LocalDateTime to) {
        if (to == null) {
            return null;
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("entryTime"), to);
    }
}
