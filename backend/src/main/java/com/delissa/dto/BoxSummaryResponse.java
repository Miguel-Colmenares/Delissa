package com.delissa.dto;

import com.delissa.model.BoxClosure;
import com.delissa.model.BoxOpening;
import com.delissa.model.Expense;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class BoxSummaryResponse {
    private LocalDate businessDate;
    private Double suggestedStartingCash;
    private Double totalSales;
    private Double cashSales;
    private Double cardSales;
    private Double nequiSales;
    private Double totalExpenses;
    private Integer salesCount;
    private Integer productsSold;
    private Map<String, Integer> products;
    private List<Expense> expenses;
    private BoxOpening currentOpening;
    private List<BoxOpening> openings;
    private List<BoxClosure> closures;

    public LocalDate getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(LocalDate businessDate) {
        this.businessDate = businessDate;
    }

    public Double getSuggestedStartingCash() {
        return suggestedStartingCash;
    }

    public void setSuggestedStartingCash(Double suggestedStartingCash) {
        this.suggestedStartingCash = suggestedStartingCash;
    }

    public Double getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(Double totalSales) {
        this.totalSales = totalSales;
    }

    public Double getCashSales() {
        return cashSales;
    }

    public void setCashSales(Double cashSales) {
        this.cashSales = cashSales;
    }

    public Double getCardSales() {
        return cardSales;
    }

    public void setCardSales(Double cardSales) {
        this.cardSales = cardSales;
    }

    public Double getNequiSales() {
        return nequiSales;
    }

    public void setNequiSales(Double nequiSales) {
        this.nequiSales = nequiSales;
    }

    public Double getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(Double totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public Integer getSalesCount() {
        return salesCount;
    }

    public void setSalesCount(Integer salesCount) {
        this.salesCount = salesCount;
    }

    public Integer getProductsSold() {
        return productsSold;
    }

    public void setProductsSold(Integer productsSold) {
        this.productsSold = productsSold;
    }

    public Map<String, Integer> getProducts() {
        return products;
    }

    public void setProducts(Map<String, Integer> products) {
        this.products = products;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    public BoxOpening getCurrentOpening() {
        return currentOpening;
    }

    public void setCurrentOpening(BoxOpening currentOpening) {
        this.currentOpening = currentOpening;
    }

    public List<BoxOpening> getOpenings() {
        return openings;
    }

    public void setOpenings(List<BoxOpening> openings) {
        this.openings = openings;
    }

    public List<BoxClosure> getClosures() {
        return closures;
    }

    public void setClosures(List<BoxClosure> closures) {
        this.closures = closures;
    }
}
