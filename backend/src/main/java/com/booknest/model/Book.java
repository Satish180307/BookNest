package com.booknest.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Model class representing a Book entity.
 * Uses plain Java structure without JPA or Hibernate annotations.
 */
public class Book {

    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String category;
    private BigDecimal price;
    private Boolean availability;
    private String description;
    private Double rating;
    private LocalDateTime createdAt;

    public Book() {
    }

    public Book(Long id, String title, String author, String isbn, String category,
                BigDecimal price, Boolean availability, String description,
                Double rating, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.price = price;
        this.availability = availability;
        this.description = description;
        this.rating = rating;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Boolean getAvailability() {
        return availability;
    }

    public void setAvailability(Boolean availability) {
        this.availability = availability;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
