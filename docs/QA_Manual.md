# QA Manual - Polarent

## 1. Introduction
This document describes the Software Quality Assurance (SQA) strategy, practices, and tools used in the Polarent project.

## 2. Testing Strategy

### 2.1 Test Levels
| Level | Description | Tools |
|-------|-------------|-------|
| Unit Tests | Test individual components/services | JUnit 5, Mockito |
| Integration Tests | Test component interactions | Spring Boot Test, TestRestTemplate |
| E2E Tests | Test complete user flows | Playwright |
| API Tests | Test REST endpoints | Cucumber (BDD) |
| Performance Tests | Load and stress testing | K6 |

### 2.2 Test Coverage
- Target: 80% code coverage
- Tool: JaCoCo
- Dashboard: [SonarCloud](https://sonarcloud.io/project/overview?id=PolarentOrg_Polarent-TQS-Project)

## 3. BDD with Cucumber
Feature files located in `src/test/resources/features/`:
- `authentication.feature` - User registration and login
- `bookings.feature` - Booking management
- `listings.feature` - Equipment listings
- `requests.feature` - Rental requests
- `make_requests.feature` - Creating rental requests

## 4. Static Code Analysis

### 4.1 SonarQube
- Platform: SonarCloud
- Quality Gate: Default (enforced in CI)
- Metrics tracked: Bugs, Vulnerabilities, Code Smells, Coverage, Duplications

### 4.2 OWASP Dependency Check
- Scans for known vulnerabilities in dependencies
- Fails build on CVSS score ≥ 9 (critical)
- Run: `mvn dependency-check:check -DnvdApiKey=YOUR_KEY`

## 5. CI/CD Pipeline

### 5.1 Continuous Integration (ci.yml)
- Triggered on: push/PR to main, dev
- Steps: Build, Test, Coverage Report

### 5.2 SonarQube Analysis (build.yml)
- Triggered on: push/PR to main, dev
- Steps: Build, Analyze, Quality Gate

### 5.3 Continuous Deployment (cd.yml)
- Triggered on: push to dev
- Deploys to: Self-hosted runner with Docker Compose

### 5.4 Playwright Tests (playwright.yml)
- Triggered on: push/PR to main, dev
- Runs E2E browser tests against Docker environment

## 6. Performance Testing (K6)

### 6.1 Test Files
- `basic-test.js` - Basic load test (10 VUs, 30s)
- `slo-test.js` - SLO validation test

### 6.2 Service Level Objectives
- Error rate: < 1%
- 95th percentile response time: < 500ms

### 6.3 Running K6 Tests
```bash
# Start application
docker compose up -d

# Run tests
k6 run app/Polarent/src/test/java/com/tqs/polarent/performance/slo-test.js
```

## 7. Observability

### 7.1 Metrics
- Spring Boot Actuator endpoints
- Micrometer + Prometheus metrics
- Endpoint: `/actuator/prometheus`

### 7.2 Monitoring Stack
- Prometheus: Metrics collection (port 9091)
- Grafana: Dashboards (port 3001)

## 8. Code Review Process
- All changes via Pull Requests
- Minimum 1 approval required
- CI must pass before merge
- Feature branches follow pattern: `feature/<description>`

## 9. Running Tests

```bash
# All tests
mvn test

# Specific test class
mvn test -Dtest=ClassName

# Cucumber tests only
mvn test -Dtest=CucumberTest

# Playwright tests (requires running app)
docker compose up -d
mvn test -Dtest=AuthenticationPlaywrightTest

# Generate coverage report
mvn jacoco:report
```
