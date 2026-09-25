package com.booknest.repository;

import com.booknest.model.Book;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

/**
 * Repository for Favorite entity using Spring JdbcTemplate.
 * Directly communicates with MySQL favorites table.
 */
@Repository
public class FavoriteRepository {

    private final JdbcTemplate jdbcTemplate;

    public FavoriteRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Book> favoriteBookRowMapper = (rs, rowNum) -> {
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
     * Retrieve all favorite books by joining favorites and books table.
     */
    public List<Book> findAllFavorites() {
        String sql = "SELECT b.id, b.title, b.author, b.isbn, b.category, b.price, b.availability, b.description, b.rating, b.created_at " +
                     "FROM favorites f " +
                     "INNER JOIN books b ON f.book_id = b.id " +
                     "ORDER BY f.created_at DESC";
        return jdbcTemplate.query(sql, favoriteBookRowMapper);
    }

    /**
     * Retrieve all favorite book IDs.
     */
    public List<Long> findAllFavoriteBookIds() {
        String sql = "SELECT book_id FROM favorites";
        return jdbcTemplate.queryForList(sql, Long.class);
    }

    /**
     * Check whether a specific book is marked as favorite.
     */
    public boolean isFavorite(Long bookId) {
        String sql = "SELECT COUNT(*) FROM favorites WHERE book_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, bookId);
        return count != null && count > 0;
    }

    /**
     * Add a book to favorites (prevents duplicate entries via unique constraint).
     */
    public int addFavorite(Long bookId) {
        String sql = "INSERT INTO favorites (book_id) VALUES (?) " +
                     "ON DUPLICATE KEY UPDATE book_id = VALUES(book_id)";
        return jdbcTemplate.update(sql, bookId);
    }

    /**
     * Remove a book from favorites.
     */
    public int removeFavorite(Long bookId) {
        String sql = "DELETE FROM favorites WHERE book_id = ?";
        return jdbcTemplate.update(sql, bookId);
    }
}
