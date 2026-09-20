package com.example.parking.repository.specification;

import com.example.parking.entity.Reservation;
import com.example.parking.entity.enums.ReservationStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/** Filtres dynamiques pour la recherche de reservations. */
public final class ReservationSpecifications {

    private ReservationSpecifications() {
    }

    public static Specification<Reservation> search(String term) {
        if (!StringUtils.hasText(term)) {
            return null;
        }
        String pattern = "%" + term.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("reference")), pattern),
                cb.like(cb.lower(root.get("vehicle").get("plateNumber")), pattern),
                cb.like(cb.lower(root.get("user").get("lastName")), pattern),
                cb.like(cb.lower(root.get("user").get("firstName")), pattern),
                cb.like(cb.lower(root.get("space").get("number")), pattern));
    }

    public static Specification<Reservation> hasStatus(ReservationStatus status) {
        if (status == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Reservation> hasUser(Long userId) {
        if (userId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Reservation> hasSpace(Long spaceId) {
        if (spaceId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("space").get("id"), spaceId);
    }

    public static Specification<Reservation> arrivalFrom(LocalDateTime from) {
        if (from == null) {
            return null;
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("expectedArrival"), from);
    }

    public static Specification<Reservation> arrivalTo(LocalDateTime to) {
        if (to == null) {
            return null;
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("expectedArrival"), to);
    }
}
