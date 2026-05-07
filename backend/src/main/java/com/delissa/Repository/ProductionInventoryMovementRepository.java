package com.delissa.repository;

import com.delissa.model.ProductionInventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductionInventoryMovementRepository extends JpaRepository<ProductionInventoryMovement, Long> {
    List<ProductionInventoryMovement> findByLastUpdateBetween(LocalDateTime start, LocalDateTime end);
    List<ProductionInventoryMovement> findByProductionItemId(Long productionItemId);
}
