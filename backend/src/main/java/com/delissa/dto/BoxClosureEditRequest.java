package com.delissa.dto;

public class BoxClosureEditRequest {
    private Double countedCash;
    private Double bankWithdrawal;
    private Double remainingCash;
    private String closingComment;
    private String editReason;
    private Integer employeeId;

    public Double getCountedCash() { return countedCash; }
    public void setCountedCash(Double countedCash) { this.countedCash = countedCash; }
    public Double getBankWithdrawal() { return bankWithdrawal; }
    public void setBankWithdrawal(Double bankWithdrawal) { this.bankWithdrawal = bankWithdrawal; }
    public Double getRemainingCash() { return remainingCash; }
    public void setRemainingCash(Double remainingCash) { this.remainingCash = remainingCash; }
    public String getClosingComment() { return closingComment; }
    public void setClosingComment(String closingComment) { this.closingComment = closingComment; }
    public String getEditReason() { return editReason; }
    public void setEditReason(String editReason) { this.editReason = editReason; }
    public Integer getEmployeeId() { return employeeId; }
    public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }
}
