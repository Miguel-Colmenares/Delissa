package com.delissa.service;

import com.delissa.dto.BoxClosureRequest;
import com.delissa.dto.BoxClosureEditRequest;
import com.delissa.dto.BoxOpeningRequest;
import com.delissa.dto.BoxSummaryResponse;
import com.delissa.model.*;
import com.delissa.repository.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BoxClosureService {

    private final BoxClosureRepository boxClosureRepository;
    private final BoxClosureEditRepository boxClosureEditRepository;
    private final BoxOpeningRepository boxOpeningRepository;
    private final SaleRepository saleRepository;
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public BoxClosureService(BoxClosureRepository boxClosureRepository,
                             BoxClosureEditRepository boxClosureEditRepository,
                             BoxOpeningRepository boxOpeningRepository,
                             SaleRepository saleRepository,
                             ExpenseRepository expenseRepository,
                             UserRepository userRepository) {
        this.boxClosureRepository = boxClosureRepository;
        this.boxClosureEditRepository = boxClosureEditRepository;
        this.boxOpeningRepository = boxOpeningRepository;
        this.saleRepository = saleRepository;
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    public BoxSummaryResponse getSummary(LocalDate businessDate) {
        LocalDate date = businessDate != null ? businessDate : LocalDate.now();
        List<Sale> sales = getPaidSales(date);
        List<Expense> expenses = getExpenses(date);

        BoxSummaryResponse response = new BoxSummaryResponse();
        response.setBusinessDate(date);
        response.setSuggestedStartingCash(
                boxOpeningRepository.findTopByBusinessDateOrderByOpenedAtDesc(date)
                        .map(BoxOpening::getStartingCash)
                        .orElseGet(() -> boxClosureRepository.findTopByOrderByClosedAtDesc()
                                .map(BoxClosure::getRemainingCash)
                                .orElse(0.0))
        );
        response.setTotalSales(sumSales(sales, null));
        response.setCashSales(sumSales(sales, PaymentMethod.CASH));
        response.setCardSales(sumSales(sales, PaymentMethod.CARD));
        response.setNequiSales(sumSales(sales, PaymentMethod.NEQUI));
        response.setTotalExpenses(sumExpenses(expenses));
        response.setSalesCount(sales.size());
        response.setProductsSold(countProducts(sales));
        response.setProducts(productSummary(sales));
        response.setExpenses(expenses);
        response.setCurrentOpening(boxOpeningRepository.findTopByBusinessDateOrderByOpenedAtDesc(date).orElse(null));
        response.setOpenings(boxOpeningRepository.findByBusinessDateOrderByOpenedAtDesc(date));
        response.setClosures(boxClosureRepository.findByBusinessDateOrderByClosedAtDesc(date));

        return response;
    }

    @Transactional
    public BoxOpening createOpening(BoxOpeningRequest request) {
        LocalDate date = request.getBusinessDate() != null ? request.getBusinessDate() : LocalDate.now();
        if (boxClosureRepository.existsByBusinessDate(date)) {
            throw new RuntimeException("La caja ya fue cerrada para esta fecha");
        }

        User employee = userRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        BoxOpening opening = new BoxOpening();
        opening.setBusinessDate(date);
        opening.setOpenedAt(LocalDateTime.now());
        opening.setStartingCash(money(request.getStartingCash()));
        opening.setOpeningComment(request.getOpeningComment());
        opening.setEmployee(employee);

        return boxOpeningRepository.save(opening);
    }

    @Transactional
    public BoxClosure createClosure(BoxClosureRequest request) {
        LocalDate date = request.getBusinessDate() != null ? request.getBusinessDate() : LocalDate.now();
        if (boxClosureRepository.existsByBusinessDate(date)) {
            throw new RuntimeException("La caja ya fue cerrada para esta fecha");
        }

        User employee = userRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        List<Sale> sales = getPaidSales(date);
        List<Expense> expenses = getExpenses(date);

        double startingCash = money(request.getStartingCash());
        double totalExpenses = sumExpenses(expenses);
        double cashSales = sumSales(sales, PaymentMethod.CASH);
        double expectedCash = startingCash + cashSales - totalExpenses;
        double countedCash = money(request.getCountedCash());
        double bankWithdrawal = money(request.getBankWithdrawal());
        double remainingCash = request.getRemainingCash() != null
                ? money(request.getRemainingCash())
                : Math.max(0, countedCash - bankWithdrawal);

        BoxClosure closure = new BoxClosure();
        closure.setBusinessDate(date);
        closure.setClosedAt(LocalDateTime.now());
        closure.setStartingCash(startingCash);
        closure.setTotalSales(sumSales(sales, null));
        closure.setCashSales(cashSales);
        closure.setCardSales(sumSales(sales, PaymentMethod.CARD));
        closure.setNequiSales(sumSales(sales, PaymentMethod.NEQUI));
        closure.setTotalExpenses(totalExpenses);
        closure.setExpectedCash(expectedCash);
        closure.setCountedCash(countedCash);
        closure.setDifference(countedCash - expectedCash);
        closure.setBankWithdrawal(bankWithdrawal);
        closure.setRemainingCash(remainingCash);
        closure.setSalesCount(sales.size());
        closure.setProductsSold(countProducts(sales));
        closure.setClosingComment(request.getClosingComment());
        closure.setEmployee(employee);

        return boxClosureRepository.save(closure);
    }

    public List<BoxClosure> getClosures() {
        return boxClosureRepository.findAll();
    }

    @Transactional
    public BoxClosure editClosure(Long closureId, BoxClosureEditRequest request) {
        if (request.getEditReason() == null || request.getEditReason().isBlank()) {
            throw new RuntimeException("El motivo de edicion es obligatorio");
        }

        BoxClosure closure = boxClosureRepository.findById(closureId)
                .orElseThrow(() -> new RuntimeException("Cierre no encontrado"));
        User employee = userRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        double countedCash = money(request.getCountedCash());
        double bankWithdrawal = money(request.getBankWithdrawal());
        double remainingCash = request.getRemainingCash() != null
                ? money(request.getRemainingCash())
                : Math.max(0, countedCash - bankWithdrawal);
        double difference = countedCash - money(closure.getExpectedCash());

        BoxClosureEdit edit = new BoxClosureEdit();
        edit.setClosure(closure);
        edit.setEmployee(employee);
        edit.setEditedAt(LocalDateTime.now());
        edit.setPreviousCountedCash(closure.getCountedCash());
        edit.setNewCountedCash(countedCash);
        edit.setPreviousBankWithdrawal(closure.getBankWithdrawal());
        edit.setNewBankWithdrawal(bankWithdrawal);
        edit.setPreviousRemainingCash(closure.getRemainingCash());
        edit.setNewRemainingCash(remainingCash);
        edit.setPreviousDifference(closure.getDifference());
        edit.setNewDifference(difference);
        edit.setPreviousComment(closure.getClosingComment());
        edit.setNewComment(request.getClosingComment());
        edit.setEditReason(request.getEditReason());
        boxClosureEditRepository.save(edit);

        closure.setCountedCash(countedCash);
        closure.setBankWithdrawal(bankWithdrawal);
        closure.setRemainingCash(remainingCash);
        closure.setDifference(difference);
        closure.setClosingComment(request.getClosingComment());

        return boxClosureRepository.save(closure);
    }

   public List<BoxClosureEdit> getClosureEdits(Long closureId) {
    return boxClosureEditRepository.findByClosure_IdOrderByEditedAtDesc(closureId);
}

    private List<Sale> getPaidSales(LocalDate date) {
        return saleRepository.findByDateBetween(startOf(date), endOf(date)).stream()
                .filter(sale -> sale.getStatus() == null || sale.getStatus() == SaleStatus.PAID)
                .toList();
    }

    private List<Expense> getExpenses(LocalDate date) {
        return expenseRepository.findByDateBetween(startOf(date), endOf(date));
    }

    private LocalDateTime startOf(LocalDate date) {
        return date.atStartOfDay();
    }

    private LocalDateTime endOf(LocalDate date) {
        return date.atTime(LocalTime.MAX);
    }

    private double sumSales(List<Sale> sales, PaymentMethod method) {
        return sales.stream()
                .filter(sale -> method == null || sale.getPaymentMethod() == method)
                .mapToDouble(sale -> money(sale.getTotal()))
                .sum();
    }

    private double sumExpenses(List<Expense> expenses) {
        return expenses.stream()
                .mapToDouble(expense -> money(expense.getAmount()))
                .sum();
    }

    private int countProducts(List<Sale> sales) {
        return sales.stream()
                .flatMap(sale -> sale.getDetails() != null ? sale.getDetails().stream() : Stream.<SaleDetail>empty())
                .mapToInt(detail -> detail.getQuantity() != null ? detail.getQuantity() : 0)
                .sum();
    }

    private Map<String, Integer> productSummary(List<Sale> sales) {
        Map<String, Integer> products = new LinkedHashMap<>();

        sales.forEach(sale -> {
            if (sale.getDetails() == null) return;

            sale.getDetails().forEach(detail -> {
                String name = detail.getProductName() != null ? detail.getProductName() : "Producto";
                int quantity = detail.getQuantity() != null ? detail.getQuantity() : 0;
                products.merge(name, quantity, Integer::sum);
            });
        });

        return products;
    }

    private double money(Double value) {
        return value != null ? value : 0.0;
    }
}
