package com.delissa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.delissa.model.ClientInvoice;

public interface ClientInvoiceRepository extends JpaRepository<ClientInvoice, Integer> {
    Optional<ClientInvoice> findBySaleId(Integer saleId);
}
