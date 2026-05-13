package com.delissa.controller;

import com.delissa.model.Product;
import com.delissa.model.Inventory;
import com.delissa.repository.ProductRepository;
import com.delissa.repository.InventoryRepository;
import com.delissa.model.User;
import com.delissa.repository.UserRepository;
import com.delissa.service.ProductionStockService;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;



@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductionStockService productionStockService;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private UserRepository userRepository;

    public ProductController(ProductRepository productRepository, ProductionStockService productionStockService) {
        this.productRepository = productRepository;
        this.productionStockService = productionStockService;
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
            @RequestParam Integer quantity,
            @RequestParam(required = false) Long userId
    ) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        int newStock = product.getStock() + quantity;
        product.setStock(newStock);

        productRepository.save(product);
        if (quantity < 0) {
            productionStockService.discountForProduct(product.getId(), Math.abs(quantity), "Salida manual de producto", userId);
        }

        // 🔥 GUARDAR HISTORIAL
        Inventory inv = new Inventory();
        inv.setProduct(product);
        inv.setQuantity(quantity);
        inv.setLastUpdate(LocalDateTime.now());
        User user = userId != null ? userRepository.findById(userId.intValue()).orElse(null) : null;
        inv.setUser(user);

        inventoryRepository.save(inv);

        return product;
    }

    // 🔥 BULK UPDATE + HISTORIAL
    @PutMapping("/bulk-update")
    public List<Product> updateAll(
            @RequestBody List<Product> products,
            @RequestParam(required = false) Long userId
    ) {

        for (Product p : products) {

            Product existing = productRepository.findById(p.getId()).orElseThrow();

            int diff = p.getStock() - existing.getStock();

            existing.setStock(p.getStock());
            productRepository.save(existing);
            if (diff < 0) {
                productionStockService.discountForProduct(existing.getId(), Math.abs(diff), "Salida manual de producto", userId);
            }

            if (diff != 0) {
                Inventory inv = new Inventory();
                inv.setProduct(existing);
                inv.setQuantity(diff);
                inv.setLastUpdate(LocalDateTime.now());
                User user = userId != null ? userRepository.findById(userId.intValue()).orElse(null) : null;
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

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        existing.setName(product.getName());
        existing.setPrice(product.getPrice());
        existing.setImg(product.getImg());
        existing.setCategory(product.getCategory());
        existing.setSubCategory(product.getSubCategory());
        existing.setStock(product.getStock());

        return productRepository.save(existing);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        inventoryRepository.deleteAll(inventoryRepository.findByProductId(id));
        productRepository.deleteById(id);
    }

}
