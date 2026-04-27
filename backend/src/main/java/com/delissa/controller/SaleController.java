package com.delissa.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.delissa.model.Sale;
import com.delissa.service.SaleService;

@RestController
@RequestMapping("/sales")
@CrossOrigin("*")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    // 🔥 CREAR VENTA
    @PostMapping
    public Sale createSale(@RequestBody Sale sale) {
        return saleService.createSale(sale);
    }

    // 🔥 OBTENER TODAS LAS VENTAS
    @GetMapping
    public List<Sale> getAllSales() {
        return saleService.getAllSales();
    }
}