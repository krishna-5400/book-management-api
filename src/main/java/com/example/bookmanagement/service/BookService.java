package com.example.bookmanagement.service;

import com.example.bookmanagement.dto.BookDTO;
import com.example.bookmanagement.dto.PartialDTO;

import java.util.List;

public interface BookService {
    String createBook(BookDTO bookDTO);
    List<BookDTO> searchBooks(String title, String author, Boolean available);
    List<String> getAvailableBookTitles();
    String updateBook(String id, PartialDTO updates);
    String deleteBook(String id);
}