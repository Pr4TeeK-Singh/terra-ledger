# 🏔️ Terra Ledger — Land Management System

A full-stack property management application for registering land parcels, tracking plots, managing owners, sellers, and brokers — with JWT-secured APIs and a clean Angular dashboard.

---

## 📸 Overview

Terra Ledger provides a complete workflow for land registry management:

- Register land parcels with owner details, dimensions, purchase rates, and contract dates
- Divide land into individual plots and track their status (Available / Sold / Reserved)
- Manage owners, sellers, and brokers inline — create new entries or link existing ones
- Secure all write operations behind JWT authentication
- View, edit, and export all records through an AG Grid-powered interface
- Monitor runtime and HTTP errors via a dedicated in-session Error Log

---

## 🗂️ Project Structure

```
terra-ledger/
├── land-backend/          # Spring Boot REST API
│   └── src/main/java/com/landmgmt/landbackend/
│       ├── auth/          # Login endpoint & JWT response models
│       ├── config/        # OpenAPI config, request logging filter
│       ├── controller/    # LandController, PlotController
│       ├── dao/           # JDBC DAOs for Land, Plot, Owner, Seller, Broker
│       ├── exception/     # Custom exceptions & GlobalExceptionHandler
│       ├── model/         # Domain models (LandDetails, Plot, Owner, …)
│       ├── security/      # JwtUtil
│       └── service/       # LandService, PlotService
│
└── land-frontend/         # Angular 17+ standalone app
    └── src/app/
        ├── auth/login/    # Login page
        ├── land/
        │   ├── land-main/ # Root layout with navbar
        │   ├── land-form/ # New land record form
        │   ├── land-list/ # AG Grid land table + inline edit
        │   └── plot-panel/# Per-land plot grid
        ├── plot-form/     # Add new plot form
        ├── errors/        # In-session error log viewer
        ├── models/        # TypeScript interfaces
        └── services/      # HTTP services, auth interceptor, error handler
```

---

## ⚙️ Tech Stack

| Layer      | Technology                                      |
|------------|-------------------------------------------------|
| Backend    | Java 17, Spring Boot 3, JJWT, SpringDoc OpenAPI |
| Database   | MySQL (raw JDBC via `DataSource`)               |
| Frontend   | Angular 17+, Standalone Components, AG Grid     |
| Auth       | JWT (Bearer token), HTTP Interceptor            |
| Styling    | CSS Variables, Figtree + Cormorant Garamond      |
| Build      | Maven (backend), Angular CLI (frontend)         |

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Node.js 18+ & Angular CLI (`npm install -g @angular/cli`)
- MySQL 8+
- Maven 3.8+

---

### 1. Database Setup

Create a MySQL database and run the following schema:

```sql
CREATE DATABASE land_management;
USE land_management;

CREATE TABLE owner_details (
  owner_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
  name       VARCHAR(100),
  contact_no VARCHAR(15),
  address    VARCHAR(255),
  aadhar_no  VARCHAR(12)
);

CREATE TABLE land_details (
  land_id                BIGINT AUTO_INCREMENT PRIMARY KEY,
  gaata_number           VARCHAR(50),
  location_address       VARCHAR(255),
  length_in_sqft         DECIMAL(10,2),
  width_in_sqft          DECIMAL(10,2),
  purchase_rate_per_sqft DECIMAL(10,2),
  total_cost             DECIMAL(14,2),
  paid_amount            DECIMAL(14,2),
  balance_amount         DECIMAL(14,2),
  contract_start_date    DATE,
  contract_end_date      DATE,
  owner_id               BIGINT,
  FOREIGN KEY (owner_id) REFERENCES owner_details(owner_id)
);

CREATE TABLE seller_details (
  seller_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
  name       VARCHAR(100),
  contact_no VARCHAR(15),
  address    VARCHAR(255),
  aadhar_no  VARCHAR(12)
);

CREATE TABLE broker_details (
  broker_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
  name       VARCHAR(100),
  contact_no VARCHAR(15),
  address    VARCHAR(255),
  aadhar_no  VARCHAR(12)
);

CREATE TABLE plots (
  plot_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
  gaata_no     VARCHAR(50),
  land_id      BIGINT,
  plot_no      VARCHAR(50),
  land_length  DECIMAL(10,2),
  land_width   DECIMAL(10,2),
  sell_rate    DECIMAL(10,2),
  total_amount DECIMAL(14,2),
  status       VARCHAR(20),
  seller_id    BIGINT,
  broker_id    BIGINT,
  FOREIGN KEY (land_id)   REFERENCES land_details(land_id),
  FOREIGN KEY (seller_id) REFERENCES seller_details(seller_id),
  FOREIGN KEY (broker_id) REFERENCES broker_details(broker_id)
);
```

---

### 2. Backend Setup

Navigate to the backend directory:

```bash
cd land-backend
```

Configure your database credentials and JWT secret in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/land_management
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password

# Generate a 64-byte hex string for the secret (128 hex characters)
jwt.secret=your_hex_encoded_256bit_secret
jwt.expiration-ms=86400000
```

> **Tip:** Generate a secure JWT secret with:
> ```bash
> openssl rand -hex 64
> ```

Run the application:

```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`.
Swagger UI: `http://localhost:8080/swagger-ui.html`

---

### 3. Frontend Setup

Navigate to the frontend directory:

```bash
cd land-frontend
npm install
ng serve
```

The app will be available at `http://localhost:4200`.

---

## 🔐 Authentication

The app uses a single admin account (configurable in `AuthController.java`):

| Username | Password  |
|----------|-----------|
| `admin`  | `admin123` |

> ⚠️ **For production use**, replace the hardcoded credentials with a proper user store (database-backed) and hash passwords with BCrypt.

On login, the backend returns a JWT token which is stored in `localStorage` and automatically attached to all subsequent API requests via the Angular HTTP interceptor.

---

## 📡 API Reference

### Auth

| Method | Endpoint         | Auth | Description        |
|--------|------------------|------|--------------------|
| POST   | `/api/auth/login`| No   | Returns JWT token  |

### Lands

| Method | Endpoint        | Auth     | Description             |
|--------|-----------------|----------|-------------------------|
| GET    | `/api/lands`    | No       | Fetch all land records  |
| POST   | `/api/lands`    | Required | Create a new land record|
| PUT    | `/api/lands/{id}`| No      | Update a land record    |

### Plots

| Method | Endpoint                   | Auth | Description                    |
|--------|----------------------------|------|--------------------------------|
| GET    | `/api/plots/land/{landId}` | No   | Get all plots for a land parcel|
| POST   | `/api/plots`               | No   | Create a new plot              |
| PUT    | `/api/plots/{plotId}`      | No   | Update a plot                  |

---

## 🧩 Key Features

### Land Management
- Register land parcels with gaata number, location, dimensions, and purchase rate
- Auto-calculates total cost and balance amount
- Link existing owners or create new ones via an inline modal

### Plot Management
- Subdivide a land parcel into numbered plots
- Track sell rate, dimensions, and total amount per plot
- Set plot status: **Available**, **Sold**, or **Reserved**
- Inline grid editing — changes save automatically on cell edit
- Attach sellers and brokers to each plot

### Owner / Seller / Broker
- Owners are managed through the land form (select existing or register new)
- Sellers and brokers can be added inline when creating/editing plots

### Error Log
- All HTTP errors (4xx / 5xx) and Angular runtime errors are captured in-session
- Viewable at `/errors` with type badges, status codes, timestamps, and URLs
- Capped at the last 100 entries per session; clearable manually

### AG Grid Tables
- Full column filtering, sorting, and resizing
- Inline cell editing with auto-save
- CSV export
- Pagination with configurable page sizes

---

## 🏗️ Architecture Notes

- **No ORM** — the backend uses raw JDBC (`DataSource`) for all database operations, keeping queries explicit and predictable.
- **Standalone Angular components** — no `NgModule` declarations; each component imports only what it needs.
- **JWT validation is manual in `LandController`** — consider moving this to a Spring Security filter chain for consistency across all protected endpoints.
- **CORS** is currently hardcoded to `http://localhost:4200` in each controller — centralise this in a `WebMvcConfigurer` for production.

---

## ⚠️ Known Limitations & TODOs

- [ ] **Delete endpoint** is not yet implemented on the backend (`deleteSelected()` in `land-list.ts` shows a placeholder alert)
- [ ] **No input validation** on the frontend forms (required fields, format checks)
- [ ] **Hardcoded credentials** in `AuthController` — replace with a user table
- [ ] **JWT auth is inconsistent** — `POST /api/lands` requires a token but `PUT /api/lands/{id}` and all plot endpoints do not
- [ ] **Owners list in the form** is fetched by scanning all land records — a dedicated `GET /api/owners` endpoint would be cleaner
- [ ] **No DELETE API** for lands or plots
- [ ] **`app.html` is unused** — `LandMainComponent` serves as the actual root layout

---

## 🧪 Running Tests

```bash
# Backend
cd land-backend
mvn test

# Frontend
cd land-frontend
ng test
```

---

## 📄 License

This project is for personal/educational use. Add a license of your choice before public distribution.
