package com.delissa.controller;

import org.springframework.web.bind.annotation.*;
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

    @PostMapping
    public Sale createSale(@RequestBody Sale sale) {
        return saleService.createSale(sale);
    }
}