package com.example.parking.entity;

import com.example.parking.entity.enums.ReservationStatus;
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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "reservations",
        uniqueConstraints = @UniqueConstraint(name = "uk_reservation_reference", columnNames = "reference"),
        indexes = {
                @Index(name = "idx_reservation_status", columnList = "status"),
                @Index(name = "idx_reservation_space", columnList = "space_id"),
                @Index(name = "idx_reservation_arrival", columnList = "expected_arrival")
        })
public class Reservation extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "space_id", nullable = false)
    private ParkingSpace space;

    @Column(name = "expected_arrival", nullable = false)
    private LocalDateTime expectedArrival;

    @Column(name = "expected_departure", nullable = false)
    private LocalDateTime expectedDeparture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status = ReservationStatus.CONFIRMEE;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancel_reason", length = 255)
    private String cancelReason;

    @Column(length = 500)
    private String notes;

    public Reservation() {
    }

    public long getExpectedDurationMinutes() {
        if (expectedArrival == null || expectedDeparture == null) {
            return 0L;
        }
        return Duration.between(expectedArrival, expectedDeparture).toMinutes();
    }

    /** Deux reservations se chevauchent si [debut, fin[ se croisent. */
    public boolean overlaps(LocalDateTime from, LocalDateTime to) {
        return expectedArrival.isBefore(to) && from.isBefore(expectedDeparture);
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public ParkingSpace getSpace() {
        return space;
    }

    public void setSpace(ParkingSpace space) {
        this.space = space;
    }

    public LocalDateTime getExpectedArrival() {
        return expectedArrival;
    }

    public void setExpectedArrival(LocalDateTime expectedArrival) {
        this.expectedArrival = expectedArrival;
    }

    public LocalDateTime getExpectedDeparture() {
        return expectedDeparture;
    }

    public void setExpectedDeparture(LocalDateTime expectedDeparture) {
        this.expectedDeparture = expectedDeparture;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reservation other)) {
            return false;
        }
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
