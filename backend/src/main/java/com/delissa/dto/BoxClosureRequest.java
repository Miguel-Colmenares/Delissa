package com.delissa.dto;

import java.time.LocalDate;

public class BoxClosureRequest {
    private LocalDate businessDate;
    private Double startingCash;
    private Double countedCash;
    private Double bankWithdrawal;
    private Double remainingCash;
    private String closingComment;
    private Integer employeeId;

    public LocalDate getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(LocalDate businessDate) {
        this.businessDate = businessDate;
    }

    public Double getStartingCash() {
        return startingCash;
    }

    public void setStartingCash(Double startingCash) {
        this.startingCash = startingCash;
    }

    public Double getCountedCash() {
        return countedCash;
    }

    public void setCountedCash(Double countedCash) {
        this.countedCash = countedCash;
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

    public String getClosingComment() {
        return closingComment;
    }

    public void setClosingComment(String closingComment) {
        this.closingComment = closingComment;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }
}
