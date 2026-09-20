package com.example.parking.repository.specification;

import com.example.parking.entity.Vehicle;
import com.example.parking.entity.enums.VehicleType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/** Filtres dynamiques pour la recherche de vehicules. */
public final class VehicleSpecifications {

    private VehicleSpecifications() {
    }

    public static Specification<Vehicle> search(String term) {
        if (!StringUtils.hasText(term)) {
            return null;
        }
        String pattern = "%" + term.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("plateNumber")), pattern),
                cb.like(cb.lower(root.get("brand")), pattern),
                cb.like(cb.lower(root.get("model")), pattern),
                cb.like(cb.lower(root.get("owner").get("lastName")), pattern));
    }

    public static Specification<Vehicle> hasType(VehicleType type) {
        if (type == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }

    public static Specification<Vehicle> hasOwner(Long ownerId) {
        if (ownerId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("owner").get("id"), ownerId);
    }
}
