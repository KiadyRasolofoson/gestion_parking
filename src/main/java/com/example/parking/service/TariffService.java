package com.example.parking.service;

import com.example.parking.dto.request.TariffRuleRequest;
import com.example.parking.dto.response.TariffRuleResponse;
import com.example.parking.entity.TariffRule;
import com.example.parking.entity.enums.SpaceType;
import com.example.parking.entity.enums.UserCategory;
import com.example.parking.entity.enums.VehicleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TariffService {

    Page<TariffRuleResponse> findAll(Pageable pageable);

    List<TariffRuleResponse> findActive();

    TariffRuleResponse getById(Long id);

    TariffRuleResponse create(TariffRuleRequest request);

    TariffRuleResponse update(Long id, TariffRuleRequest request);

    void delete(Long id);

    /** Regle active de plus forte priorite correspondant au contexte, ou null. */
    TariffRule findApplicable(String zone, SpaceType spaceType, VehicleType vehicleType, UserCategory category);
}
