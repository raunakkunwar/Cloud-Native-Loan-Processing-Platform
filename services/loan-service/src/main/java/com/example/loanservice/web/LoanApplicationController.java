package com.example.loanservice.web;

import com.example.loanservice.model.LoanApplication;
import com.example.loanservice.service.LoanService;
import com.example.loanservice.web.dto.LoanRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/loans")
public class LoanApplicationController {

    private final LoanService loanService;

    public LoanApplicationController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    public ResponseEntity<LoanApplication> create(@Valid @RequestBody LoanRequest request) {
        return ResponseEntity.ok(loanService.create(request));
    }

    @GetMapping
    public Iterable<LoanApplication> list() {
        return loanService.list();
    }

    @GetMapping("/{id}")
    public LoanApplication get(@PathVariable UUID id) {
        return loanService.get(id);
    }
}
