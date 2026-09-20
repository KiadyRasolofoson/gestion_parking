package com.example.parking.service.impl;

import com.example.parking.dto.request.TariffRuleRequest;
import com.example.parking.dto.response.TariffRuleResponse;
import com.example.parking.entity.TariffRule;
import com.example.parking.entity.enums.SpaceType;
import com.example.parking.entity.enums.UserCategory;
import com.example.parking.entity.enums.VehicleType;
import com.example.parking.exception.ConflictException;
import com.example.parking.exception.ResourceNotFoundException;
import com.example.parking.mapper.TariffRuleMapper;
import com.example.parking.repository.TariffRuleRepository;
import com.example.parking.service.TariffService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class TariffServiceImpl implements TariffService {

    private final TariffRuleRepository tariffRuleRepository;
    private final TariffRuleMapper tariffRuleMapper;

    public TariffServiceImpl(TariffRuleRepository tariffRuleRepository, TariffRuleMapper tariffRuleMapper) {
        this.tariffRuleRepository = tariffRuleRepository;
        this.tariffRuleMapper = tariffRuleMapper;
    }

    @Override
    public Page<TariffRuleResponse> findAll(Pageable pageable) {
        return tariffRuleRepository.findAll(pageable).map(tariffRuleMapper::toResponse);
    }

    @Override
    public List<TariffRuleResponse> findActive() {
        return tariffRuleRepository.findByActiveTrueOrderByPriorityDesc().stream()
                .map(tariffRuleMapper::toResponse)
                .toList();
    }

    @Override
    public TariffRuleResponse getById(Long id) {
        return tariffRuleMapper.toResponse(getEntity(id));
    }

    @Override
    @Transactional
    public TariffRuleResponse create(TariffRuleRequest request) {
        if (tariffRuleRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new ConflictException("Une regle tarifaire nommee " + request.getName() + " existe deja");
        }
        TariffRule rule = tariffRuleMapper.toEntity(request);
        return tariffRuleMapper.toResponse(tariffRuleRepository.save(rule));
    }

    @Override
    @Transactional
    public TariffRuleResponse update(Long id, TariffRuleRequest request) {
        TariffRule rule = getEntity(id);
        if (tariffRuleRepository.existsByNameIgnoreCaseAndIdNot(request.getName().trim(), id)) {
            throw new ConflictException("Une regle tarifaire nommee " + request.getName() + " existe deja");
        }
        tariffRuleMapper.applyTo(rule, request);
        return tariffRuleMapper.toResponse(tariffRuleRepository.save(rule));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        tariffRuleRepository.delete(getEntity(id));
    }

    /**
     * Selectionne la regle active la plus specifique : priorite decroissante,
     * puis nombre de criteres renseignes decroissant.
     */
    @Override
    public TariffRule findApplicable(String zone, SpaceType spaceType, VehicleType vehicleType,
                                     UserCategory category) {
        return tariffRuleRepository.findByActiveTrueOrderByPriorityDesc().stream()
                .filter(rule -> rule.matches(zone, spaceType, vehicleType, category))
                .max(Comparator.comparingInt(TariffRule::getPriority)
                        .thenComparingInt(TariffServiceImpl::specificity))
                .orElse(null);
    }

    private static int specificity(TariffRule rule) {
        int score = 0;
        if (rule.getZone() != null) {
            score++;
        }
        if (rule.getSpaceType() != null) {
            score++;
        }
        if (rule.getVehicleType() != null) {
            score++;
        }
        if (rule.getUserCategory() != null) {
            score++;
        }
        return score;
    }

    private TariffRule getEntity(Long id) {
        return tariffRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Regle tarifaire", id));
    }
}
