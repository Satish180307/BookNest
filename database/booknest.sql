-- BookNest Database Schema
-- Database: booknest_db

CREATE DATABASE IF NOT EXISTS booknest_db;
USE booknest_db;

-- 1. Books Table
CREATE TABLE IF NOT EXISTS books (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(50),
    category VARCHAR(100),
    price DECIMAL(10, 2),
    availability BOOLEAN DEFAULT TRUE,
    description TEXT,
    rating DECIMAL(2, 1),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Favorites Table
CREATE TABLE IF NOT EXISTS favorites (
    id INT PRIMARY KEY AUTO_INCREMENT,
    book_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_favorites_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    CONSTRAINT uk_book_favorite UNIQUE (book_id)
);

-- Indexes for efficient queries
CREATE INDEX idx_books_category ON books(category);
CREATE INDEX idx_favorites_book ON favorites(book_id);

-- Seed Initial Book Catalog
INSERT INTO books (id, title, author, isbn, category, price, availability, description, rating) VALUES
(1, 'Java Programming', 'Herbert Schildt', '978-1260440232', 'Programming', 450.00, true, 'Comprehensive guide covering Java syntax, object-oriented concepts, and core libraries.', 4.8),
(2, 'Python Crash Course', 'Eric Matthes', '978-1593279288', 'Programming', 520.00, true, 'Fast-paced, thorough introduction to programming with Python, covering basic concepts and practical project workflows.', 4.7),
(3, 'Database System Concepts', 'Abraham Silberschatz', '978-0073523323', 'Database', 680.00, true, 'Foundational textbook covering relational models, SQL query design, indexing, and transaction management.', 4.6),
(4, 'HTML and CSS', 'Jon Duckett', '978-1118008188', 'Web Development', 590.00, true, 'Visual introduction to markup and styling structure for designing standard, responsive web pages.', 4.7),
(5, 'Artificial Intelligence', 'Stuart Russell', '978-0134610993', 'AI', 750.00, false, 'Authoritative reference on intelligent agents, problem-solving, knowledge representation, and machine learning principles.', 4.8),
(6, 'The Alchemist', 'Paulo Coelho', '978-0062315007', 'Fiction', 299.00, true, 'Philosophical novel about a young shepherd journeying to discover his personal legend and pursue life purpose.', 4.5)
ON DUPLICATE KEY UPDATE title = VALUES(title), author = VALUES(author), category = VALUES(category), price = VALUES(price), availability = VALUES(availability), rating = VALUES(rating);

-- Seed Initial Favorites
INSERT INTO favorites (book_id) VALUES
(1),
(3)
ON DUPLICATE KEY UPDATE book_id = VALUES(book_id);
