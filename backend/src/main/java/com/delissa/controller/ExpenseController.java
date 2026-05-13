package com.delissa.controller;

import com.delissa.dto.ExpenseRequest;
import com.delissa.model.Expense;
import com.delissa.model.User;
import com.delissa.repository.ExpenseRepository;
import com.delissa.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseController(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public Expense create(@RequestBody ExpenseRequest request) {
        Expense expense = new Expense();
        expense.setAmount(request.getAmount());
        expense.setDate(request.getDate() != null ? request.getDate() : LocalDateTime.now());
        expense.setComment(request.getComment());

        if (request.getEmployeeId() != null) {
            User employee = userRepository.findById(request.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
            expense.setUsuario(employee);
        }

        return expenseRepository.save(expense);
    }

    @GetMapping("/today")
    public List<Expense> getToday() {
        LocalDate today = LocalDate.now();
        return expenseRepository.findByDateBetween(today.atStartOfDay(), today.atTime(LocalTime.MAX));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        expenseRepository.deleteById(id);
    }
}
