package com.booknest.repository;

import com.booknest.model.Book;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Book entity using Spring JdbcTemplate.
 * Does not use JPA or Hibernate.
 */
@Repository
public class BookRepository {

    private final JdbcTemplate jdbcTemplate;

    public BookRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Book> bookRowMapper = (rs, rowNum) -> {
        Book book = new Book();
        book.setId(rs.getLong("id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setIsbn(rs.getString("isbn"));
        book.setCategory(rs.getString("category"));
        book.setPrice(rs.getBigDecimal("price"));
        book.setAvailability(rs.getBoolean("availability"));
        book.setDescription(rs.getString("description"));
        book.setRating(rs.getDouble("rating"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            book.setCreatedAt(ts.toLocalDateTime());
        }
        return book;
    };

    /**
     * Retrieve all books with optional category filtering and whitelisted sorting.
     */
    public List<Book> findAll(String sort, String category) {
        StringBuilder sql = new StringBuilder("SELECT id, title, author, isbn, category, price, availability, description, rating, created_at FROM books");
        List<Object> params = new ArrayList<>();

        if (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("All")) {
            sql.append(" WHERE LOWER(category) = LOWER(?)");
            params.add(category.trim());
        }

        // Whitelist sorting parameters safely to prevent SQL injection
        String orderByClause = " ORDER BY id ASC";
        if (sort != null) {
            switch (sort.toLowerCase().trim()) {
                case "rating_desc":
                case "rating-desc":
                    orderByClause = " ORDER BY rating DESC, title ASC";
                    break;
                case "price_asc":
                case "price-asc":
                    orderByClause = " ORDER BY price ASC";
                    break;
                case "price_desc":
                case "price-desc":
                    orderByClause = " ORDER BY price DESC";
                    break;
                case "name_asc":
                case "title_asc":
                case "name-asc":
                case "title-asc":
                    orderByClause = " ORDER BY title ASC";
                    break;
                case "featured":
                default:
                    orderByClause = " ORDER BY id ASC";
                    break;
            }
        }
        sql.append(orderByClause);

        return jdbcTemplate.query(sql.toString(), bookRowMapper, params.toArray());
    }

    /**
     * Find book by primary key.
     */
    public Optional<Book> findById(Long id) {
        String sql = "SELECT id, title, author, isbn, category, price, availability, description, rating, created_at FROM books WHERE id = ?";
        try {
            Book book = jdbcTemplate.queryForObject(sql, bookRowMapper, id);
            return Optional.ofNullable(book);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Find books by category.
     */
    public List<Book> findByCategory(String category) {
        String sql = "SELECT id, title, author, isbn, category, price, availability, description, rating, created_at FROM books WHERE LOWER(category) = LOWER(?) ORDER BY id ASC";
        return jdbcTemplate.query(sql, bookRowMapper, category);
    }

    /**
     * Insert a new book using parameterized PreparedStatement and KeyHolder.
     */
    public Book save(Book book) {
        String sql = "INSERT INTO books (title, author, isbn, category, price, availability, description, rating, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        LocalDateTime now = LocalDateTime.now();
        book.setCreatedAt(now);

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getIsbn());
            ps.setString(4, book.getCategory());
            ps.setBigDecimal(5, book.getPrice());
            ps.setBoolean(6, book.getAvailability() != null ? book.getAvailability() : true);
            ps.setString(7, book.getDescription());
            ps.setDouble(8, book.getRating() != null ? book.getRating() : 4.5);
            ps.setTimestamp(9, Timestamp.valueOf(now));
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            book.setId(keyHolder.getKey().longValue());
        }
        return book;
    }

    /**
     * Update an existing book record.
     */
    public int update(Long id, Book book) {
        String sql = "UPDATE books SET title = ?, author = ?, isbn = ?, category = ?, price = ?, availability = ?, description = ?, rating = ? WHERE id = ?";
        return jdbcTemplate.update(sql,
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getCategory(),
                book.getPrice(),
                book.getAvailability() != null ? book.getAvailability() : true,
                book.getDescription(),
                book.getRating() != null ? book.getRating() : 4.5,
                id
        );
    }

    /**
     * Delete a book record by primary key.
     */
    public int deleteById(Long id) {
        String sql = "DELETE FROM books WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    /**
     * Check if a book exists by primary key.
     */
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM books WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    /**
     * Count total books in the database.
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM books";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }
}
