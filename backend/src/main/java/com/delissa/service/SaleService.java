package com.delissa.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.delissa.model.*;
import com.delissa.repository.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final ClientInvoiceRepository clientInvoiceRepository;

    public SaleService(SaleRepository saleRepository,
                       ProductRepository productRepository,
                       ClientInvoiceRepository clientInvoiceRepository) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.clientInvoiceRepository = clientInvoiceRepository;
    }

    @Transactional
    public Sale createSale(Sale sale) {

        // 🔥 fecha automática
        sale.setDate(LocalDateTime.now());

        // 🔥 transaction ID único
        sale.setTransactionId(UUID.randomUUID().toString());

        double subtotal = 0;

        // 🚨 validación básica
        if (sale.getDetails() == null || sale.getDetails().isEmpty()) {
            throw new RuntimeException("La venta no tiene productos");
        }

        // 🔥 recorrer productos
        for (SaleDetail detail : sale.getDetails()) {

            Product product = productRepository.findById(detail.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            // 🚨 validar stock
            if (product.getStock() < detail.getQuantity()) {
                throw new RuntimeException("Stock insuficiente para: " + product.getName());
            }

            // 🔥 snapshot (guardar info aunque el producto cambie después)
            detail.setProductName(product.getName());
            detail.setUnitPrice(product.getPrice());
            detail.setSubtotal(product.getPrice() * detail.getQuantity());

            // 🔥 descontar stock
            product.setStock(product.getStock() - detail.getQuantity());
            productRepository.save(product);

            // 🔥 relación
            detail.setSale(sale);

            subtotal += detail.getSubtotal();
        }

        // 🔥 totales
        sale.setSubtotal(subtotal);

        // 👉 OPCIÓN SIMPLE (recomendada)
        sale.setTax(0.0);
        sale.setTotal(subtotal);

        // 👉 si luego quieres IVA:
        // sale.setTax(subtotal * 0.19);
        // sale.setTotal(subtotal + sale.getTax());

        sale.setStatus(SaleStatus.PAID);

        // 🔥 factura empresarial
        if ("EMPRESARIAL".equalsIgnoreCase(sale.getInvoiceType())
                && sale.getClientInvoice() != null) {

            ClientInvoice invoice = sale.getClientInvoice();

            invoice.setSale(sale);

            // 🔥 número de factura
            invoice.setInvoiceNumber("INV-" + System.currentTimeMillis());

            clientInvoiceRepository.save(invoice);
        }

        // 🔥 guardar venta (cascade guarda detalles)
        return saleRepository.save(sale);
    }
}