package com.delissa.controller;

import com.delissa.model.Product;
import com.delissa.model.Inventory;
import com.delissa.repository.ProductRepository;
import com.delissa.repository.InventoryRepository;
import com.delissa.model.User;
import com.delissa.repository.UserRepository;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;



@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // 🔥 GET ALL PRODUCTS
    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 🔥 UPDATE STOCK + HISTORIAL
    @PutMapping("/{id}/stock")
    public Product updateStock(
            @PathVariable Long id,
            @RequestParam Integer quantity
    ) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        int newStock = product.getStock() + quantity;
        product.setStock(newStock);

        productRepository.save(product);

        // 🔥 GUARDAR HISTORIAL
        Inventory inv = new Inventory();
        inv.setProduct(product);
        inv.setQuantity(quantity);
        inv.setLastUpdate(LocalDateTime.now());

        inventoryRepository.save(inv);

        return product;
    }

    // 🔥 BULK UPDATE + HISTORIAL
    @PutMapping("/bulk-update")
    public List<Product> updateAll(@RequestBody List<Product> products) {

        for (Product p : products) {

            Product existing = productRepository.findById(p.getId()).orElseThrow();

            int diff = p.getStock() - existing.getStock();

            existing.setStock(p.getStock());
            productRepository.save(existing);

            if (diff != 0) {
                Inventory inv = new Inventory();
                inv.setProduct(existing);
                inv.setQuantity(diff);
                inv.setLastUpdate(LocalDateTime.now());
                User user = userRepository.findById(1).orElse(null);
                inv.setUser(user);

                inventoryRepository.save(inv);
            }
        }

        return products;
    }

    // 🔥 HISTORIAL POR FECHA
    @GetMapping("/inventory")
    public List<Inventory> getByDate(
            @RequestParam String start,
            @RequestParam String end
    ) {
        return inventoryRepository.findByLastUpdateBetween(
                LocalDateTime.parse(start.replace("Z","")),
                LocalDateTime.parse(end.replace("Z",""))
        );
    }

    @Autowired
    private UserRepository userRepository;

    @PostMapping
public Product createProduct(@RequestBody Product product) {
    return productRepository.save(product);
}



}