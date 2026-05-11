package com.delissa.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.delissa.model.*;
import com.delissa.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final ClientInvoiceRepository clientInvoiceRepository;
    private final ProductionStockService productionStockService;
    private final UserRepository userRepository;
    private final ConfigSettingRepository configSettingRepository;
    private final InventoryRepository inventoryRepository;

    private double getIvaRate() {
        return configSettingRepository.findByConfigKey("iva_rate")
                .map(s -> Double.parseDouble(s.getConfigValue()))
                .orElse(0.19);
    }

    public SaleService(SaleRepository saleRepository,
                       ProductRepository productRepository,
                       ClientInvoiceRepository clientInvoiceRepository,
                       ProductionStockService productionStockService,
                       UserRepository userRepository,
                       ConfigSettingRepository configSettingRepository,
                       InventoryRepository inventoryRepository) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.clientInvoiceRepository = clientInvoiceRepository;
        this.productionStockService = productionStockService;
        this.userRepository = userRepository;
        this.configSettingRepository = configSettingRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public Sale createSale(Sale sale) {
        sale.setDate(LocalDateTime.now());
        sale.setTransactionId(UUID.randomUUID().toString());

        if (sale.getDetails() == null || sale.getDetails().isEmpty()) {
            throw new RuntimeException("La venta no tiene productos");
        }

        double subtotal = 0;

        for (SaleDetail detail : sale.getDetails()) {
            Product product = productRepository.findById(detail.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (product.getStock() < detail.getQuantity()) {
                throw new RuntimeException("Stock insuficiente para: " + product.getName());
            }

            detail.setProductName(product.getName());
            detail.setUnitPrice(product.getPrice());
            detail.setSubtotal(product.getPrice() * detail.getQuantity());
            detail.setSale(sale);

            product.setStock(product.getStock() - detail.getQuantity());
            productRepository.save(product);
            Long userId = sale.getUsuario() != null ? sale.getUsuario().getId().longValue() : null;
            productionStockService.discountForProduct(product.getId(), detail.getQuantity(), "Venta de " + product.getName(), userId);

            Inventory inv = new Inventory();
            inv.setProduct(product);
            inv.setQuantity(-detail.getQuantity());
            inv.setLastUpdate(LocalDateTime.now());
            User invUser = userId != null ? userRepository.findById(userId.intValue()).orElse(null) : null;
            inv.setUser(invUser);
            inventoryRepository.save(inv);

            subtotal += detail.getSubtotal();
        }

        sale.setSubtotal(subtotal);
        double ivaRate = getIvaRate();
        double tax = subtotal * ivaRate;
        sale.setTax(tax);
        sale.setTotal(subtotal + tax);
        sale.setStatus(SaleStatus.PAID);

        ClientInvoice invoiceToSave = null;

        if ("EMPRESARIAL".equalsIgnoreCase(sale.getInvoiceType())
                && sale.getClientInvoice() != null) {
            invoiceToSave = sale.getClientInvoice();
            invoiceToSave.setSale(sale);
            invoiceToSave.setInvoiceNumber("INV-" + System.currentTimeMillis());
        }

        Sale savedSale = saleRepository.save(sale);

        if (invoiceToSave != null) {
            invoiceToSave.setSale(savedSale);
            clientInvoiceRepository.save(invoiceToSave);
        }

        return savedSale;
    }

    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    public List<Sale> getTodaySales() {
        return saleRepository.findAll().stream()
                .filter(s -> s.getDate().toLocalDate().equals(LocalDate.now()))
                .toList();
    }

    @Transactional
    public Sale updateInvoice(Integer saleId, Sale payload) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        boolean wasCancelled = sale.getStatus() == SaleStatus.CANCELLED;
        SaleStatus requestedStatus = payload.getStatus() != null ? payload.getStatus() : sale.getStatus();
        applyEditAudit(sale, payload);

        updateInvoiceMetadata(sale, payload);

        if (requestedStatus == SaleStatus.CANCELLED) {
            if (!wasCancelled) {
                restoreStock(sale);
            }

            sale.setStatus(SaleStatus.CANCELLED);
            return saleRepository.save(sale);
        }

        if (payload.getDetails() != null && !payload.getDetails().isEmpty()) {
            boolean detailsChanged = haveDetailsChanged(sale.getDetails(), payload.getDetails());

            if (detailsChanged) {
                if (!wasCancelled) {
                    restoreStock(sale);
                }

                sale.getDetails().clear();
                double subtotal = applyDetailsAndDiscountStock(sale, payload.getDetails());
                sale.setSubtotal(subtotal);
                double ivaRate = getIvaRate();
                double tax = subtotal * ivaRate;
                sale.setTax(tax);
                sale.setTotal(subtotal + tax);
            }
        }

        sale.setStatus(requestedStatus != null ? requestedStatus : SaleStatus.PAID);

        return saleRepository.save(sale);
    }

    @Transactional
    public Sale cancelInvoice(Integer saleId, Sale payload) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        applyEditAudit(sale, payload != null ? payload : payloadForSystemCancel());

        if (sale.getStatus() != SaleStatus.CANCELLED) {
            restoreStock(sale);
            sale.setStatus(SaleStatus.CANCELLED);
        }

        return saleRepository.save(sale);
    }

    private void applyEditAudit(Sale sale, Sale payload) {
        sale.setLastEditedAt(LocalDateTime.now());

        if (payload.getLastEditReason() != null && !payload.getLastEditReason().isBlank()) {
            sale.setLastEditReason(payload.getLastEditReason());
        } else if (sale.getLastEditReason() == null || sale.getLastEditReason().isBlank()) {
            sale.setLastEditReason("Edicion de factura");
        }

        if (payload.getLastEditedBy() != null && payload.getLastEditedBy().getId() != null) {
            userRepository.findById(payload.getLastEditedBy().getId())
                    .ifPresent(sale::setLastEditedBy);
        }
    }

    private Sale payloadForSystemCancel() {
        Sale payload = new Sale();
        payload.setLastEditReason("Anulacion de factura");
        return payload;
    }

    private void updateInvoiceMetadata(Sale sale, Sale payload) {
        if (payload.getPaymentMethod() != null) {
            sale.setPaymentMethod(payload.getPaymentMethod());
        }

        if (payload.getInvoiceType() != null) {
            sale.setInvoiceType(payload.getInvoiceType());
        }

        if ("EMPRESARIAL".equalsIgnoreCase(sale.getInvoiceType())) {
            ClientInvoice incoming = payload.getClientInvoice();
            ClientInvoice invoice = clientInvoiceRepository.findBySaleId(sale.getId())
                    .orElseGet(ClientInvoice::new);

            if (incoming != null) {
                invoice.setClientName(incoming.getClientName());
                invoice.setNit(incoming.getNit());
                invoice.setEmail(incoming.getEmail());
                invoice.setAddress(incoming.getAddress());
            }

            if (invoice.getInvoiceNumber() == null || invoice.getInvoiceNumber().isBlank()) {
                invoice.setInvoiceNumber("INV-" + System.currentTimeMillis());
            }

            invoice.setSale(sale);
            sale.setClientInvoice(invoice);
            clientInvoiceRepository.save(invoice);
        } else {
            clientInvoiceRepository.findBySaleId(sale.getId()).ifPresent(clientInvoiceRepository::delete);
            sale.setClientInvoice(null);
        }
    }

    private double applyDetailsAndDiscountStock(Sale sale, List<SaleDetail> requestedDetails) {
        double subtotal = 0;

        if (sale.getDetails() == null) {
            sale.setDetails(new ArrayList<>());
        }

        for (SaleDetail requestedDetail : requestedDetails) {
            if (requestedDetail.getProduct() == null || requestedDetail.getProduct().getId() == null) {
                throw new RuntimeException("Un producto de la factura no tiene ID");
            }

            Product product = productRepository.findById(requestedDetail.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            int quantity = requestedDetail.getQuantity() != null ? requestedDetail.getQuantity() : 0;
            if (quantity <= 0) {
                throw new RuntimeException("La cantidad debe ser mayor a cero");
            }

            if (product.getStock() < quantity) {
                throw new RuntimeException("Stock insuficiente para: " + product.getName());
            }

            SaleDetail detail = new SaleDetail();
            detail.setProduct(product);
            detail.setQuantity(quantity);
            detail.setProductName(product.getName());
            detail.setUnitPrice(product.getPrice());
            detail.setSubtotal(product.getPrice() * quantity);
            detail.setSale(sale);

            product.setStock(product.getStock() - quantity);
            productRepository.save(product);
            Long userId = sale.getUsuario() != null ? sale.getUsuario().getId().longValue() : null;
            productionStockService.discountForProduct(product.getId(), quantity, "Venta de " + product.getName(), userId);
            sale.getDetails().add(detail);

            subtotal += detail.getSubtotal();
        }

        return subtotal;
    }

    private boolean haveDetailsChanged(List<SaleDetail> original, List<SaleDetail> requested) {
        if (original == null || requested == null) return true;
        if (original.size() != requested.size()) return true;

        Map<Long, Integer> originalMap = new HashMap<>();
        for (SaleDetail d : original) {
            if (d.getProduct() != null && d.getProduct().getId() != null) {
                originalMap.put(d.getProduct().getId(), d.getQuantity() != null ? d.getQuantity() : 0);
            }
        }

        for (SaleDetail d : requested) {
            if (d.getProduct() == null || d.getProduct().getId() == null) return true;
            Integer originalQty = originalMap.get(d.getProduct().getId());
            if (originalQty == null || !originalQty.equals(d.getQuantity())) return true;
        }

        return false;
    }

    private void restoreStock(Sale sale) {
        if (sale.getDetails() == null) return;
        Long userId = sale.getUsuario() != null ? sale.getUsuario().getId().longValue() : null;

        for (SaleDetail detail : sale.getDetails()) {
            if (detail.getProduct() == null || detail.getProduct().getId() == null) continue;

            Product product = productRepository.findById(detail.getProduct().getId())
                    .orElse(null);

            if (product != null) {
                int quantity = detail.getQuantity() != null ? detail.getQuantity() : 0;
                product.setStock(product.getStock() + quantity);
                productRepository.save(product);
                productionStockService.restoreForProduct(product.getId(), quantity, "Restauracion de venta cancelada", userId);

                Inventory inv = new Inventory();
                inv.setProduct(product);
                inv.setQuantity(quantity);
                inv.setLastUpdate(LocalDateTime.now());
                User invUser = userId != null ? userRepository.findById(userId.intValue()).orElse(null) : null;
                inv.setUser(invUser);
                inventoryRepository.save(inv);
            }
        }
    }
}
