package com.delissa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "box_closures")
public class BoxClosure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate businessDate;
    private LocalDateTime closedAt;

    private Double startingCash;
    private Double totalSales;
    private Double cashSales;
    private Double cardSales;
    private Double nequiSales;
    private Double totalExpenses;
    private Double expectedCash;
    private Double countedCash;
    private Double difference;
    private Double bankWithdrawal;
    private Double remainingCash;

    private Integer salesCount;
    private Integer productsSold;

    @Column(length = 1000)
    private String closingComment;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private User employee;

    public Long getId() {
        return id;
    }

    public LocalDate getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(LocalDate businessDate) {
        this.businessDate = businessDate;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public Double getStartingCash() {
        return startingCash;
    }

    public void setStartingCash(Double startingCash) {
        this.startingCash = startingCash;
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

    public Double getExpectedCash() {
        return expectedCash;
    }

    public void setExpectedCash(Double expectedCash) {
        this.expectedCash = expectedCash;
    }

    public Double getCountedCash() {
        return countedCash;
    }

    public void setCountedCash(Double countedCash) {
        this.countedCash = countedCash;
    }

    public Double getDifference() {
        return difference;
    }

    public void setDifference(Double difference) {
        this.difference = difference;
    }

    public Double getBankWithdrawal() {
        return bankWithdrawal;
    }

    public void setBankWithdrawal(Double bankWithdrawal) {
        this.bankWithdrawal = bankWithdrawal;
    }

    public Double getRemainingCash() {
        return remainingCash;
    }

    public void setRemainingCash(Double remainingCash) {
        this.remainingCash = remainingCash;
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

    public String getClosingComment() {
        return closingComment;
    }

    public void setClosingComment(String closingComment) {
        this.closingComment = closingComment;
    }

    public User getEmployee() {
        return employee;
    }

    public void setEmployee(User employee) {
        this.employee = employee;
    }

    public Integer getEmployeeId() {
        return employee != null ? employee.getId() : null;
    }

    public String getEmployeeName() {
        return employee != null ? employee.getNombre() : null;
    }
}
