package com.example.parking.entity;

import com.example.parking.entity.enums.InvoiceStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "invoices",
        uniqueConstraints = @UniqueConstraint(name = "uk_invoice_reference", columnNames = "reference"),
        indexes = {
                @Index(name = "idx_invoice_status", columnList = "status"),
                @Index(name = "idx_invoice_issued", columnList = "issued_at")
        })
public class Invoice extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String reference;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false, unique = true)
    private ParkingSession session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Duree facturee en minutes (apres arrondi a l'increment de facturation). */
    @Column(name = "billed_minutes", nullable = false)
    private long billedMinutes;

    /** Duree prevue par la reservation, en minutes (0 si entree sans reservation). */
    @Column(name = "expected_minutes", nullable = false)
    private long expectedMinutes;

    /** Minutes de depassement au-dela de la reservation. */
    @Column(name = "overstay_minutes", nullable = false)
    private long overstayMinutes;

    @Column(name = "base_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal baseAmount = BigDecimal.ZERO;

    @Column(name = "overstay_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal overstayAmount = BigDecimal.ZERO;

    @Column(name = "discount_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "applied_tariff", length = 120)
    private String appliedTariff;

    @Column(length = 1000)
    private String details;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InvoiceStatus status = InvoiceStatus.EN_ATTENTE;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    public Invoice() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public ParkingSession getSession() {
        return session;
    }

    public void setSession(ParkingSession session) {
        this.session = session;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getBilledMinutes() {
        return billedMinutes;
    }

    public void setBilledMinutes(long billedMinutes) {
        this.billedMinutes = billedMinutes;
    }

    public long getExpectedMinutes() {
        return expectedMinutes;
    }

    public void setExpectedMinutes(long expectedMinutes) {
        this.expectedMinutes = expectedMinutes;
    }

    public long getOverstayMinutes() {
        return overstayMinutes;
    }

    public void setOverstayMinutes(long overstayMinutes) {
        this.overstayMinutes = overstayMinutes;
    }

    public BigDecimal getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(BigDecimal baseAmount) {
        this.baseAmount = baseAmount;
    }

    public BigDecimal getOverstayAmount() {
        return overstayAmount;
    }

    public void setOverstayAmount(BigDecimal overstayAmount) {
        this.overstayAmount = overstayAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getAppliedTariff() {
        return appliedTariff;
    }

    public void setAppliedTariff(String appliedTariff) {
        this.appliedTariff = appliedTariff;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Invoice other)) {
            return false;
        }
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
