package com.example.parking.service;

import com.example.parking.dto.response.InvoiceResponse;
import com.example.parking.entity.Invoice;
import com.example.parking.entity.ParkingSession;
import com.example.parking.entity.Subscription;
import com.example.parking.entity.enums.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface BillingService {

    /**
     * Emet la facture d'une session terminee. Appele dans la transaction de
     * sortie du parking.
     */
    Invoice issueInvoice(ParkingSession session, Subscription subscription, boolean markAsPaid);

    Page<InvoiceResponse> search(String term, InvoiceStatus status, LocalDateTime from, LocalDateTime to,
                                 Pageable pageable);

    InvoiceResponse getById(Long id);

    InvoiceResponse markAsPaid(Long id);

    InvoiceResponse cancel(Long id);

    String currency();
}
