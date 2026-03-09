# TerraLedger

**A Real Estate ERP system for managing land records, property transactions, plot management, and administrative operations in a centralized platform.**

---

## Overview

TerraLedger is a full-stack web application built to streamline the management of land and property data. It provides a structured interface to register land parcels, track ownership, manage plot subdivisions, and handle seller/broker assignments — all secured behind JWT authentication.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 25, Spring Boot 3.5.11 |
| Database | MySQL 8 |
| Data Access | Pure JDBC (no ORM) |
| Auth | JWT (JJWT 0.12.6) |
| API Docs | Springdoc OpenAPI / Swagger UI |
| Frontend | Angular 17+ (standalone components) |
| Styling | Custom CSS (Cormorant Garamond + Figtree) |

---

## Features

- **Land Records** — Register land parcels with gaata number, location, dimensions, purchase rate, and contract dates
- **Owner Management** — Assign existing owners or register new ones inline — no separate API call
- **Plot Management** — Subdivide land into plots, track area usage (total / used / remaining), set sell rates
- **Seller & Broker Assignment** — Attach sellers and brokers to plots; existing ones are extracted from loaded data — no separate API call
- **JWT Authentication** — Login-protected access; token auto-attached to all requests via HTTP interceptor
- **Inline Edit** — Edit land and plot records directly in the table with snapshot-based cancel
- **Auto Calculation** — Total cost, balance amount, and plot total auto-calculate on input

---

## Project Structure

```
terra-ledger/
├── land-backend/               # Spring Boot backend
│   └── src/main/
│       ├── java/com/landmgmt/landbackend/
│       │   ├── auth/           # AuthController, AuthRequest, AuthResponse
│       │   ├── controller/     # LandController, PlotController
│       │   ├── dao/            # DAO interfaces + pure JDBC implementations
│       │   ├── model/          # LandDetails, Owner, Plot, Seller, Broker
│       │   ├── security/       # JwtUtil
│       │   ├── service/        # LandService, PlotService
│       │   └── LandBackendApplication.java
│       └── resources/
│           ├── application.properties
│           └── schema_plots.sql
│
└── land-frontend/              # Angular frontend
    └── src/app/
        ├── auth/login/         # Login page
        ├── land/
        │   ├── land-form/      # Add land record form
        │   ├── land-list/      # Land records table with inline edit
        │   ├── land-main/      # Protected shell (navbar + content)
        │   └── plot-panel/     # Inline plot management panel
        ├── models/             # TypeScript interfaces
        ├── services/           # HTTP services + auth interceptor
        ├── app.ts
        ├── app.routes.ts       # Lazy-loaded routes with auth guard
        └── app.config.ts
```

---

## Database Schema

```sql
owner_details   — owner_id, name, contact_no, address, aadhar_no
land_details    — land_id, gaata_number, location_address, length_in_sqft,
                  width_in_sqft, purchase_rate_per_sqft, total_cost,
                  paid_amount, balance_amount, contract_start_date,
                  contract_end_date, owner_id (FK)
seller_details  — seller_id, name, contact_no, address, aadhar_no
broker_details  — broker_id, name, contact_no, address, aadhar_no
plots           — plot_id, gaata_no, land_id (FK), plot_no, land_length,
                  land_width, sell_rate, total_amount,
                  status ENUM('AVAILABLE','SOLD','RESERVED'),
                  seller_id (FK), broker_id (FK)
```

---

## API Endpoints

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/api/auth/login` | — | Login, returns JWT |
| `POST` | `/api/lands` | ✅ JWT | Save new land record |
| `PUT` | `/api/lands/{id}` | — | Update land record |
| `GET` | `/api/lands` | — | Get all land records |
| `POST` | `/api/plots` | — | Save new plot |
| `PUT` | `/api/plots/{plotId}` | — | Update plot |
| `GET` | `/api/plots/land/{landId}` | — | Get plots by land |

Swagger UI available at: `http://localhost:8080/swagger-ui.html`

---

## Getting Started

### Prerequisites

- Java 25+
- Maven 3.9+
- MySQL 8+
- Node.js 18+
- Angular CLI 17+

### Backend Setup

**1. Create the database**

```sql
CREATE DATABASE land_mgmt;
```

Then run `schema_plots.sql` to create all tables.

**2. Configure credentials**

Edit `land-backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/land_mgmt
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
jwt.secret=YOUR_HEX_SECRET_64_CHARS
jwt.expiration-ms=86400000
```

**3. Run the backend**

```bash
cd land-backend
mvn spring-boot:run
```

Backend starts at `http://localhost:8080`

---

### Frontend Setup

```bash
cd land-frontend
npm install
ng serve
```

Frontend starts at `http://localhost:4200`

---

## Default Login

```
Username: admin
Password: admin123
```

> To change credentials, update `VALID_USERNAME` and `VALID_PASSWORD` in `AuthController.java`.

---

## Design System

| Token | Value | Usage |
|---|---|---|
| `--paper` | `#F6F4F0` | Page background |
| `--accent` | `#1E5C3A` | Forest green — primary actions |
| `--ink` | `#141210` | Body text |
| `--amber` | `#92400E` | Reserved status |
| `--blue` | `#1D4ED8` | Broker pills |
| `--red` | `#B91C1C` | Sold status |

Fonts: **Cormorant Garamond** (headings) + **Figtree** (body)

---

## Security Note

`application.properties` contains sensitive credentials. For production use, replace hardcoded values with environment variables:

```properties
spring.datasource.password=${DB_PASSWORD}
jwt.secret=${JWT_SECRET}
```

And add `application.properties` to `.gitignore`.

---

## License

This project is private. All rights reserved.
