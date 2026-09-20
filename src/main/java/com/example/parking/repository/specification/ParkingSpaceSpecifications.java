package com.example.parking.repository.specification;

import com.example.parking.entity.ParkingSpace;
import com.example.parking.entity.enums.SpaceStatus;
import com.example.parking.entity.enums.SpaceType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/** Filtres dynamiques pour la recherche de places. */
public final class ParkingSpaceSpecifications {

    private ParkingSpaceSpecifications() {
    }

    public static Specification<ParkingSpace> search(String term) {
        if (!StringUtils.hasText(term)) {
            return null;
        }
        String pattern = "%" + term.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("number")), pattern),
                cb.like(cb.lower(root.get("zone")), pattern));
    }

    public static Specification<ParkingSpace> hasZone(String zone) {
        if (!StringUtils.hasText(zone)) {
            return null;
        }
        return (root, query, cb) -> cb.equal(cb.lower(root.get("zone")), zone.trim().toLowerCase());
    }

    public static Specification<ParkingSpace> hasType(SpaceType type) {
        if (type == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }

    public static Specification<ParkingSpace> hasStatus(SpaceStatus status) {
        if (status == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<ParkingSpace> hasFloor(Integer floor) {
        if (floor == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("floor"), floor);
    }
}
