package com.delissa.repository;

import com.delissa.model.ProductionItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionItemRepository extends JpaRepository<ProductionItem, Long> {
}
