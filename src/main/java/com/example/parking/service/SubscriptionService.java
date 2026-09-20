package com.example.parking.service;

import com.example.parking.dto.request.CreateSubscriptionRequest;
import com.example.parking.dto.response.SubscriptionResponse;
import com.example.parking.entity.Subscription;
import com.example.parking.entity.enums.SubscriptionStatus;
import com.example.parking.entity.enums.SubscriptionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface SubscriptionService {

    Page<SubscriptionResponse> search(String term, SubscriptionStatus status, SubscriptionType type,
                                      Long userId, LocalDate validOn, Pageable pageable);

    SubscriptionResponse getById(Long id);

    SubscriptionResponse create(CreateSubscriptionRequest request);

    SubscriptionResponse cancel(Long id);

    SubscriptionResponse suspend(Long id);

    SubscriptionResponse reactivate(Long id);

    /** Abonnement valide couvrant le vehicule a la date donnee. */
    Optional<Subscription> findValidSubscription(Long vehicleId, LocalDate date);

    BigDecimal catalogPrice(SubscriptionType type);

    /** Passe en EXPIRE les abonnements actifs dont la date de fin est depassee. */
    int refreshExpiredSubscriptions();
}
