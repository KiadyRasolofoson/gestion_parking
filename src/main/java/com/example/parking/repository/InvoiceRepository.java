package com.example.parking.repository;

import com.example.parking.entity.Invoice;
import com.example.parking.entity.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {

    Optional<Invoice> findByReference(String reference);

    Optional<Invoice> findBySessionId(Long sessionId);

    List<Invoice> findByUserIdOrderByIssuedAtDesc(Long userId);

    long countByStatus(InvoiceStatus status);

    @Query("""
            select coalesce(sum(i.totalAmount), 0)
            from Invoice i
            where i.status <> :excludedStatus
              and i.issuedAt >= :from and i.issuedAt < :to
            """)
    BigDecimal sumRevenueBetween(@Param("from") LocalDateTime from,
                                 @Param("to") LocalDateTime to,
                                 @Param("excludedStatus") InvoiceStatus excludedStatus);

    @Query("""
            select i from Invoice i
            where i.status <> :excludedStatus
              and i.issuedAt >= :from and i.issuedAt < :to
            order by i.issuedAt asc
            """)
    List<Invoice> findBillableBetween(@Param("from") LocalDateTime from,
                                      @Param("to") LocalDateTime to,
                                      @Param("excludedStatus") InvoiceStatus excludedStatus);

    @Query("""
            select coalesce(sum(i.totalAmount), 0)
            from Invoice i
            where i.status = :status
            """)
    BigDecimal sumByStatus(@Param("status") InvoiceStatus status);
}
