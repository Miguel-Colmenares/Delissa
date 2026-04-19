package com.delissa.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer quantity;
    private LocalDateTime lastUpdate;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // getters and setters
}