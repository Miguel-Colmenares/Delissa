package com.delissa.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "sales")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime date;

    // 🔥 valores monetarios
    private Double subtotal;
    private Double tax;
    private Double total;

    private String invoiceType; // NORMAL | EMPRESARIAL

    @Column(unique = true)
    private String transactionId; // id único de la venta

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // CASH, CARD, NEQUI

    @Enumerated(EnumType.STRING)
    private SaleStatus status; // PAID, PENDING, CANCELLED

    // 🔥 usuario que realiza la venta
   @ManyToOne
@JoinColumn(name = "usuario_id")
@JsonIgnore
private User usuario;

    // 🔥 detalles de la venta
    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<SaleDetail> details;

    // 🔥 factura empresarial (opcional)
   @OneToOne(mappedBy = "sale", cascade = CascadeType.ALL)
@JsonIgnore
private ClientInvoice clientInvoice;

    // -------------------------
    // CONSTRUCTORES
    // -------------------------

    public Sale() {
        this.date = LocalDateTime.now();
    }

    // -------------------------
    // GETTERS Y SETTERS
    // -------------------------

    public Integer getId() {
        return id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public SaleStatus getStatus() {
        return status;
    }

    public void setStatus(SaleStatus status) {
        this.status = status;
    }

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }

    public List<SaleDetail> getDetails() {
        return details;
    }

    public void setDetails(List<SaleDetail> details) {
        this.details = details;
    }

    public ClientInvoice getClientInvoice() {
        return clientInvoice;
    }

    public void setClientInvoice(ClientInvoice clientInvoice) {
        this.clientInvoice = clientInvoice;
    }
}