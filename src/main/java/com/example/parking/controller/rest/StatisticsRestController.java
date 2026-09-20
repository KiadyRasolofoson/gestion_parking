package com.example.parking.controller.rest;

import com.example.parking.dto.response.DashboardResponse;
import com.example.parking.dto.response.OccupancyResponse;
import com.example.parking.dto.response.RevenueResponse;
import com.example.parking.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/statistics")
@Tag(name = "Statistiques", description = "Indicateurs d'occupation et de revenus")
public class StatisticsRestController {

    private final StatisticsService statisticsService;

    public StatisticsRestController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Indicateurs du tableau de bord")
    public ResponseEntity<DashboardResponse> dashboard() {
        return ResponseEntity.ok(statisticsService.dashboard());
    }

    @GetMapping("/occupancy")
    @Operation(summary = "Occupation globale et par zone")
    public ResponseEntity<OccupancyResponse> occupancy() {
        return ResponseEntity.ok(statisticsService.occupancy());
    }

    @GetMapping("/revenue")
    @Operation(summary = "Chiffre d'affaires sur une periode (par defaut le mois en cours)")
    public ResponseEntity<RevenueResponse> revenue(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(statisticsService.revenue(from, to));
    }
}
