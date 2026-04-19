package com.delissa.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private Double price;
    private String type;
    private String image;

    private Integer stock;
    private Boolean available;
    private String description;
    private Double cost;

    // 🔥 RELATIONSHIP
    @OneToMany(mappedBy = "product")
    private List<SaleDetail> details;

    // getters and setters
}