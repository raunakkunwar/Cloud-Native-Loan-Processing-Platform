package com.example.loanservice.repo;

import com.example.loanservice.model.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, UUID> {
    boolean existsBySsn(String ssn);
}
