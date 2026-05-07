package com.delissa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "box_closure_edits")
public class BoxClosureEdit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime editedAt;

    private Double previousCountedCash;
    private Double newCountedCash;
    private Double previousBankWithdrawal;
    private Double newBankWithdrawal;
    private Double previousRemainingCash;
    private Double newRemainingCash;
    private Double previousDifference;
    private Double newDifference;

    @Column(length = 1000)
    private String previousComment;

    @Column(length = 1000)
    private String newComment;

    @Column(length = 1000)
    private String editReason;

    @ManyToOne
    @JoinColumn(name = "closure_id")
    @JsonIgnore
    private BoxClosure closure;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private User employee;

    public Long getId() { return id; }
    public LocalDateTime getEditedAt() { return editedAt; }
    public void setEditedAt(LocalDateTime editedAt) { this.editedAt = editedAt; }
    public Double getPreviousCountedCash() { return previousCountedCash; }
    public void setPreviousCountedCash(Double previousCountedCash) { this.previousCountedCash = previousCountedCash; }
    public Double getNewCountedCash() { return newCountedCash; }
    public void setNewCountedCash(Double newCountedCash) { this.newCountedCash = newCountedCash; }
    public Double getPreviousBankWithdrawal() { return previousBankWithdrawal; }
    public void setPreviousBankWithdrawal(Double previousBankWithdrawal) { this.previousBankWithdrawal = previousBankWithdrawal; }
    public Double getNewBankWithdrawal() { return newBankWithdrawal; }
    public void setNewBankWithdrawal(Double newBankWithdrawal) { this.newBankWithdrawal = newBankWithdrawal; }
    public Double getPreviousRemainingCash() { return previousRemainingCash; }
    public void setPreviousRemainingCash(Double previousRemainingCash) { this.previousRemainingCash = previousRemainingCash; }
    public Double getNewRemainingCash() { return newRemainingCash; }
    public void setNewRemainingCash(Double newRemainingCash) { this.newRemainingCash = newRemainingCash; }
    public Double getPreviousDifference() { return previousDifference; }
    public void setPreviousDifference(Double previousDifference) { this.previousDifference = previousDifference; }
    public Double getNewDifference() { return newDifference; }
    public void setNewDifference(Double newDifference) { this.newDifference = newDifference; }
    public String getPreviousComment() { return previousComment; }
    public void setPreviousComment(String previousComment) { this.previousComment = previousComment; }
    public String getNewComment() { return newComment; }
    public void setNewComment(String newComment) { this.newComment = newComment; }
    public String getEditReason() { return editReason; }
    public void setEditReason(String editReason) { this.editReason = editReason; }
    public BoxClosure getClosure() { return closure; }
    public void setClosure(BoxClosure closure) { this.closure = closure; }
    public User getEmployee() { return employee; }
    public void setEmployee(User employee) { this.employee = employee; }

    public Long getClosureId() {
        return closure != null ? closure.getId() : null;
    }

    public Integer getEmployeeId() {
        return employee != null ? employee.getId() : null;
    }

    public String getEmployeeName() {
        return employee != null ? employee.getNombre() : null;
    }
}
