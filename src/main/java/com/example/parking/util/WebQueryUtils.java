package com.example.parking.util;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Construction des fragments de requete utilises par la pagination Thymeleaf :
 * les filtres courants sont conserves d'une page a l'autre.
 */
public final class WebQueryUtils {

    private WebQueryUtils() {
    }

    /** Retourne une chaine du type {@code &search=abc&status=LIBRE} (valeurs encodees). */
    public static String buildFilterQuery(Map<String, Object> filters) {
        StringBuilder query = new StringBuilder();
        filters.forEach((key, value) -> {
            if (value == null) {
                return;
            }
            String text = String.valueOf(value);
            if (text.isBlank()) {
                return;
            }
            query.append('&')
                    .append(key)
                    .append('=')
                    .append(URLEncoder.encode(text, StandardCharsets.UTF_8));
        });
        return query.toString();
    }

    /** Representation {@code champ,direction} du tri courant, pour les liens de pagination. */
    public static String sortParam(Pageable pageable) {
        if (pageable == null || pageable.getSort().isUnsorted()) {
            return "";
        }
        Sort.Order order = pageable.getSort().iterator().next();
        return order.getProperty() + "," + order.getDirection().name().toLowerCase();
    }
}
