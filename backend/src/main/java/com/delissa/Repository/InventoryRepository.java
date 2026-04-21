package com.delissa.repository;

import com.delissa.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import com.delissa.model.Inventory;
import com.delissa.repository.InventoryRepository;

import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    // 🔥 buscar por rango de fechas
    List<Inventory> findByLastUpdateBetween(LocalDateTime start, LocalDateTime end);

    // 🔥 buscar por producto
    List<Inventory> findByProductId(Long productId);
}