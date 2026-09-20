package com.example.parking.controller.rest;

import com.example.parking.dto.request.TariffRuleRequest;
import com.example.parking.dto.response.PageResponse;
import com.example.parking.dto.response.TariffRuleResponse;
import com.example.parking.service.TariffService;
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
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tariffs")
@Tag(name = "Tarification", description = "Regles tarifaires configurables (ADMIN)")
public class TariffRestController {

    private final TariffService tariffService;

    public TariffRestController(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @GetMapping
    @Operation(summary = "Liste paginee des regles tarifaires")
    public ResponseEntity<PageResponse<TariffRuleResponse>> list(
            @PageableDefault(size = 20, sort = "priority", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TariffRuleResponse> page = tariffService.findAll(pageable);
        return ResponseEntity.ok(PageResponse.of(page));
    }

    @GetMapping("/active")
    @Operation(summary = "Regles actives, par priorite decroissante")
    public ResponseEntity<List<TariffRuleResponse>> active() {
        return ResponseEntity.ok(tariffService.findActive());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail d'une regle tarifaire")
    public ResponseEntity<TariffRuleResponse> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(tariffService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Cree une regle tarifaire")
    public ResponseEntity<TariffRuleResponse> create(@Valid @RequestBody TariffRuleRequest request) {
        TariffRuleResponse created = tariffService.create(request);
        return ResponseEntity.created(URI.create("/api/tariffs/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Met a jour une regle tarifaire")
    public ResponseEntity<TariffRuleResponse> update(@PathVariable Long id,
                                                     @Valid @RequestBody TariffRuleRequest request) {
        return ResponseEntity.ok(tariffService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprime une regle tarifaire")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tariffService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
