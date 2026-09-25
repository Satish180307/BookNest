package com.booknest.model;

import java.time.LocalDateTime;

/**
 * Model class representing a Favorite entity.
 * Uses plain Java structure without JPA or Hibernate annotations.
 */
public class Favorite {

    private Long id;
    private Long bookId;
    private LocalDateTime createdAt;
    private Book book;

    public Favorite() {
    }

    public Favorite(Long id, Long bookId, LocalDateTime createdAt) {
        this.id = id;
        this.bookId = bookId;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
