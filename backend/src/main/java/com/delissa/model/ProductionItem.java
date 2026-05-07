package com.delissa.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@Entity
@Table(name = "production_item")
public class ProductionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String category;
    private String unit;
    private Double stock;
    private Double minStock;
    private Double cost;
    private String supplier;
    private String supplierContact;
    private String packageName;
    private Double unitsPerPackage;
    private String notes;
    private LocalDateTime lastUpdate;

    @OneToMany(mappedBy = "productionItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductionItemProduct> productLinks = new ArrayList<>();

    public ProductionItem() {}

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getStock() {
        return stock;
    }

    public void setStock(Double stock) {
        this.stock = stock;
    }

    public Double getMinStock() {
        return minStock;
    }

    public void setMinStock(Double minStock) {
        this.minStock = minStock;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getSupplierContact() {
        return supplierContact;
    }

    public void setSupplierContact(String supplierContact) {
        this.supplierContact = supplierContact;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Double getUnitsPerPackage() {
        return unitsPerPackage;
    }

    public void setUnitsPerPackage(Double unitsPerPackage) {
        this.unitsPerPackage = unitsPerPackage;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public List<ProductionItemProduct> getProductLinks() {
        return productLinks;
    }

    public void setProductLinks(List<ProductionItemProduct> productLinks) {
        List<ProductionItemProduct> incoming = productLinks == null ? null : new ArrayList<>(productLinks);
        this.productLinks.clear();
        if (incoming != null) {
            for (ProductionItemProduct link : incoming) {
                addProductLink(link);
            }
        }
    }

    public void addProductLink(ProductionItemProduct link) {
        ProductionItemProduct copy = new ProductionItemProduct();
        copy.setProduct(link.getProduct());
        copy.setQuantityPerProduct(link.getQuantityPerProduct());
        copy.setProductionItem(this);
        this.productLinks.add(copy);
    }
}
