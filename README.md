# Order Management

Production-oriented order management backend built with **Java 21 and Spring Boot**, containerized with Docker and deployed to **Oracle Cloud Infrastructure (OCI) OKE** using Kubernetes and GitHub Actions CI/CD.

**Stack:** Java 21 · Spring Boot · Maven · Docker · Kubernetes · OCI OKE · GitHub Actions · Docker Hub

---

## Architecture

```text
                    Developer
                        │
                        │ git push
                        ▼
                   ┌─────────┐
                   │ GitHub  │
                   └────┬────┘
                        │
                 ┌──────┴──────┐
                 │             │
                CI            CD
                 │             │
                 ▼             │
          ┌────────────┐       │
          │ Docker     │       │
          │ Buildx     │       │
          └─────┬──────┘       │
                │              │
                ▼              ▼
          ┌───────────┐   ┌─────────┐
          │ Docker Hub│   │  OKE    │
          └─────┬─────┘   │Kubernetes│
                │         └────┬────┘
                │              │
                └──────► Deployment
                              │
                              ▼
                       Spring Boot Pod
                              │
                              ▼
                     Kubernetes Service
                              │
                              ▼
                     OCI Load Balancer
                              │
                              ▼
                            Client
```

### Production flow

```text
Code
 ↓
Git Push
 ↓
GitHub Actions
 ↓
Test
 ↓
Build
 ↓
Docker Multi-Architecture Image
 ↓
Docker Hub
 ↓
CD
 ↓
OCI OKE
 ↓
Kubernetes Deployment
 ↓
Spring Boot Pod
 ↓
Health Check
 ↓
Load Balancer
 ↓
Client
```

---

# 1. Development

### Application

```text
Java 21
Spring Boot
Maven
Port: 8080
Profile: prod
```

### Project structure

```text
order-management/
├── src/
│   ├── main/
│   └── test/
├── k8s/
│   ├── deployment.yaml
│   ├── service.yaml
│   ├── service-account.yaml
│   ├── role.yaml
│   └── role-binding.yaml
├── .github/
│   └── workflows/
│       ├── ci.yml
│       └── deploy.yml
├── Dockerfile
├── pom.xml
└── README.md
```

### Development flow

```text
Code
 ↓
Test
 ↓
Commit
 ↓
Push
```

---

# 2. GitHub CI/CD

## CI

**File:** `.github/workflows/ci.yml`

```text
Push / Pull Request
        ↓
Checkout
        ↓
Java 21
        ↓
Maven Test
        ↓
Maven Package
        ↓
Docker Buildx
        ↓
amd64 + arm64
        ↓
Docker Hub
```

### CI responsibilities

- Automated tests
- Maven build
- Docker image build
- Multi-architecture image
- Push image on `master`

---

## CD

**File:** `.github/workflows/deploy.yml`

```text
master
  ↓
Git SHA
  ↓
Kubernetes authentication
  ↓
OKE API
  ↓
Apply Kubernetes manifests
  ↓
Update Deployment image
  ↓
Rolling deployment
  ↓
Verify rollout
```

### GitHub Secrets

```text
DOCKERHUB_USERNAME
DOCKERHUB_TOKEN

KUBE_SERVER
KUBE_CA
KUBE_TOKEN
```

### GitHub → OKE

```text
KUBE_SERVER
    ↓
OKE Kubernetes API

KUBE_CA
    ↓
API certificate verification

KUBE_TOKEN
    ↓
Kubernetes authentication
    ↓
order-management-deployer
```

---

# 3. Docker Hub

### Repository

```text
<DOCKERHUB_USERNAME>/order-management
```

### Image versions

```text
<git-sha>
latest
```

### Architectures

```text
linux/amd64
linux/arm64
```

### Image flow

```text
GitHub Actions
      ↓
Docker Buildx
      ↓
Multi-architecture image
      ↓
Docker Hub
      ↓
OKE pulls image
```

### Version relationship

```text
Git Commit SHA
      ↓
Docker Image Tag
      ↓
Kubernetes Deployment
```

Production deployments use the **Git SHA** image tag so every deployment maps to a specific source revision.

---

# 4. OCI / OKE

### OKE

```text
Cluster:        <OKE_CLUSTER_NAME>
Type:           BASIC_CLUSTER
Region:         <OCI_REGION>
API Server:     <OKE_API_SERVER>
Worker subnet:  <WORKER_SUBNET_CIDR>
Worker:         ARM64
```

### Kubernetes resources

```text
Deployment:
order-management

Service:
order-management-service

Namespace:
default

Container:
Spring Boot :8080
```

---

# 5. Kubernetes

## Deployment

```text
Deployment
    ↓
ReplicaSet
    ↓
Pod
    ↓
Spring Boot
```

Deployment provides:

- Rolling updates
- Replica management
- Resource requests/limits
- Container image versioning
- Health probes

---

## Service

```text
OCI Load Balancer :80
          ↓
Kubernetes Service :80
          ↓
Pod :8080
```

Service configuration:

```text
Type:        LoadBalancer
Port:        80
TargetPort:  8080
Selector:    app=order-management
```

---

## Health checks

```text
/actuator/health
/actuator/health/liveness
/actuator/health/readiness
```

```text
Readiness
   ↓
Pod receives traffic

Liveness
   ↓
Container restart when unhealthy
```

---

# 6. Kubernetes Security

## ServiceAccount

```text
order-management-deployer
```

## RBAC

```text
GitHub Actions
      ↓
ServiceAccount
      ↓
RoleBinding
      ↓
Role
      ↓
Deployment / Service
```

### Allowed resources

```text
Deployments
    get
    list
    watch
    create
    update
    patch

Services
    get
    list
    watch
    create
    update
    patch
```

### Restricted

```text
Secrets       → no access
Roles         → no modification
RoleBindings  → no modification
```

RBAC is namespace-scoped to `default`.

---

# 7. Application Secrets

Application secrets are stored in Kubernetes:

```text
order-management-secret

├── DB_URL
├── DB_USERNAME
├── DB_PASSWORD
└── JWT_SECRET
```

Application:

```text
Kubernetes Secret
        ↓
Environment variables
        ↓
Spring Boot
```

Secret values are **not stored in Git**.

---

# 8. Network Flow

```text
Internet
    │
    ▼
OCI Load Balancer
    │
    │ :80
    ▼
Kubernetes Service
    │
    │ :8080
    ▼
Spring Boot Pod
    │
    ▼
Database
```

OCI network components:

```text
VCN
 ↓
Subnet
 ↓
Route Table
 ↓
Security List
 ↓
Load Balancer / OKE
```

Infrastructure-specific addresses are intentionally omitted from this public documentation.

---

# 9. Deployment and Rollback

## Deployment

```text
git push
   ↓
CI
   ↓
Docker image
   ↓
Docker Hub
   ↓
CD
   ↓
OKE
   ↓
Rolling update
   ↓
Readiness check
   ↓
Traffic
```

## Verify

```bash
kubectl get pods
kubectl get deployment
kubectl get service
kubectl rollout status deployment/order-management
```

## Health

```bash
curl http://<LOAD_BALANCER_IP>/actuator/health
```

## Rollback

```bash
kubectl rollout history deployment/order-management

kubectl rollout undo deployment/order-management

kubectl rollout status deployment/order-management
```

---

# 10. Production Capabilities

- CI/CD with GitHub Actions
- Java 21 and Spring Boot
- Automated Maven tests
- Docker containerization
- Multi-architecture Docker images
- Git SHA image versioning
- Kubernetes deployments
- Rolling updates
- Kubernetes RBAC
- Kubernetes Secrets
- Liveness and readiness probes
- OCI OKE deployment
- OCI Load Balancer
- Production health checks
- Deployment rollback

---

# 11. Security Notes

```text
GitHub Secrets
      ↓
CI/CD authentication

Kubernetes Secrets
      ↓
Application credentials

RBAC
      ↓
Least-privilege deployment access
```

Never commit:

```text
KUBE_TOKEN values
KUBE_CA values
Database credentials
JWT_SECRET values
Docker Hub credentials
```

The current OKE cluster uses a temporary Kubernetes ServiceAccount token for CD authentication. A future hardening step is GitHub OIDC federation with an OKE configuration that supports it.

---

# 12. What This Project Demonstrates

This project demonstrates an end-to-end production deployment workflow:

```text
Software Development
        ↓
Automated Testing
        ↓
Containerization
        ↓
Multi-Architecture Build
        ↓
Container Registry
        ↓
CI/CD Automation
        ↓
Kubernetes
        ↓
Cloud Infrastructure
        ↓
Security / RBAC
        ↓
Health Monitoring
        ↓
Rolling Deployment
        ↓
Rollback
```

It demonstrates how application code moves from **development to a running cloud workload through an automated, versioned deployment pipeline**.
