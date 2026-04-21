package com.delissa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.delissa.model.SaleDetail;

public interface SaleDetailRepository extends JpaRepository<SaleDetail, Integer> {
}