package com.booknest.controller;

import com.booknest.dto.ApiResponse;
import com.booknest.model.Book;
import com.booknest.service.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Favorite operations.
 * Endpoints under /api/favorites.
 */
@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    /**
     * GET /api/favorites
     * Retrieve list of all favorite books from MySQL.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Book>>> getFavorites() {
        List<Book> favorites = favoriteService.getFavorites();
        return ResponseEntity.ok(ApiResponse.success("Favorites retrieved successfully", favorites));
    }

    /**
     * GET /api/favorites/ids
     * Retrieve list of favorite book IDs for fast frontend synchronization.
     */
    @GetMapping("/ids")
    public ResponseEntity<ApiResponse<List<Long>>> getFavoriteIds() {
        List<Long> ids = favoriteService.getFavoriteBookIds();
        return ResponseEntity.ok(ApiResponse.success("Favorite IDs retrieved successfully", ids));
    }

    /**
     * GET /api/favorites/{bookId}/status
     * Check whether a specific book is in favorites.
     */
    @GetMapping("/{bookId}/status")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> getFavoriteStatus(@PathVariable Long bookId) {
        boolean status = favoriteService.isFavorite(bookId);
        return ResponseEntity.ok(ApiResponse.success("Favorite status retrieved", Map.of("isFavorite", status)));
    }

    /**
     * POST /api/favorites/{bookId}
     * Add a book to favorites in MySQL.
     */
    @PostMapping("/{bookId}")
    public ResponseEntity<ApiResponse<Void>> addFavorite(@PathVariable Long bookId) {
        try {
            boolean added = favoriteService.addFavorite(bookId);
            if (added) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success("Book added to favorites", null));
            } else {
                return ResponseEntity.ok(ApiResponse.success("Book already in favorites", null));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to add favorite"));
        }
    }

    /**
     * DELETE /api/favorites/{bookId}
     * Remove a book from favorites in MySQL.
     */
    @DeleteMapping("/{bookId}")
    public ResponseEntity<ApiResponse<Void>> removeFavorite(@PathVariable Long bookId) {
        try {
            favoriteService.removeFavorite(bookId);
            return ResponseEntity.ok(ApiResponse.success("Book removed from favorites", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to remove favorite"));
        }
    }
}
