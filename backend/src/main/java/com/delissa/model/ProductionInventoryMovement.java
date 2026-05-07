package com.delissa.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "production_inventory_movement")
public class ProductionInventoryMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double quantity;
    private String type;
    private String reason;
    private LocalDateTime lastUpdate;

    @ManyToOne
    @JoinColumn(name = "production_item_id")
    private ProductionItem productionItem;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public ProductionInventoryMovement() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public ProductionItem getProductionItem() {
        return productionItem;
    }

    public void setProductionItem(ProductionItem productionItem) {
        this.productionItem = productionItem;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
