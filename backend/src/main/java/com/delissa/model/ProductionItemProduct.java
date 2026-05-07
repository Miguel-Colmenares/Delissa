package com.delissa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "production_item_product")
public class ProductionItemProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "production_item_id")
    @JsonIgnore
    private ProductionItem productionItem;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Double quantityPerProduct;

    public ProductionItemProduct() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductionItem getProductionItem() {
        return productionItem;
    }

    public void setProductionItem(ProductionItem productionItem) {
        this.productionItem = productionItem;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Double getQuantityPerProduct() {
        return quantityPerProduct;
    }

    public void setQuantityPerProduct(Double quantityPerProduct) {
        this.quantityPerProduct = quantityPerProduct;
    }
}
