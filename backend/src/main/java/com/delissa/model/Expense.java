package com.delissa.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 🔥 CAMBIADO

    private Double amount;

    private LocalDateTime date;

    public Long getId() { return id; } // 🔥 CAMBIADO

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
}