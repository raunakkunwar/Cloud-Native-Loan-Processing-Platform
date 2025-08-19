# Cloud-Native Loan Processing Platform

End-to-end, cloud-native microservices demo for loan processing:
- **loan-service**: Java 17 + Spring Boot REST API (CRUD + orchestration)
- **risk-service**: Python FastAPI microservice (risk scoring)
- **PostgreSQL**: persistence
- **Docker Compose**: local dev
- **Kubernetes (manifests)**: deploy to any k8s cluster (e.g., AKS)
- **Jenkinsfile**: CI/CD pipeline
- **Terraform (skeleton)**: Azure RG + AKS + ACR (customize before use)

> This repo is production-minded but intentionally minimal so you can extend it quickly.

---

## Quick Start (Local)

### Prereqs
- Docker & Docker Compose
- JDK 17 (optional if building locally)

### 1) Build & Run with Docker Compose
```bash
docker compose -f deployments/docker-compose.yml up -d --build
# loan-service => http://localhost:8080
# risk-service => http://localhost:8000/docs
```

### 2) Try the API
Create a loan (the service will call `risk-service` to score and auto-approve/reject):
```bash
curl -X POST http://localhost:8080/api/loans   -H 'Content-Type: application/json'   -d '{
    "applicantName": "Alex Doe",
    "ssn": "123-45-6789",
    "income": 85000,
    "loanAmount": 25000,
    "termMonths": 36,
    "creditScore": 720
  }'
```

List loans:
```bash
curl http://localhost:8080/api/loans
```

---

## Project Structure
```
Cloud-Native-Loan-Processing-Platform/
├── README.md
├── services
│   ├── loan-service/                # Spring Boot (Java 17)
│   └── risk-service/                # FastAPI (Python 3.11)
├── deployments
│   ├── docker-compose.yml
│   └── k8s/                         # Kubernetes manifests
│       ├── namespace.yaml
│       ├── postgres.yaml
│       ├── risk-service.yaml
│       └── loan-service.yaml
├── ci
│   └── Jenkinsfile
├── terraform
│   ├── main.tf
│   ├── variables.tf
│   └── outputs.tf
└── sonar-project.properties
```

---

## Kubernetes (Dev)
> Edit container image names if you push to your own registry.

```bash
kubectl apply -f deployments/k8s/namespace.yaml
kubectl apply -f deployments/k8s/postgres.yaml
kubectl apply -f deployments/k8s/risk-service.yaml
kubectl apply -f deployments/k8s/loan-service.yaml

# Port-forward for quick testing:
kubectl -n loan-platform port-forward svc/loan-service 8080:8080
kubectl -n loan-platform port-forward svc/risk-service 8000:8000
```

---

## CI/CD (Jenkins)
- Pipeline builds & tests loan-service, builds/pushes Docker images, applies k8s manifests.
- Configure credentials/params in Jenkins.
- Integrate SonarQube by setting the `sonar.*` properties and server in Jenkins.

---

## Terraform (Azure Skeleton)
- Provisions resource group, ACR, and AKS (simplified).
- **You must** customize variables, auth, and RBAC before `terraform apply`.

---

## Notes
- Security is open-by-default for demo simplicity. Add JWT/OAuth2 as needed.
- `risk-service` uses a simple heuristic risk model (extend with real ML if desired).
- Replace placeholder Docker registry/org names with your own.
