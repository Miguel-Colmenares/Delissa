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

    // 🔥 RELACIÓN PRODUCTO
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // 🔥 NUEVO: RELACIÓN USUARIO
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 🔥 GETTERS Y SETTERS

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    // 🔥 USER
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}