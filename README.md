# BookNest: Smart Online Book Explorer

A complete full-stack web application designed for exploring academic and general books, filtering by categories, sorting dynamically, managing personal favorites, and registering new books. Built with a clean Model-View-Controller (MVC) architecture using Spring Boot, Spring JDBC (JdbcTemplate), MySQL 8, and a responsive frontend.

---

## 1. Project Overview

BookNest provides a clean, academic book exploration platform. It is built as a complete full-stack project where:
- The frontend is built using standard HTML5, CSS3, JavaScript, and Bootstrap 5.
- The backend is a Spring Boot application providing RESTful endpoints.
- Database access is handled via Spring JDBC using `JdbcTemplate` with parameterized queries.
- Data is stored in a relational MySQL 8 database named `booknest_db`.
- No JPA, Hibernate, or frontend local storage fallbacks are used for database entities.

---

## 2. Key Features

- **Dynamic Catalog:** Browse books loaded directly from the MySQL database through Spring Boot REST APIs.
- **Category Filtering:** Filter books across Programming, Database, Web Development, AI, and Fiction with zero page reloads.
- **Dynamic Sorting:** Sort books via backend query parameters by Featured, Rating High to Low, Price Low to High, Price High to Low, and Name A-Z.
- **Book Details Modal:** Inspect detailed metadata (title, author, ISBN, category, price, availability, and description) fetched from MySQL.
- **Favorites Management:** Add and remove books from favorites stored permanently in MySQL.
- **Book Registration:** Register new books via an HTML5 validated form submitting JSON payloads to `POST /api/books`.
- **Surprise Me:** Discover a random title from the collection at the click of a button.
- **Non-Intrusive Notifications:** Bootstrap toasts confirm user actions like adding books or toggling favorites.
- **Responsive Design:** Clean, academic light visual style without purple gradients, excessive animations, or horizontal scrolling.

---

## 3. Technology Stack

### Frontend
- **HTML5:** Semantic document structure and native form constraint validation.
- **CSS3:** Custom styles, CSS variables, and responsive media queries.
- **Bootstrap 5 (v5.3.3):** Responsive 12-column grid, navbar, modal, and toast components.
- **JavaScript (ES6+):** Pure vanilla JavaScript communicating with the backend using the Fetch API.

### Backend
- **Java:** Object-oriented backend language (compatible with Java 17+ and JDK 26).
- **Spring Boot (3.2.5):** Application framework and REST API controller layer.
- **Spring Web:** HTTP request routing and JSON serialization.
- **Spring JDBC (`JdbcTemplate`):** Direct SQL query execution with parameterization (no JPA, no Hibernate).
- **MySQL Connector/J:** Official JDBC driver for MySQL communication.

### Database
- **MySQL Server 8.0+:** Relational database storing catalog books and user favorites.
- **Database Name:** `booknest_db`.

### Build Tool
- **Apache Maven 3.9+:** Dependency management and build packaging.

---

## 4. MVC Architecture

BookNest strictly separates concerns across the Model-View-Controller layers:

```
USER INTERFACE (Browser)
       |
       | HTTP Requests (fetch JSON)
       v
CONTROLLER LAYER (BookController, FavoriteController)
       |
       v
SERVICE LAYER (BookService, FavoriteService)
       |
       v
REPOSITORY LAYER (BookRepository, FavoriteRepository)
       |
       | Parameterized SQL Queries via JdbcTemplate
       v
DATABASE LAYER (MySQL: booknest_db)
```

1. **Model:** Represents database entities (`Book.java`, `Favorite.java`) and request transfer objects (`BookRequest.java`, `ApiResponse.java`).
2. **View:** Responsive web interface (`index.html`, `style.css`, `script.js`).
3. **Controller:** REST API endpoints handling incoming HTTP requests, input validation, and HTTP response codes.
4. **Service:** Encapsulates business validation rules, category checks, and query coordination.
5. **Repository:** Executes direct, parameterized SQL statements via Spring `JdbcTemplate`.

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
│   ├── css/
│   │   └── style.css
│   ├── js/
│   │   └── script.js
│   └── assets/
│       └── images/
│
├── backend/
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/
│           │       └── booknest/
│           │           ├── BookNestApplication.java
│           │           ├── controller/
│           │           │   ├── BookController.java
│           │           │   └── FavoriteController.java
│           │           ├── service/
│           │           │   ├── BookService.java
│           │           │   └── FavoriteService.java
│           │           ├── repository/
│           │           │   ├── BookRepository.java
│           │           │   └── FavoriteRepository.java
│           │           ├── model/
│           │           │   ├── Book.java
│           │           │   └── Favorite.java
│           │           ├── dto/
│           │           │   ├── ApiResponse.java
│           │           │   └── BookRequest.java
│           │           └── config/
│           │               └── CorsConfig.java
│           │
│           └── resources/
│               ├── application.properties
│               └── schema.sql
│
├── database/
│   └── booknest.sql
│
├── index.html
├── privacy-policy.html
├── terms-and-conditions.html
├── 404.html
├── favicon.svg
├── css/style.css
├── js/script.js
└── README.md
```

---

## 6. MySQL Database Setup

The application uses the database named `booknest_db`.

### Step 1: Import Schema and Seed Data
Open MySQL Workbench, MySQL Command Line Client, or your terminal, and execute:

```sql
SOURCE database/booknest.sql;
```

Alternatively, open `database/booknest.sql` and run its contents directly in your MySQL client.

### Step 2: Database Tables Overview
- **`books` Table:**
  - `id`: INT AUTO_INCREMENT PRIMARY KEY
  - `title`: VARCHAR(255) NOT NULL
  - `author`: VARCHAR(255) NOT NULL
  - `isbn`: VARCHAR(50)
  - `category`: VARCHAR(100)
  - `price`: DECIMAL(10, 2)
  - `availability`: BOOLEAN DEFAULT TRUE
  - `description`: TEXT
  - `rating`: DECIMAL(2, 1)
  - `created_at`: TIMESTAMP DEFAULT CURRENT_TIMESTAMP

- **`favorites` Table:**
  - `id`: INT AUTO_INCREMENT PRIMARY KEY
  - `book_id`: INT NOT NULL (Foreign Key referencing `books.id` ON DELETE CASCADE)
  - `created_at`: TIMESTAMP DEFAULT CURRENT_TIMESTAMP

### Step 3: Seed Books Included
1. **Java Programming** | Author: Herbert Schildt | Category: Programming | Price: Rs. 450 | Rating: 4.8/5 | Availability: Available
2. **Python Crash Course** | Author: Eric Matthes | Category: Programming | Price: Rs. 520 | Rating: 4.7/5 | Availability: Available
3. **Database System Concepts** | Author: Abraham Silberschatz | Category: Database | Price: Rs. 680 | Rating: 4.6/5 | Availability: Available
4. **HTML and CSS** | Author: Jon Duckett | Category: Web Development | Price: Rs. 590 | Rating: 4.7/5 | Availability: Available
5. **Artificial Intelligence** | Author: Stuart Russell | Category: AI | Price: Rs. 750 | Rating: 4.8/5 | Availability: Not Available
6. **The Alchemist** | Author: Paulo Coelho | Category: Fiction | Price: Rs. 299 | Rating: 4.5/5 | Availability: Available

---

## 7. Configuring DB_PASSWORD

In accordance with security requirements, database credentials are not hardcoded. The application reads the MySQL password using the `DB_PASSWORD` environment variable.

In `backend/src/main/resources/application.properties`:
```properties
server.port=8080
spring.datasource.url=jdbc:mysql://localhost:3306/booknest_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=${DB_PASSWORD:}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### Setting the Environment Variable:

**On Windows (PowerShell):**
```powershell
$env:DB_PASSWORD="your_mysql_password"
```

**On Windows (Command Prompt):**
```cmd
set DB_PASSWORD=your_mysql_password
```

**On Linux / macOS:**
```bash
export DB_PASSWORD=your_mysql_password
```

---

## 8. How to Build and Run the Backend

Navigate to the `backend` folder:

```bash
cd backend
```

### Build with Maven:
```bash
mvn clean install
```

### Run the Spring Boot Application:
```bash
mvn spring-boot:run
```

Alternatively, you can run the generated jar file directly:
```bash
java -jar target/booknest-backend-1.0.0.jar
```

The REST API server will start on:
```
http://localhost:8080
```

---

## 9. How to Run the Frontend

The frontend is served from the project root or the `frontend/` folder.

### Option 1: VS Code Live Server
1. Open the `booknest` directory in VS Code.
2. Right click `index.html` (or `frontend/index.html`) and select **"Open with Live Server"**.
3. The site opens at `http://127.0.0.1:5500`.

### Option 2: Python HTTP Server
Run a local static server from the project directory:
```bash
python -m http.server 5500
```
Open `http://localhost:5500` in any web browser.

---

## 10. REST API Endpoints

### Books API (`/api/books`)
| HTTP Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/books` | Get all books (supports `?sort=featured`, `rating_desc`, `price_asc`, `price_desc`, `name_asc` and `?category=...`) |
| `GET` | `/api/books/{id}` | Get single book details by ID |
| `GET` | `/api/books/category/{category}` | Get books in a specific category |
| `POST` | `/api/books` | Register a new book record in MySQL |
| `PUT` | `/api/books/{id}` | Update existing book record |
| `DELETE` | `/api/books/{id}` | Delete book record by ID |

### Favorites API (`/api/favorites`)
| HTTP Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/favorites` | Get all favorite books from MySQL |
| `GET` | `/api/favorites/ids` | Get list of favorited book IDs |
| `GET` | `/api/favorites/{bookId}/status` | Check if a book is favorited |
| `POST` | `/api/favorites/{bookId}` | Add book to favorites in MySQL |
| `DELETE` | `/api/favorites/{bookId}` | Remove book from favorites |

---

## 11. How the Frontend Communicates with the Backend

1. When the page loads, JavaScript invokes `GET /api/books` via the `fetch()` API.
2. Spring Boot queries the `books` table in MySQL and returns a JSON response:
   ```json
   {
     "success": true,
     "message": "Books retrieved successfully",
     "data": [ ... ]
   }
   ```
3. JavaScript parses the response and dynamically renders the book cards into the DOM.
4. If the backend is offline or MySQL is unreachable, a clear notification is displayed:
   > *"Unable to load books. Please make sure the backend server is running on port 8080 and MySQL is active."*
5. No fake data substitution occurs.

---

## 12. How Spring JDBC and JdbcTemplate Work

Unlike JPA or Hibernate which generate opaque SQL behind object-relational mapping proxies, Spring's `JdbcTemplate`:
- Gives direct control over precise SQL execution.
- Employs parameterized queries (`?` placeholders) which completely prevent SQL injection attacks.
- Maps database result rows directly to Java models using `RowMapper<Book>`.
- Generates auto-incremented primary keys using `GeneratedKeyHolder`.

Example query from `BookRepository.java`:
```java
String sql = "SELECT id, title, author, isbn, category, price, availability, description, rating, created_at FROM books WHERE id = ?";
Book book = jdbcTemplate.queryForObject(sql, bookRowMapper, id);
```

---

## 13. Troubleshooting

- **Error: Access denied for user 'root'@'localhost':**
  Ensure you set the `DB_PASSWORD` environment variable before running the backend:
  `$env:DB_PASSWORD="your_password"`
- **Backend Port Conflict (Port 8080 already in use):**
  Change `server.port=8081` in `application.properties` and update `const API_BASE_URL = "http://localhost:8081/api";` in `js/script.js`.
- **CORS Issues:**
  The backend is configured in `CorsConfig.java` to allow local ports (`http://127.0.0.1:5500`, `http://localhost:5500`, and `http://localhost:*`).
- **Database Does Not Exist:**
  Run `SOURCE database/booknest.sql;` in your MySQL console to create `booknest_db` and initialize tables.
