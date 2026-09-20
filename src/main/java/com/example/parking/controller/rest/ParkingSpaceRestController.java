package com.example.parking.controller.rest;

import com.example.parking.dto.request.CreateSpaceRequest;
import com.example.parking.dto.request.UpdateSpaceRequest;
import com.example.parking.dto.response.PageResponse;
import com.example.parking.dto.response.ParkingSpaceResponse;
import com.example.parking.entity.enums.SpaceStatus;
import com.example.parking.entity.enums.SpaceType;
import com.example.parking.service.ParkingSpaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/parking/spaces")
@Tag(name = "Places", description = "Gestion des places de parking")
public class ParkingSpaceRestController {

    private final ParkingSpaceService spaceService;

    public ParkingSpaceRestController(ParkingSpaceService spaceService) {
        this.spaceService = spaceService;
    }

    @GetMapping
    @Operation(summary = "Liste paginee des places avec recherche, filtres et tri")
    public ResponseEntity<PageResponse<ParkingSpaceResponse>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String zone,
            @RequestParam(required = false) SpaceType type,
            @RequestParam(required = false) SpaceStatus status,
            @RequestParam(required = false) Integer floor,
            @PageableDefault(size = 20, sort = {"zone", "number"}, direction = Sort.Direction.ASC)
            Pageable pageable) {
        Page<ParkingSpaceResponse> page = spaceService.search(search, zone, type, status, floor, pageable);
        return ResponseEntity.ok(PageResponse.of(page));
    }

    @GetMapping("/available")
    @Operation(summary = "Places libres, filtrables par type et par zone")
    public ResponseEntity<List<ParkingSpaceResponse>> available(
            @RequestParam(required = false) SpaceType type,
            @RequestParam(required = false) String zone) {
        return ResponseEntity.ok(spaceService.findAvailable(type, zone));
    }

    @GetMapping("/zones")
    @Operation(summary = "Liste des zones existantes")
    public ResponseEntity<List<String>> zones() {
        return ResponseEntity.ok(spaceService.findZones());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail d'une place")
    public ResponseEntity<ParkingSpaceResponse> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(spaceService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Cree une place (ADMIN)")
    public ResponseEntity<ParkingSpaceResponse> create(@Valid @RequestBody CreateSpaceRequest request) {
        ParkingSpaceResponse created = spaceService.create(request);
        return ResponseEntity
                .created(URI.create("/api/parking/spaces/" + created.id()))
                .body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Met a jour une place (ADMIN)")
    public ResponseEntity<ParkingSpaceResponse> update(@PathVariable Long id,
                                                       @Valid @RequestBody UpdateSpaceRequest request) {
        return ResponseEntity.ok(spaceService.update(id, request));
    }

    @PostMapping("/{id}/out-of-service")
    @Operation(summary = "Met une place hors service (ADMIN)")
    public ResponseEntity<ParkingSpaceResponse> outOfService(@PathVariable Long id,
                                                             @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(spaceService.setOutOfService(id, reason));
    }

    @PostMapping("/{id}/in-service")
    @Operation(summary = "Remet une place en service (ADMIN)")
    public ResponseEntity<ParkingSpaceResponse> inService(@PathVariable Long id) {
        return ResponseEntity.ok(spaceService.setInService(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprime une place (ADMIN)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        spaceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
