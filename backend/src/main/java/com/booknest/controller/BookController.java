package com.booknest.controller;

import com.booknest.dto.ApiResponse;
import com.booknest.dto.BookRequest;
import com.booknest.model.Book;
import com.booknest.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Book operations.
 * Endpoints under /api/books.
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * GET /api/books
     * Supports optional sorting (?sort=rating_desc, price_asc, price_desc, name_asc, featured)
     * and category filtering (?category=Programming).
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Book>>> getAllBooks(
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String category) {
        List<Book> books = bookService.getAllBooks(sort, category);
        return ResponseEntity.ok(ApiResponse.success("Books retrieved successfully", books));
    }

    /**
     * GET /api/books/{id}
     * Retrieve single book details.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Book>> getBookById(@PathVariable Long id) {
        Optional<Book> bookOpt = bookService.getBookById(id);
        if (bookOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Book not found with ID: " + id));
        }
        return ResponseEntity.ok(ApiResponse.success("Book retrieved successfully", bookOpt.get()));
    }

    /**
     * GET /api/books/category/{category}
     * Retrieve books belonging to a specific category.
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<Book>>> getBooksByCategory(@PathVariable String category) {
        List<Book> books = bookService.getBooksByCategory(category);
        return ResponseEntity.ok(ApiResponse.success("Books retrieved for category: " + category, books));
    }

    /**
     * POST /api/books
     * Register a new book record.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Book>> createBook(@RequestBody BookRequest request) {
        try {
            Book created = bookService.createBook(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Book registered successfully", created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to register book"));
        }
    }

    /**
     * PUT /api/books/{id}
     * Update an existing book record.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Book>> updateBook(@PathVariable Long id, @RequestBody BookRequest request) {
        try {
            Optional<Book> updated = bookService.updateBook(id, request);
            if (updated.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Book not found with ID: " + id));
            }
            return ResponseEntity.ok(ApiResponse.success("Book updated successfully", updated.get()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update book"));
        }
    }

    /**
     * DELETE /api/books/{id}
     * Delete an existing book.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long id) {
        boolean deleted = bookService.deleteBook(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Book not found with ID: " + id));
        }
        return ResponseEntity.ok(ApiResponse.success("Book deleted successfully", null));
    }
}
