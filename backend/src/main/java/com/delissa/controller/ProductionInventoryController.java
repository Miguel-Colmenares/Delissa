package com.delissa.controller;

import com.delissa.model.ProductionInventoryMovement;
import com.delissa.model.ProductionItem;
import com.delissa.model.User;
import com.delissa.repository.ProductionInventoryMovementRepository;
import com.delissa.repository.ProductionItemRepository;
import com.delissa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/production-items")
@CrossOrigin(origins = "*")
public class ProductionInventoryController {

    private final ProductionItemRepository productionItemRepository;
    private final ProductionInventoryMovementRepository movementRepository;

    @Autowired
    private UserRepository userRepository;

    public ProductionInventoryController(
            ProductionItemRepository productionItemRepository,
            ProductionInventoryMovementRepository movementRepository
    ) {
        this.productionItemRepository = productionItemRepository;
        this.movementRepository = movementRepository;
    }

    @GetMapping
    public List<ProductionItem> getAll() {
        return productionItemRepository.findAll();
    }

    @PostMapping
    public ProductionItem create(@RequestBody ProductionItem item) {
        item.setLastUpdate(LocalDateTime.now());
        return productionItemRepository.save(item);
    }

    @PutMapping("/{id}")
    public ProductionItem update(@PathVariable Long id, @RequestBody ProductionItem item, @RequestParam(required = false) Long userId) {
        ProductionItem existing = productionItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingrediente no encontrado"));

        existing.setName(item.getName());
        existing.setCategory(item.getCategory());
        existing.setUnit(item.getUnit());
        existing.setStock(item.getStock());
        existing.setMinStock(item.getMinStock());
        existing.setCost(item.getCost());
        existing.setSupplier(item.getSupplier());
        existing.setSupplierContact(item.getSupplierContact());
        existing.setPackageName(item.getPackageName());
        existing.setUnitsPerPackage(item.getUnitsPerPackage());
        existing.setNotes(item.getNotes());
        existing.setProductLinks(item.getProductLinks());
        existing.setLastUpdate(LocalDateTime.now());

        ProductionItem saved = productionItemRepository.save(existing);

        ProductionInventoryMovement movement = new ProductionInventoryMovement();
        movement.setProductionItem(saved);
        movement.setQuantity(0.0);
        movement.setType("EDIT");
        movement.setReason("Edicion de ingrediente");
        movement.setLastUpdate(LocalDateTime.now());
        if (item.getUserId() != null) {
            userRepository.findById(item.getUserId().intValue()).ifPresent(movement::setUser);
        }
        movementRepository.save(movement);

        return saved;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        movementRepository.deleteAll(movementRepository.findByProductionItemId(id));
        productionItemRepository.deleteById(id);
    }

    @PutMapping("/{id}/adjust")
    public ProductionItem adjustStock(
            @PathVariable Long id,
            @RequestParam Double quantity,
            @RequestParam(required = false, defaultValue = "ADJUSTMENT") String type,
            @RequestParam(required = false, defaultValue = "Ajuste manual") String reason,
            @RequestParam(required = false) Long userId
    ) {
        ProductionItem item = productionItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingrediente no encontrado"));

        item.setStock((item.getStock() == null ? 0 : item.getStock()) + quantity);
        item.setLastUpdate(LocalDateTime.now());
        productionItemRepository.save(item);

        ProductionInventoryMovement movement = new ProductionInventoryMovement();
        movement.setProductionItem(item);
        movement.setQuantity(quantity);
        movement.setType(type);
        movement.setReason(reason);
        movement.setLastUpdate(LocalDateTime.now());
        User user = userId != null ? userRepository.findById(userId.intValue()).orElse(null) : null;
        movement.setUser(user);
        movementRepository.save(movement);

        return item;
    }

    @GetMapping("/inventory")
    public List<ProductionInventoryMovement> getByDate(
            @RequestParam String start,
            @RequestParam String end
    ) {
        return movementRepository.findByLastUpdateBetween(
                LocalDateTime.parse(start.replace("Z", "")),
                LocalDateTime.parse(end.replace("Z", ""))
        );
    }
}
