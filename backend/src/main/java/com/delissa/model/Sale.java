package com.delissa.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "sales")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime date;
    private Double total;

    private String paymentMethod; // CASH, CARD, NEQUI
    private String status; // PAID, PENDING

    // 🔥 IMPORTANTE → sigue siendo "usuario"
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User usuario;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<SaleDetail> details;

    @OneToOne(mappedBy = "sale", cascade = CascadeType.ALL)
    private ClientInvoice clientInvoice;

    // getters and setters

}