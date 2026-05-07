package com.delissa.dto;

import java.time.LocalDate;

public class BoxOpeningRequest {
    private LocalDate businessDate;
    private Double startingCash;
    private String openingComment;
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

    public String getOpeningComment() {
        return openingComment;
    }

    public void setOpeningComment(String openingComment) {
        this.openingComment = openingComment;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }
}
