package com.delissa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.delissa.model.Sale;
import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Integer> {
    List<Sale> findByDateBetween(LocalDateTime start, LocalDateTime end);
}
