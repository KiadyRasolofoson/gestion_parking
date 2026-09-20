package com.example.parking.controller.rest;

import com.example.parking.dto.request.EntryRequest;
import com.example.parking.dto.request.ExitRequest;
import com.example.parking.dto.response.OccupancyResponse;
import com.example.parking.dto.response.PageResponse;
import com.example.parking.dto.response.ParkingSessionResponse;
import com.example.parking.dto.response.RevenueResponse;
import com.example.parking.entity.enums.SessionStatus;
import com.example.parking.security.CurrentUserProvider;
import com.example.parking.service.ParkingService;
import com.example.parking.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/parking")
@Tag(name = "Parking", description = "Entrees, sorties, occupation et chiffre d'affaires")
public class ParkingRestController {

    private final ParkingService parkingService;
    private final StatisticsService statisticsService;
    private final CurrentUserProvider currentUserProvider;

    public ParkingRestController(ParkingService parkingService,
                                 StatisticsService statisticsService,
                                 CurrentUserProvider currentUserProvider) {
        this.parkingService = parkingService;
        this.statisticsService = statisticsService;
        this.currentUserProvider = currentUserProvider;
    }

    @PostMapping("/entries")
    @Operation(summary = "Enregistre une entree de vehicule et attribue une place")
    public ResponseEntity<ParkingSessionResponse> registerEntry(@Valid @RequestBody EntryRequest request) {
        ParkingSessionResponse session = parkingService.registerEntry(request, currentUserProvider.username());
        return ResponseEntity
                .created(URI.create("/api/parking/sessions/" + session.id()))
                .body(session);
    }

    @PostMapping("/exits")
    @Operation(summary = "Enregistre une sortie, libere la place et emet la facture")
    public ResponseEntity<ParkingSessionResponse> registerExit(@Valid @RequestBody ExitRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(parkingService.registerExit(request, currentUserProvider.username()));
    }

    @GetMapping("/sessions")
    @Operation(summary = "Historique pagine des entrees / sorties")
    public ResponseEntity<PageResponse<ParkingSessionResponse>> sessions(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) SessionStatus status,
            @RequestParam(required = false) String zone,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime to,
            @PageableDefault(size = 20, sort = "entryTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ParkingSessionResponse> page = parkingService.search(search, status, zone, from, to, pageable);
        return ResponseEntity.ok(PageResponse.of(page));
    }

    @GetMapping("/sessions/ongoing")
    @Operation(summary = "Vehicules actuellement dans le parking")
    public ResponseEntity<List<ParkingSessionResponse>> ongoing() {
        return ResponseEntity.ok(parkingService.findOngoing());
    }

    @GetMapping("/sessions/{id}")
    @Operation(summary = "Detail d'une session de stationnement")
    public ResponseEntity<ParkingSessionResponse> session(@PathVariable Long id) {
        return ResponseEntity.ok(parkingService.getById(id));
    }

    @GetMapping("/occupancy")
    @Operation(summary = "Taux d'occupation global et par zone")
    public ResponseEntity<OccupancyResponse> occupancy() {
        return ResponseEntity.ok(statisticsService.occupancy());
    }

    @GetMapping("/revenue")
    @Operation(summary = "Chiffre d'affaires sur une periode")
    public ResponseEntity<RevenueResponse> revenue(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(statisticsService.revenue(from, to));
    }
}
