package com.booknest.service;

import com.booknest.dto.BookRequest;
import com.booknest.model.Book;
import com.booknest.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service layer handling business logic and validation for Books.
 */
@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Retrieve books with optional sorting and category filter.
     */
    public List<Book> getAllBooks(String sort, String category) {
        return bookRepository.findAll(sort, category);
    }

    /**
     * Retrieve a specific book by ID.
     */
    public Optional<Book> getBookById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return bookRepository.findById(id);
    }

    /**
     * Retrieve books by category.
     */
    public List<Book> getBooksByCategory(String category) {
        if (category == null || category.trim().isEmpty() || category.equalsIgnoreCase("All")) {
            return bookRepository.findAll(null, null);
        }
        return bookRepository.findByCategory(category.trim());
    }

    /**
     * Create a new book after validating required business fields.
     */
    public Book createBook(BookRequest request) {
        validateBookRequest(request);

        Book book = new Book();
        book.setTitle(request.getTitle().trim());
        book.setAuthor(request.getAuthor().trim());
        book.setIsbn(request.getIsbn() != null ? request.getIsbn().trim() : null);
        book.setCategory(request.getCategory().trim());
        book.setPrice(request.getPrice());
        book.setAvailability(request.getAvailability() != null ? request.getAvailability() : true);
        book.setDescription(request.getDescription().trim());
        book.setRating(request.getRating() != null ? request.getRating() : 4.5);

        return bookRepository.save(book);
    }

    /**
     * Update an existing book record.
     */
    public Optional<Book> updateBook(Long id, BookRequest request) {
        if (id == null || !bookRepository.existsById(id)) {
            return Optional.empty();
        }

        validateBookRequest(request);

        Book book = new Book();
        book.setId(id);
        book.setTitle(request.getTitle().trim());
        book.setAuthor(request.getAuthor().trim());
        book.setIsbn(request.getIsbn() != null ? request.getIsbn().trim() : null);
        book.setCategory(request.getCategory().trim());
        book.setPrice(request.getPrice());
        book.setAvailability(request.getAvailability() != null ? request.getAvailability() : true);
        book.setDescription(request.getDescription().trim());
        book.setRating(request.getRating() != null ? request.getRating() : 4.5);

        bookRepository.update(id, book);
        return bookRepository.findById(id);
    }

    /**
     * Delete a book by ID.
     */
    public boolean deleteBook(Long id) {
        if (id == null || !bookRepository.existsById(id)) {
            return false;
        }
        return bookRepository.deleteById(id) > 0;
    }

    /**
     * Business validation logic for incoming book data.
     */
    private void validateBookRequest(BookRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Book request cannot be null");
        }
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Book title is required");
        }
        if (request.getAuthor() == null || request.getAuthor().trim().isEmpty()) {
            throw new IllegalArgumentException("Author name is required");
        }
        if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("Category is required");
        }
        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valid positive price is required");
        }
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Description is required");
        }
    }
}
