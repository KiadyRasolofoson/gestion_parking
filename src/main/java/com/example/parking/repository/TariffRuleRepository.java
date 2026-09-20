package com.example.parking.repository;

import com.example.parking.entity.TariffRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TariffRuleRepository extends JpaRepository<TariffRule, Long>,
        JpaSpecificationExecutor<TariffRule> {

    List<TariffRule> findByActiveTrueOrderByPriorityDesc();

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
