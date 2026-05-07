package com.delissa.repository;

import com.delissa.model.ProductionItemProduct;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionItemProductRepository extends JpaRepository<ProductionItemProduct, Long> {
    List<ProductionItemProduct> findByProductId(Long productId);
}
