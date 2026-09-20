package com.example.parking.repository.specification;

import com.example.parking.entity.Subscription;
import com.example.parking.entity.enums.SubscriptionStatus;
import com.example.parking.entity.enums.SubscriptionType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

/** Filtres dynamiques pour la recherche d'abonnements. */
public final class SubscriptionSpecifications {

    private SubscriptionSpecifications() {
    }

    public static Specification<Subscription> search(String term) {
        if (!StringUtils.hasText(term)) {
            return null;
        }
        String pattern = "%" + term.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("reference")), pattern),
                cb.like(cb.lower(root.get("vehicle").get("plateNumber")), pattern),
                cb.like(cb.lower(root.get("user").get("lastName")), pattern),
                cb.like(cb.lower(root.get("user").get("firstName")), pattern));
    }

    public static Specification<Subscription> hasStatus(SubscriptionStatus status) {
        if (status == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Subscription> hasType(SubscriptionType type) {
        if (type == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }

    public static Specification<Subscription> hasUser(Long userId) {
        if (userId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Subscription> validOn(LocalDate date) {
        if (date == null) {
            return null;
        }
        return (root, query, cb) -> cb.and(
                cb.lessThanOrEqualTo(root.get("startDate"), date),
                cb.greaterThanOrEqualTo(root.get("endDate"), date));
    }
}
