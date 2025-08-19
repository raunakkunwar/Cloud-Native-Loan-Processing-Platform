from fastapi import FastAPI
from pydantic import BaseModel, Field
import numpy as np

app = FastAPI(title="Risk Service", version="1.0.0")

class LoanInput(BaseModel):
    applicantName: str
    ssn: str
    income: float = Field(..., ge=0)
    loanAmount: float = Field(..., ge=0)
    termMonths: int = Field(..., ge=1)
    creditScore: int = Field(..., ge=300, le=850)

class RiskOutput(BaseModel):
    riskScore: float
    approved: bool

def heuristic_score(income: float, loan_amount: float, term_months: int, credit_score: int) -> float:
    # Simple interpretable heuristic: lower is better risk
    dti = loan_amount / max(income, 1.0)
    cs_factor = (850 - credit_score) / 850.0
    term_factor = term_months / 84.0  # normalize vs 7 years
    score = 0.55 * dti + 0.35 * cs_factor + 0.10 * term_factor
    return float(np.clip(score, 0.0, 1.0))

@app.get("/health")
def health():
    return {"status": "ok"}

@app.post("/score", response_model=RiskOutput)
def score(payload: LoanInput):
    score = heuristic_score(payload.income, payload.loanAmount, payload.termMonths, payload.creditScore)
    approved = score < 0.45  # threshold (tune to taste)
    return RiskOutput(riskScore=round(score, 4), approved=approved)
