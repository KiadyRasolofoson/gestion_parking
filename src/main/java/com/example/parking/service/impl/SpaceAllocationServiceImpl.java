package com.example.parking.service.impl;

import com.example.parking.config.ParkingProperties;
import com.example.parking.entity.ParkingSpace;
import com.example.parking.entity.enums.SpaceStatus;
import com.example.parking.entity.enums.SpaceType;
import com.example.parking.entity.enums.UserCategory;
import com.example.parking.exception.BusinessException;
import com.example.parking.repository.ParkingSpaceRepository;
import com.example.parking.service.SpaceAllocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class SpaceAllocationServiceImpl implements SpaceAllocationService {

    private static final Logger log = LoggerFactory.getLogger(SpaceAllocationServiceImpl.class);

    private final ParkingSpaceRepository spaceRepository;
    private final ParkingProperties properties;

    public SpaceAllocationServiceImpl(ParkingSpaceRepository spaceRepository, ParkingProperties properties) {
        this.spaceRepository = spaceRepository;
        this.properties = properties;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public ParkingSpace allocate(UserCategory category, String preferredZone) {
        UserCategory effectiveCategory = category != null ? category : UserCategory.STANDARD;
        SpaceType requested = effectiveCategory.getPreferredSpaceType();
        List<SpaceType> preference = properties.getAllocation().preferenceFor(requested);
        if (preference == null || preference.isEmpty()) {
            preference = List.of(requested, SpaceType.STANDARD);
        }

        String zone = StringUtils.hasText(preferredZone) ? preferredZone.trim() : null;

        // 1. Recherche dans la zone souhaitee, en respectant l'ordre de preference
        if (zone != null) {
            for (SpaceType type : preference) {
                Optional<ParkingSpace> candidate = lockIfStillFree(
                        spaceRepository.findFirstByTypeAndStatusAndZoneIgnoreCaseOrderByNumberAsc(
                                type, SpaceStatus.LIBRE, zone));
                if (candidate.isPresent()) {
                    log.debug("Place {} attribuee (type {}, zone {})", candidate.get().getNumber(), type, zone);
                    return candidate.get();
                }
            }
        }

        // 2. Recherche sur tout le parking
        for (SpaceType type : preference) {
            Optional<ParkingSpace> candidate = lockIfStillFree(
                    spaceRepository.findFirstByTypeAndStatusOrderByNumberAsc(type, SpaceStatus.LIBRE));
            if (candidate.isPresent()) {
                log.debug("Place {} attribuee (type {})", candidate.get().getNumber(), type);
                return candidate.get();
            }
        }

        throw new BusinessException("Aucune place compatible disponible pour la categorie "
                + effectiveCategory.getLabel()
                + (zone != null ? " (zone " + zone + ")" : ""));
    }

    /**
     * Reprend la place avec un verrou pessimiste et verifie qu'elle est encore
     * libre : la premiere lecture n'est pas verrouillee, une autre transaction
     * peut l'avoir prise entre temps.
     */
    private Optional<ParkingSpace> lockIfStillFree(Optional<ParkingSpace> candidate) {
        if (candidate.isEmpty()) {
            return Optional.empty();
        }
        Optional<ParkingSpace> locked = spaceRepository.findByIdForUpdate(candidate.get().getId());
        if (locked.isPresent() && locked.get().getStatus() == SpaceStatus.LIBRE) {
            return locked;
        }
        return Optional.empty();
    }
}
