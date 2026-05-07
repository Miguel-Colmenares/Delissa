package com.delissa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "box_openings")
public class BoxOpening {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate businessDate;
    private LocalDateTime openedAt;
    private Double startingCash;

    @Column(length = 1000)
    private String openingComment;

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

    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(LocalDateTime openedAt) {
        this.openedAt = openedAt;
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
