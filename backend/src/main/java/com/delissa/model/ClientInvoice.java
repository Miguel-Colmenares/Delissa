package com.delissa.model;

import jakarta.persistence.*;

@Entity
@Table(name = "client_invoices")
public class ClientInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String clientName;
    private String nit;
    private String email;
    private String phone;

    @OneToOne
    @JoinColumn(name = "sale_id")
    private Sale sale;

    // getters and setters
}