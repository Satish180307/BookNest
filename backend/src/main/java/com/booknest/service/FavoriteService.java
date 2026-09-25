package com.booknest.service;

import com.booknest.model.Book;
import com.booknest.repository.BookRepository;
import com.booknest.repository.FavoriteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer handling favorites logic and coordinating with repositories.
 */
@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final BookRepository bookRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, BookRepository bookRepository) {
        this.favoriteRepository = favoriteRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * Retrieve full list of favorite books from MySQL.
     */
    public List<Book> getFavorites() {
        return favoriteRepository.findAllFavorites();
    }

    /**
     * Retrieve all favorite book IDs.
     */
    public List<Long> getFavoriteBookIds() {
        return favoriteRepository.findAllFavoriteBookIds();
    }

    /**
     * Check if a book is already favorited.
     */
    public boolean isFavorite(Long bookId) {
        if (bookId == null || bookId <= 0) {
            return false;
        }
        return favoriteRepository.isFavorite(bookId);
    }

    /**
     * Add a book to favorites in MySQL. Verifies book exists.
     */
    public boolean addFavorite(Long bookId) {
        if (bookId == null || bookId <= 0) {
            throw new IllegalArgumentException("Invalid book ID");
        }
        if (!bookRepository.existsById(bookId)) {
            throw new IllegalArgumentException("Book does not exist");
        }
        return favoriteRepository.addFavorite(bookId) > 0;
    }

    /**
     * Remove a book from favorites in MySQL.
     */
    public boolean removeFavorite(Long bookId) {
        if (bookId == null || bookId <= 0) {
            return false;
        }
        return favoriteRepository.removeFavorite(bookId) > 0;
    }
}
