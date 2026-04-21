package com.delissa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.delissa.model.Sale;

public interface SaleRepository extends JpaRepository<Sale, Integer> {
}