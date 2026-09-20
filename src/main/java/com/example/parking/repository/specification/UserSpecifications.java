package com.example.parking.repository.specification;

import com.example.parking.entity.User;
import com.example.parking.entity.enums.Role;
import com.example.parking.entity.enums.UserCategory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/** Filtres dynamiques pour la recherche d'utilisateurs. */
public final class UserSpecifications {

    private UserSpecifications() {
    }

    public static Specification<User> search(String term) {
        if (!StringUtils.hasText(term)) {
            return null;
        }
        String pattern = "%" + term.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("username")), pattern),
                cb.like(cb.lower(root.get("firstName")), pattern),
                cb.like(cb.lower(root.get("lastName")), pattern),
                cb.like(cb.lower(root.get("email")), pattern));
    }

    public static Specification<User> hasRole(Role role) {
        if (role == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("role"), role);
    }

    public static Specification<User> hasCategory(UserCategory category) {
        if (category == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("category"), category);
    }

    public static Specification<User> isEnabled(Boolean enabled) {
        if (enabled == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("enabled"), enabled);
    }
}
