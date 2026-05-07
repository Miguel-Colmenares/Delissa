package com.delissa.service;

import com.delissa.model.ProductionInventoryMovement;
import com.delissa.model.ProductionItem;
import com.delissa.model.ProductionItemProduct;
import com.delissa.model.User;
import com.delissa.repository.ProductionInventoryMovementRepository;
import com.delissa.repository.ProductionItemProductRepository;
import com.delissa.repository.ProductionItemRepository;
import com.delissa.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductionStockService {

    private final ProductionItemProductRepository linkRepository;
    private final ProductionItemRepository productionItemRepository;
    private final ProductionInventoryMovementRepository movementRepository;
    private final UserRepository userRepository;

    public ProductionStockService(
            ProductionItemProductRepository linkRepository,
            ProductionItemRepository productionItemRepository,
            ProductionInventoryMovementRepository movementRepository,
            UserRepository userRepository
    ) {
        this.linkRepository = linkRepository;
        this.productionItemRepository = productionItemRepository;
        this.movementRepository = movementRepository;
        this.userRepository = userRepository;
    }

    public void discountForProduct(Long productId, int productQuantity, String reason, Long userId) {
        moveForProduct(productId, productQuantity, -1, "SALE", reason, userId);
    }

    public void restoreForProduct(Long productId, int productQuantity, String reason, Long userId) {
        moveForProduct(productId, productQuantity, 1, "RESTORE", reason, userId);
    }

    private void moveForProduct(Long productId, int productQuantity, int direction, String type, String reason, Long userId) {
        if (productId == null || productQuantity <= 0) return;

        List<ProductionItemProduct> links = linkRepository.findByProductId(productId);
        for (ProductionItemProduct link : links) {
            ProductionItem item = link.getProductionItem();
            if (item == null) continue;

            double perProduct = link.getQuantityPerProduct() != null && link.getQuantityPerProduct() > 0
                    ? link.getQuantityPerProduct()
                    : 1;
            double quantity = perProduct * productQuantity * direction;

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
        }
    }
}
