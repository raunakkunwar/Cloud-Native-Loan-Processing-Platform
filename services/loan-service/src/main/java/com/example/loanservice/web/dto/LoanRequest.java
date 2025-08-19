package com.example.loanservice.web.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class LoanRequest {
    @NotBlank
    public String applicantName;
    @NotBlank
    public String ssn;
    @Positive
    public BigDecimal income;
    @Positive
    public BigDecimal loanAmount;
    @Min(1)
    public Integer termMonths;
    @Min(300) @Max(850)
    public Integer creditScore;
}
