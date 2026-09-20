package com.example.parking.service.impl;

import com.example.parking.config.ParkingProperties;
import com.example.parking.dto.response.DashboardResponse;
import com.example.parking.dto.response.OccupancyResponse;
import com.example.parking.dto.response.RevenueResponse;
import com.example.parking.entity.Invoice;
import com.example.parking.entity.enums.InvoiceStatus;
import com.example.parking.entity.enums.ReservationStatus;
import com.example.parking.entity.enums.SessionStatus;
import com.example.parking.entity.enums.SpaceStatus;
import com.example.parking.entity.enums.SpaceType;
import com.example.parking.entity.enums.SubscriptionStatus;
import com.example.parking.exception.BusinessException;
import com.example.parking.repository.InvoiceRepository;
import com.example.parking.repository.ParkingSessionRepository;
import com.example.parking.repository.ParkingSpaceRepository;
import com.example.parking.repository.ReservationRepository;
import com.example.parking.repository.SubscriptionRepository;
import com.example.parking.service.StatisticsService;
import com.example.parking.util.DateTimeUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class StatisticsServiceImpl implements StatisticsService {

    private final ParkingSpaceRepository spaceRepository;
    private final ParkingSessionRepository sessionRepository;
    private final ReservationRepository reservationRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final InvoiceRepository invoiceRepository;
    private final ParkingProperties properties;

    public StatisticsServiceImpl(ParkingSpaceRepository spaceRepository,
                                 ParkingSessionRepository sessionRepository,
                                 ReservationRepository reservationRepository,
                                 SubscriptionRepository subscriptionRepository,
                                 InvoiceRepository invoiceRepository,
                                 ParkingProperties properties) {
        this.spaceRepository = spaceRepository;
        this.sessionRepository = sessionRepository;
        this.reservationRepository = reservationRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.invoiceRepository = invoiceRepository;
        this.properties = properties;
    }

    @Override
    public DashboardResponse dashboard() {
        long total = spaceRepository.count();
        long free = spaceRepository.countByStatus(SpaceStatus.LIBRE);
        long occupied = spaceRepository.countByStatus(SpaceStatus.OCCUPEE);
        long reserved = spaceRepository.countByStatus(SpaceStatus.RESERVEE);
        long outOfService = spaceRepository.countByStatus(SpaceStatus.HORS_SERVICE);

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = DateTimeUtils.startOfDay(today);
        LocalDateTime startOfTomorrow = DateTimeUtils.endOfDayExclusive(today);
        LocalDateTime startOfMonth = DateTimeUtils.startOfDay(today.withDayOfMonth(1));
        LocalDateTime startOfNextMonth = DateTimeUtils.startOfDay(today.withDayOfMonth(1).plusMonths(1));

        BigDecimal revenueToday = nullSafe(invoiceRepository.sumRevenueBetween(startOfDay, startOfTomorrow,
                InvoiceStatus.ANNULEE));
        BigDecimal revenueMonth = nullSafe(invoiceRepository.sumRevenueBetween(startOfMonth, startOfNextMonth,
                InvoiceStatus.ANNULEE));

        return new DashboardResponse(
                total,
                free,
                occupied,
                reserved,
                outOfService,
                occupancyRate(total, occupied, outOfService),
                sessionRepository.countByStatus(SessionStatus.EN_COURS),
                sessionRepository.countEntriesBetween(startOfDay, startOfTomorrow),
                sessionRepository.countExitsBetween(startOfDay, startOfTomorrow),
                reservationRepository.countByStatus(ReservationStatus.CONFIRMEE),
                subscriptionRepository.countByStatus(SubscriptionStatus.ACTIF),
                revenueToday,
                revenueMonth,
                properties.getPricing().getCurrency());
    }

    @Override
    public OccupancyResponse occupancy() {
        long total = spaceRepository.count();
        long free = spaceRepository.countByStatus(SpaceStatus.LIBRE);
        long reserved = spaceRepository.countByStatus(SpaceStatus.RESERVEE);
        long occupied = spaceRepository.countByStatus(SpaceStatus.OCCUPEE);
        long outOfService = spaceRepository.countByStatus(SpaceStatus.HORS_SERVICE);

        Map<String, Long> countByType = new LinkedHashMap<>();
        for (SpaceType type : SpaceType.values()) {
            countByType.put(type.name(), spaceRepository.countByType(type));
        }

        Map<String, Long> countByStatus = new LinkedHashMap<>();
        for (SpaceStatus status : SpaceStatus.values()) {
            countByStatus.put(status.name(), spaceRepository.countByStatus(status));
        }

        List<OccupancyResponse.ZoneOccupancy> zones = new ArrayList<>();
        for (String zone : spaceRepository.findDistinctZones()) {
            long zoneTotal = spaceRepository.countByZone(zone);
            long zoneFree = spaceRepository.countByZoneAndStatus(zone, SpaceStatus.LIBRE);
            long zoneOccupied = spaceRepository.countByZoneAndStatus(zone, SpaceStatus.OCCUPEE);
            long zoneOutOfService = spaceRepository.countByZoneAndStatus(zone, SpaceStatus.HORS_SERVICE);
            zones.add(new OccupancyResponse.ZoneOccupancy(zone, zoneTotal, zoneFree, zoneOccupied,
                    occupancyRate(zoneTotal, zoneOccupied, zoneOutOfService)));
        }

        return new OccupancyResponse(total, free, reserved, occupied, outOfService,
                occupancyRate(total, occupied, outOfService), countByType, countByStatus, zones);
    }

    @Override
    public RevenueResponse revenue(LocalDate from, LocalDate to) {
        LocalDate start = from != null ? from : LocalDate.now().withDayOfMonth(1);
        LocalDate end = to != null ? to : LocalDate.now();
        if (end.isBefore(start)) {
            throw new BusinessException("La date de fin doit etre posterieure a la date de debut");
        }

        List<Invoice> invoices = invoiceRepository.findBillableBetween(
                DateTimeUtils.startOfDay(start),
                DateTimeUtils.endOfDayExclusive(end),
                InvoiceStatus.ANNULEE);

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal parkingRevenue = BigDecimal.ZERO;
        BigDecimal overstayRevenue = BigDecimal.ZERO;
        BigDecimal discountGranted = BigDecimal.ZERO;
        Map<LocalDate, BigDecimal> amountByDay = new LinkedHashMap<>();
        Map<LocalDate, Long> countByDay = new LinkedHashMap<>();

        for (Invoice invoice : invoices) {
            totalRevenue = totalRevenue.add(invoice.getTotalAmount());
            parkingRevenue = parkingRevenue.add(invoice.getBaseAmount());
            overstayRevenue = overstayRevenue.add(invoice.getOverstayAmount());
            discountGranted = discountGranted.add(invoice.getDiscountAmount());
            LocalDate day = invoice.getIssuedAt().toLocalDate();
            amountByDay.merge(day, invoice.getTotalAmount(), BigDecimal::add);
            countByDay.merge(day, 1L, Long::sum);
        }

        List<RevenueResponse.DailyRevenue> daily = new ArrayList<>();
        for (LocalDate day = start; !day.isAfter(end); day = day.plusDays(1)) {
            daily.add(new RevenueResponse.DailyRevenue(
                    day,
                    countByDay.getOrDefault(day, 0L),
                    amountByDay.getOrDefault(day, BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP)));
        }

        long invoiceCount = invoices.size();
        BigDecimal averageTicket = invoiceCount == 0
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : totalRevenue.divide(BigDecimal.valueOf(invoiceCount), 2, RoundingMode.HALF_UP);

        return new RevenueResponse(
                start,
                end,
                properties.getPricing().getCurrency(),
                totalRevenue.setScale(2, RoundingMode.HALF_UP),
                parkingRevenue.setScale(2, RoundingMode.HALF_UP),
                overstayRevenue.setScale(2, RoundingMode.HALF_UP),
                discountGranted.setScale(2, RoundingMode.HALF_UP),
                invoiceCount,
                averageTicket,
                daily);
    }

    /** Taux d'occupation calcule sur les places exploitables (hors service exclues). */
    private double occupancyRate(long total, long occupied, long outOfService) {
        long usable = total - outOfService;
        if (usable <= 0) {
            return 0d;
        }
        return Math.round((occupied * 10000d) / usable) / 100d;
    }

    private BigDecimal nullSafe(BigDecimal value) {
        return value != null ? value.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2,
                RoundingMode.HALF_UP);
    }
}
