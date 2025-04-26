package com.example.bookmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BookDTO {
    @NotBlank
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    @NotBlank
    @Size(max = 50, message = "Author cannot exceed 50 characters")
    private String author;

    @Size(max = 300, message = "Description cannot exceed 300 characters")
    private  String description;
    private boolean available;

    public BookDTO() {}

    public BookDTO (String title,  String author, String description, boolean available){
        this.title = title;
        this.author = author;
        this.description = description;
        this.available = available;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAvailable() {
        return available;
    }
}
