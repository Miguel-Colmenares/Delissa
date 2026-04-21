package com.delissa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.delissa.model.ClientInvoice;

public interface ClientInvoiceRepository extends JpaRepository<ClientInvoice, Integer> {
}