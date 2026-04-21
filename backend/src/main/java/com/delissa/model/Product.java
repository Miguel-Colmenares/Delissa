package com.delissa.model;

import jakarta.persistence.*;

@Entity
@Table(name = "product") // si quieres puedes cambiar a "products"
public class Product{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double price;
    private String img;

    private String category;
    private String subCategory;

    private Integer stock; // 🔥 IMPORTANTE

    // --- CONSTRUCTORES ---
    public Product() {}

    public Product(String name, Double price, String img, String category, String subCategory, Integer stock) {
        this.name = name;
        this.price = price;
        this.img = img;
        this.category = category;
        this.subCategory = subCategory;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    // --- GETTERS Y SETTERS ---
  
}