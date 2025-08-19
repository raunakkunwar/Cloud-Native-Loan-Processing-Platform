package com.example.loanservice.service;

import com.example.loanservice.model.LoanApplication;
import com.example.loanservice.repo.LoanApplicationRepository;
import com.example.loanservice.risk.RiskResponse;
import com.example.loanservice.web.dto.LoanRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class LoanService {
    private final LoanApplicationRepository repo;
    private final WebClient webClient;

    public LoanService(LoanApplicationRepository repo,
                       WebClient.Builder webClientBuilder,
                       @Value("${app.risk.url}") String riskBaseUrl) {
        this.repo = repo;
        this.webClient = webClientBuilder.baseUrl(riskBaseUrl).build();
    }

    public LoanApplication create(LoanRequest req) {
        if (repo.existsBySsn(req.ssn)) {
            throw new IllegalArgumentException("Duplicate SSN");
        }

        // call risk-service
        RiskResponse risk = webClient.post()
                .uri("/score")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new RiskRequestAdapter(req))
                .retrieve()
                .bodyToMono(RiskResponse.class)
                .onErrorResume(e -> Mono.error(new RuntimeException("Risk service error: " + e.getMessage())))
                .block();

        LoanApplication app = new LoanApplication();
        app.setApplicantName(req.applicantName);
        app.setSsn(req.ssn);
        app.setIncome(req.income);
        app.setLoanAmount(req.loanAmount);
        app.setTermMonths(req.termMonths);
        app.setCreditScore(req.creditScore);
        if (risk != null) {
            app.setRiskScore(risk.getRiskScore());
            app.setStatus(risk.isApproved() ? "APPROVED" : "REJECTED");
        } else {
            app.setStatus("PENDING");
        }
        return repo.save(app);
    }

    public Iterable<LoanApplication> list() {
        return repo.findAll();
    }

    public LoanApplication get(java.util.UUID id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Not found"));
    }

    // Adapter class so we can control payload to risk-service
    static class RiskRequestAdapter {
        public String applicantName;
        public String ssn;
        public double income;
        public double loanAmount;
        public int termMonths;
        public int creditScore;

        public RiskRequestAdapter(LoanRequest r) {
            this.applicantName = r.applicantName;
            this.ssn = r.ssn;
            this.income = r.income.doubleValue();
            this.loanAmount = r.loanAmount.doubleValue();
            this.termMonths = r.termMonths;
            this.creditScore = r.creditScore;
        }
    }
}
