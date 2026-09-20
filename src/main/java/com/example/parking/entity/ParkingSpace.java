package com.example.parking.entity;

import com.example.parking.entity.enums.SpaceStatus;
import com.example.parking.entity.enums.SpaceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

import java.util.Objects;

@Entity
@Table(name = "parking_spaces",
        uniqueConstraints = @UniqueConstraint(name = "uk_space_number", columnNames = "number"),
        indexes = {
                @Index(name = "idx_space_status", columnList = "status"),
                @Index(name = "idx_space_type", columnList = "type"),
                @Index(name = "idx_space_zone", columnList = "zone")
        })
public class ParkingSpace extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number", nullable = false, length = 15)
    private String number;

    @Column(nullable = false, length = 30)
    private String zone;

    @Column(name = "floor_level", nullable = false)
    private Integer floor = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SpaceType type = SpaceType.STANDARD;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SpaceStatus status = SpaceStatus.LIBRE;

    @Column(name = "out_of_service_reason", length = 255)
    private String outOfServiceReason;

    /** Verrou optimiste : evite deux attributions concurrentes de la meme place. */
    @Version
    private Long version;

    public ParkingSpace() {
    }

    public ParkingSpace(String number, String zone, Integer floor, SpaceType type) {
        this.number = number;
        this.zone = zone;
        this.floor = floor;
        this.type = type;
        this.status = SpaceStatus.LIBRE;
    }

    public boolean isAvailable() {
        return status == SpaceStatus.LIBRE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public SpaceType getType() {
        return type;
    }

    public void setType(SpaceType type) {
        this.type = type;
    }

    public SpaceStatus getStatus() {
        return status;
    }

    public void setStatus(SpaceStatus status) {
        this.status = status;
    }

    public String getOutOfServiceReason() {
        return outOfServiceReason;
    }

    public void setOutOfServiceReason(String outOfServiceReason) {
        this.outOfServiceReason = outOfServiceReason;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParkingSpace other)) {
            return false;
        }
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
