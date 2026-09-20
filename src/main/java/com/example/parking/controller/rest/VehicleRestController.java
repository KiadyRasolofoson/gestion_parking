package com.example.parking.controller.rest;

import com.example.parking.dto.request.CreateVehicleRequest;
import com.example.parking.dto.request.UpdateVehicleRequest;
import com.example.parking.dto.response.PageResponse;
import com.example.parking.dto.response.VehicleResponse;
import com.example.parking.entity.enums.VehicleType;
import com.example.parking.service.VehicleService;
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
@RequestMapping("/api/vehicles")
@Tag(name = "Vehicules", description = "Vehicules rattaches aux utilisateurs")
public class VehicleRestController {

    private final VehicleService vehicleService;

    public VehicleRestController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    @Operation(summary = "Liste paginee des vehicules")
    public ResponseEntity<PageResponse<VehicleResponse>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) VehicleType type,
            @RequestParam(required = false) Long ownerId,
            @PageableDefault(size = 20, sort = "plateNumber", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<VehicleResponse> page = vehicleService.search(search, type, ownerId, pageable);
        return ResponseEntity.ok(PageResponse.of(page));
    }

    @GetMapping("/by-owner/{ownerId}")
    @Operation(summary = "Vehicules d'un utilisateur")
    public ResponseEntity<List<VehicleResponse>> byOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(vehicleService.findByOwner(ownerId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail d'un vehicule")
    public ResponseEntity<VehicleResponse> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Enregistre un vehicule")
    public ResponseEntity<VehicleResponse> create(@Valid @RequestBody CreateVehicleRequest request) {
        VehicleResponse created = vehicleService.create(request);
        return ResponseEntity.created(URI.create("/api/vehicles/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Met a jour un vehicule")
    public ResponseEntity<VehicleResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody UpdateVehicleRequest request) {
        return ResponseEntity.ok(vehicleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprime un vehicule")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
