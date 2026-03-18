package com.factory.factory_erp.repository;

import com.factory.factory_erp.entity.CommissionRule;
import com.factory.factory_erp.entity.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommissionRuleRepository extends JpaRepository<CommissionRule, Long> {
    
    Optional<CommissionRule> findByRuleId(String ruleId);
    
    Optional<CommissionRule> findByProductIdAndStatus(Long productId, Status status);
    
    List<CommissionRule> findByStatus(Status status);
}
