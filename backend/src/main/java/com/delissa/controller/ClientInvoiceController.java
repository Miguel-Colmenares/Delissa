package com.delissa.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

import com.delissa.model.ClientInvoice;
import com.delissa.model.Sale;
import com.delissa.repository.ClientInvoiceRepository;
import com.delissa.service.SaleService;

@RestController
@RequestMapping("/invoices")
@CrossOrigin("*")
public class ClientInvoiceController {

    private final ClientInvoiceRepository clientInvoiceRepository;
    private final SaleService saleService;

    public ClientInvoiceController(ClientInvoiceRepository clientInvoiceRepository,
                                   SaleService saleService) {
        this.clientInvoiceRepository = clientInvoiceRepository;
        this.saleService = saleService;
    }

    @GetMapping
    public List<ClientInvoice> getAllInvoices() {
        return clientInvoiceRepository.findAll().stream()
                .sorted(Comparator.comparing(
                        invoice -> invoice.getSale() != null ? invoice.getSale().getDate() : null,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .toList();
    }

    @PutMapping("/{saleId}")
    public Sale updateInvoice(@PathVariable Integer saleId, @RequestBody Sale sale) {
        return saleService.updateInvoice(saleId, sale);
    }

    @PostMapping("/{saleId}/cancel")
    public Sale cancelInvoice(@PathVariable Integer saleId, @RequestBody(required = false) Sale payload) {
        return saleService.cancelInvoice(saleId, payload);
    }
}
