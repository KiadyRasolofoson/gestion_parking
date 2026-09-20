package com.example.parking.controller.rest;

import com.example.parking.dto.response.InvoiceResponse;
import com.example.parking.dto.response.PageResponse;
import com.example.parking.entity.enums.InvoiceStatus;
import com.example.parking.service.BillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/invoices")
@Tag(name = "Facturation", description = "Factures emises a la sortie du parking")
public class InvoiceRestController {

    private final BillingService billingService;

    public InvoiceRestController(BillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping
    @Operation(summary = "Liste paginee des factures")
    public ResponseEntity<PageResponse<InvoiceResponse>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) InvoiceStatus status,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime to,
            @PageableDefault(size = 20, sort = "issuedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<InvoiceResponse> page = billingService.search(search, status, from, to, pageable);
        return ResponseEntity.ok(PageResponse.of(page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail d'une facture")
    public ResponseEntity<InvoiceResponse> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(billingService.getById(id));
    }

    @PostMapping("/{id}/pay")
    @Operation(summary = "Marque une facture comme reglee")
    public ResponseEntity<InvoiceResponse> pay(@PathVariable Long id) {
        return ResponseEntity.ok(billingService.markAsPaid(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Annule une facture non reglee")
    public ResponseEntity<InvoiceResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(billingService.cancel(id));
    }
}
