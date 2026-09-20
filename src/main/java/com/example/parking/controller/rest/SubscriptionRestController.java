package com.example.parking.controller.rest;

import com.example.parking.dto.request.CreateSubscriptionRequest;
import com.example.parking.dto.response.PageResponse;
import com.example.parking.dto.response.SubscriptionResponse;
import com.example.parking.entity.enums.SubscriptionStatus;
import com.example.parking.entity.enums.SubscriptionType;
import com.example.parking.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/subscriptions")
@Tag(name = "Abonnements", description = "Abonnements journaliers, hebdomadaires, mensuels et annuels")
public class SubscriptionRestController {

    private final SubscriptionService subscriptionService;

    public SubscriptionRestController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping
    @Operation(summary = "Liste paginee des abonnements")
    public ResponseEntity<PageResponse<SubscriptionResponse>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) SubscriptionStatus status,
            @RequestParam(required = false) SubscriptionType type,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validOn,
            @PageableDefault(size = 20, sort = "startDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<SubscriptionResponse> page = subscriptionService.search(search, status, type, userId, validOn, pageable);
        return ResponseEntity.ok(PageResponse.of(page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail d'un abonnement")
    public ResponseEntity<SubscriptionResponse> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(subscriptionService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Cree un abonnement (date de fin calculee automatiquement)")
    public ResponseEntity<SubscriptionResponse> create(@Valid @RequestBody CreateSubscriptionRequest request) {
        SubscriptionResponse created = subscriptionService.create(request);
        return ResponseEntity.created(URI.create("/api/subscriptions/" + created.id())).body(created);
    }

    @PostMapping("/{id}/suspend")
    @Operation(summary = "Suspend un abonnement actif")
    public ResponseEntity<SubscriptionResponse> suspend(@PathVariable Long id) {
        return ResponseEntity.ok(subscriptionService.suspend(id));
    }

    @PostMapping("/{id}/reactivate")
    @Operation(summary = "Reactive un abonnement suspendu")
    public ResponseEntity<SubscriptionResponse> reactivate(@PathVariable Long id) {
        return ResponseEntity.ok(subscriptionService.reactivate(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Annule un abonnement")
    public ResponseEntity<SubscriptionResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(subscriptionService.cancel(id));
    }

    @PostMapping("/refresh-expired")
    @Operation(summary = "Passe en EXPIRE les abonnements arrives a echeance")
    public ResponseEntity<Integer> refreshExpired() {
        return ResponseEntity.ok(subscriptionService.refreshExpiredSubscriptions());
    }
}
