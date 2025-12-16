# Project Specification - Polarent

## 1. Project Overview

### 1.1 Abstract
Polarent is a peer-to-peer rental marketplace for photo and video equipment. The platform enables equipment owners to monetize their idle gear while providing renters affordable access to professional equipment they need occasionally.

### 1.2 Domain
Photo & Video Equipment Rentals

## 2. Actors

| Actor | Description |
|-------|-------------|
| Renter | Searches, browses, and books equipment |
| Owner | Lists equipment and manages availability |
| Admin | Manages platform operations and users |

## 3. Epics & User Stories

### Epic 1: User Authentication
- US1.1: As a user, I can register with email and password
- US1.2: As a user, I can login to access the platform
- US1.3: As a user, I can logout from the platform

### Epic 2: Equipment Listings
- US2.1: As an owner, I can create a new equipment listing
- US2.2: As an owner, I can update my listing details
- US2.3: As an owner, I can activate/deactivate listings
- US2.4: As a renter, I can browse available equipment
- US2.5: As a renter, I can search equipment by name
- US2.6: As a renter, I can filter by price, city, district

### Epic 3: Booking & Requests
- US3.1: As a renter, I can request to book equipment
- US3.2: As an owner, I can approve/decline booking requests
- US3.3: As a renter, I can view my booking history
- US3.4: As an owner, I can view incoming requests

### Epic 4: Dashboards
- US4.1: As a renter, I can see my rental dashboard with stats
- US4.2: As an owner, I can see my owner dashboard with earnings
- US4.3: As an admin, I can view platform metrics

## 4. System Architecture

### 4.1 Technology Stack
| Layer | Technology |
|-------|------------|
| Frontend | HTML, CSS, JavaScript |
| Backend | Spring Boot 3.4, Java 21 |
| Database | PostgreSQL 16 |
| Containerization | Docker, Docker Compose |
| CI/CD | GitHub Actions |
| Monitoring | Prometheus, Grafana |

### 4.2 Architecture Diagram
```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Frontend  │────▶│   Backend   │────▶│  PostgreSQL │
│   (Nginx)   │     │ (Spring Boot)│     │             │
└─────────────┘     └─────────────┘     └─────────────┘
      :8081              :8080               :5432
                            │
                            ▼
                    ┌─────────────┐
                    │ Prometheus  │
                    └─────────────┘
                          │
                          ▼
                    ┌─────────────┐
                    │   Grafana   │
                    └─────────────┘
```

### 4.3 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/register | Register new user |
| POST | /api/auth/login | User login |
| GET | /api/listings/enabled | Get active listings |
| GET | /api/listings/search | Search listings |
| GET | /api/listings/filter/advanced | Filter listings |
| POST | /api/listings | Create listing |
| GET | /api/requests/renter/{id} | Get renter's requests |
| POST | /api/requests | Create booking request |
| PUT | /api/requests/{id}/approve | Approve request |
| PUT | /api/requests/{id}/decline | Decline request |
| GET | /api/bookings/renter/{id} | Get renter's bookings |

## 5. Data Model

### 5.1 Main Entities
- **User**: id, firstName, lastName, email, password, role
- **Listing**: id, name, description, pricePerDay, city, district, owner, enabled
- **Request**: id, listing, renter, startDate, endDate, status
- **Booking**: id, request, status, totalPrice

## 6. Deployment

### 6.1 Environments
| Environment | URL | Compose File |
|-------------|-----|--------------|
| Development | localhost:8081 | docker-compose.yml |
| Production | localhost:8080 | docker-compose.prod.yml |

### 6.2 Running Locally
```bash
# Development
docker compose up -d --build

# Production
docker compose -f docker-compose.prod.yml up -d --build
```

## 7. Project Backlog
[Jira Board](https://polarent.atlassian.net)
