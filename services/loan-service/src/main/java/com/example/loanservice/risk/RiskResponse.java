package com.example.loanservice.risk;

public class RiskResponse {
    private double riskScore;
    private boolean approved;

    public RiskResponse(){}

    public double getRiskScore() { return riskScore; }
    public void setRiskScore(double riskScore) { this.riskScore = riskScore; }
    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
}
