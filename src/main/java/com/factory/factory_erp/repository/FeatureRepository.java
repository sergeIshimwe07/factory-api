package com.factory.factory_erp.repository;

import com.factory.factory_erp.entity.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, Long> {
    Optional<Feature> findByFeatureCode(String featureCode);
    boolean existsByFeatureCode(String featureCode);
}
