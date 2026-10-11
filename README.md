# BookNest: Smart Online Book Explorer

A full-stack web application for exploring academic and general books, filtering by category, sorting, managing favorites, and registering new books. Built with Spring Boot, Spring JDBC (`JdbcTemplate`), MySQL 8, and a responsive Bootstrap frontend.

## Live Demo

- **Website:** [https://satish180307.github.io/BookNest/](https://satish180307.github.io/BookNest/)
- **Backend API:** [https://booknest-backend-3ilg.onrender.com/api/books](https://booknest-backend-3ilg.onrender.com/api/books)

> The backend runs on Render's free tier, which sleeps after inactivity. The first request after a idle period can take about 50 seconds.

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Key Features](#2-key-features)
3. [Technology Stack](#3-technology-stack)
4. [Architecture](#4-architecture)
5. [Project Structure](#5-project-structure)
6. [Database Setup](#6-database-setup)
7. [Run Locally](#7-run-locally)
8. [REST API Endpoints](#8-rest-api-endpoints)
9. [Deployment](#9-deployment)
10. [Troubleshooting](#10-troubleshooting)

---

## 1. Project Overview

- **Frontend:** HTML5, CSS3, JavaScript (ES6+), and Bootstrap 5, hosted on GitHub Pages.
- **Backend:** Spring Boot REST API, hosted on Render (Docker).
- **Database:** MySQL 8 (`booknest_db`), hosted on Aiven.
- **Data access:** Spring JDBC with `JdbcTemplate` and parameterized queries. No JPA or Hibernate.
- **No fake data:** the frontend shows real data from MySQL only. If the backend is unreachable, it shows an error notice instead.

---

## 2. Key Features

- **Dynamic catalog:** books load from MySQL through the REST API.
- **Category filtering:** Programming, Database, Web Development, AI, and Fiction, with no page reload.
- **Sorting:** Featured, Rating (high to low), Price (low to high), Price (high to low), and Name (A-Z).
- **Book details modal:** title, author, ISBN, category, price, availability, rating, and description.
- **Favorites:** add and remove favorites, stored permanently in MySQL.
- **Book registration:** a validated form that submits JSON to `POST /api/books`.
- **Surprise Me:** opens a random book from the current list.
- **Toast notifications:** confirm actions such as adding a book or toggling a favorite.
- **Responsive design:** works on desktop and mobile.

---

## 3. Technology Stack

| Layer | Technology |
|---|---|
| Frontend | HTML5, CSS3, JavaScript (Fetch API), Bootstrap 5.3.3 |
| Backend | Java 17, Spring Boot 3.2.5, Spring Web, Spring JDBC |
| Database | MySQL 8 (Aiven) |
| Driver | MySQL Connector/J 8.3.0 |
| Connection pool | HikariCP |
| Build tool | Apache Maven 3.9+ |
| Containerization | Docker (multi-stage build) |
| Hosting | GitHub Pages (frontend), Render (backend), Aiven (database) |

---

## 4. Architecture

```
Browser (GitHub Pages)
        |
        | HTTPS requests (fetch, JSON)
        v
Controller layer   (BookController, FavoriteController)
        |
        v
Service layer      (BookService, FavoriteService)
        |
        v
Repository layer   (BookRepository, FavoriteRepository)
        |
        | Parameterized SQL via JdbcTemplate
        v
MySQL database     (booknest_db on Aiven)
```

- **Model:** `Book`, `Favorite`, plus DTOs `BookRequest` and `ApiResponse`.
- **View:** `index.html`, `style.css`, `script.js`.
- **Controller:** REST endpoints, input validation, HTTP status codes.
- **Service:** business rules and category validation.
- **Repository:** SQL execution through `JdbcTemplate`.

---

## 5. Project Structure

```
BookNest/
│
├── frontend/
│   ├── index.html
│   ├── privacy-policy.html
│   ├── terms-and-conditions.html
│   ├── 404.html
│   ├── favicon.svg
│   ├── css/style.css
│   ├── js/script.js
│   └── assets/images/
│
├── backend/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/booknest/
│       │   ├── BookNestApplication.java
│       │   ├── controller/   (BookController, FavoriteController)
│       │   ├── service/      (BookService, FavoriteService)
│       │   ├── repository/   (BookRepository, FavoriteRepository)
│       │   ├── model/        (Book, Favorite)
│       │   ├── dto/          (ApiResponse, BookRequest)
│       │   └── config/       (CorsConfig)
│       └── resources/
│           ├── application.properties
│           └── schema.sql
│
├── database/
│   └── booknest.sql
│
├── index.html            (root copy served by GitHub Pages)
├── privacy-policy.html
├── terms-and-conditions.html
├── 404.html
├── favicon.svg
├── css/style.css
├── js/script.js
└── README.md
```

---

## 6. Database Setup

The application uses a MySQL database named `booknest_db`.

### Import schema and seed data

```sql
SOURCE database/booknest.sql;
```

You can also paste the contents of `database/booknest.sql` into MySQL Workbench or any MySQL client.

### Tables

**`books`**

| Column | Type |
|---|---|
| id | INT AUTO_INCREMENT PRIMARY KEY |
| title | VARCHAR(255) NOT NULL |
| author | VARCHAR(255) NOT NULL |
| isbn | VARCHAR(50) |
| category | VARCHAR(100) |
| price | DECIMAL(10,2) |
| availability | BOOLEAN DEFAULT TRUE |
| description | TEXT |
| rating | DECIMAL(2,1) |
| created_at | TIMESTAMP DEFAULT CURRENT_TIMESTAMP |

**`favorites`**

| Column | Type |
|---|---|
| id | INT AUTO_INCREMENT PRIMARY KEY |
| book_id | INT NOT NULL (foreign key to `books.id`, ON DELETE CASCADE) |
| created_at | TIMESTAMP DEFAULT CURRENT_TIMESTAMP |

### Seed books

| Title | Author | Category | Price | Rating | Available |
|---|---|---|---|---|---|
| Java Programming | Herbert Schildt | Programming | Rs. 450 | 4.8 | Yes |
| Python Crash Course | Eric Matthes | Programming | Rs. 520 | 4.7 | Yes |
| Database System Concepts | Abraham Silberschatz | Database | Rs. 680 | 4.6 | Yes |
| HTML and CSS | Jon Duckett | Web Development | Rs. 590 | 4.7 | Yes |
| Artificial Intelligence | Stuart Russell | AI | Rs. 750 | 4.8 | No |
| The Alchemist | Paulo Coelho | Fiction | Rs. 299 | 4.5 | Yes |

---

## 7. Run Locally

### Prerequisites

- Java 17 or later
- Maven 3.9+
- MySQL 8+
- A static file server (VS Code Live Server or Python)

### Backend

The backend reads its database settings from environment variables, so no credentials are stored in the code.

`backend/src/main/resources/application.properties`:

```properties
server.port=${PORT:8080}

spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.sql.init.mode=never
spring.sql.init.continue-on-error=true

logging.level.com.booknest=INFO

spring.datasource.hikari.maximum-pool-size=2
spring.datasource.hikari.minimum-idle=1
spring.datasource.hikari.initialization-fail-timeout=-1
spring.datasource.hikari.connection-timeout=30000
```

Set the variables, then start the app.

**Windows (PowerShell):**

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/booknest_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_mysql_password"
cd backend
mvn spring-boot:run
```

**Linux / macOS:**

```bash
export DB_URL="jdbc:mysql://localhost:3306/booknest_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export DB_USERNAME="root"
export DB_PASSWORD="your_mysql_password"
cd backend
mvn spring-boot:run
```

The API starts at `http://localhost:8080`.

To build and run the jar instead:

```bash
mvn clean package -DskipTests
java -jar target/booknest-backend-1.0.0.jar
```

### Frontend

In `js/script.js`, point the API constant at your local backend:

```javascript
const API_BASE_URL = "http://localhost:8080/api";
```

Then serve the frontend:

- **VS Code Live Server:** right-click `index.html` and choose **Open with Live Server**.
- **Python:** run `python -m http.server 5500` and open `http://localhost:5500`.

For the deployed site, `API_BASE_URL` is set to `https://booknest-backend-3ilg.onrender.com/api`.

---

## 8. REST API Endpoints

### Books (`/api/books`)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/books` | List all books. Supports `?sort=featured\|rating_desc\|price_asc\|price_desc\|name_asc` and `?category=...` |
| GET | `/api/books/{id}` | Get one book |
| GET | `/api/books/category/{category}` | Get books in a category |
| POST | `/api/books` | Register a new book |
| PUT | `/api/books/{id}` | Update a book |
| DELETE | `/api/books/{id}` | Delete a book |

### Favorites (`/api/favorites`)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/favorites` | List favorite books |
| GET | `/api/favorites/ids` | List favorited book IDs |
| GET | `/api/favorites/{bookId}/status` | Check whether a book is a favorite |
| POST | `/api/favorites/{bookId}` | Add a favorite |
| DELETE | `/api/favorites/{bookId}` | Remove a favorite |

### Response format

```json
{
  "success": true,
  "message": "Books retrieved successfully",
  "data": []
}
```

---

## 9. Deployment

| Part | Platform | Notes |
|---|---|---|
| Frontend | GitHub Pages | Served from the `main` branch |
| Backend | Render (Docker web service) | Builds from `backend/Dockerfile` |
| Database | Aiven for MySQL | Public access enabled, SSL required |

### Backend on Render

Set these environment variables on the Render service (Environment tab):

| Key | Value |
|---|---|
| `DB_URL` | `jdbc:mysql://<aiven-host>:<port>/booknest_db?sslMode=REQUIRED&serverTimezone=UTC` |
| `DB_USERNAME` | your Aiven database user |
| `DB_PASSWORD` | your Aiven database password |

Never commit the password to GitHub. Keep it in Render's environment settings only.

Render provides the `PORT` variable, and the app reads it through `server.port=${PORT:8080}`.

### Dockerfile

```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/booknest-backend-1.0.0.jar app.jar
EXPOSE 10000
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### CORS

`config/CorsConfig.java` must allow the origin of the deployed frontend, otherwise browsers block API calls from GitHub Pages. Allowed origins:

```
https://satish180307.github.io
http://localhost:5500
http://127.0.0.1:5500
```

### Frontend on GitHub Pages

1. Open **Settings > Pages** in the repository.
2. Set **Source** to **Deploy from a branch**, branch `main`, folder `/ (root)`.
3. Make sure `index.html` is in the repository root.
4. Make sure `API_BASE_URL` in `js/script.js` points to the Render backend.

---

## 10. Troubleshooting

- **The site shows "Unable to load books":** the Render backend may be asleep. Wait about 50 seconds and refresh. If it persists, check the Render logs.
- **CORS error in the browser console:** add `https://satish180307.github.io` to the allowed origins in `CorsConfig.java` and redeploy.
- **`Connection refused` on startup, with the Aiven database confirmed reachable:** check `application.properties`. If `spring.datasource.url` is hardcoded to `localhost:3306`, the app never reaches Aiven. It must be `${DB_URL}`.
- **`Access denied for user`:** check that `DB_USERNAME` and `DB_PASSWORD` are correct in the environment.
- **Port 8080 already in use (local):** change `server.port` and update `API_BASE_URL` in `js/script.js` to match.
- **Database does not exist:** import `database/booknest.sql` to create `booknest_db` and its tables.

---

## Author

Built by Satish Chandravanshi.
