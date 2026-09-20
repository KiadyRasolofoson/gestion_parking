package com.example.parking.repository.specification;

import com.example.parking.entity.Invoice;
import com.example.parking.entity.enums.InvoiceStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/** Filtres dynamiques pour la facturation. */
public final class InvoiceSpecifications {

    private InvoiceSpecifications() {
    }

    public static Specification<Invoice> search(String term) {
        if (!StringUtils.hasText(term)) {
            return null;
        }
        String pattern = "%" + term.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("reference")), pattern),
                cb.like(cb.lower(root.get("session").get("reference")), pattern),
                cb.like(cb.lower(root.get("user").get("lastName")), pattern));
    }

    public static Specification<Invoice> hasStatus(InvoiceStatus status) {
        if (status == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Invoice> issuedFrom(LocalDateTime from) {
        if (from == null) {
            return null;
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("issuedAt"), from);
    }

    public static Specification<Invoice> issuedTo(LocalDateTime to) {
        if (to == null) {
            return null;
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("issuedAt"), to);
    }
}
