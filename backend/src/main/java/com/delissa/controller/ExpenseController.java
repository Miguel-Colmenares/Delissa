package com.delissa.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import com.delissa.model.Expense;
import com.delissa.repository.ExpenseRepository;

@RestController
@RequestMapping("/expenses")
@CrossOrigin("*")
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    // 🔥 CREAR
    @PostMapping
    public Expense create(@RequestBody Expense expense) {
        return expenseRepository.save(expense);
    }

    // 🔥 LISTAR HOY
    @GetMapping("/today")
    public List<Expense> getToday() {
        return expenseRepository.findAll(); // luego filtramos si quieres
    }

    // 🔥 DELETE (ESTO ES LO QUE TE FALTA)
  
    @DeleteMapping("/{id}")
public void delete(@PathVariable Long id) {
    expenseRepository.deleteById(id);
}
}