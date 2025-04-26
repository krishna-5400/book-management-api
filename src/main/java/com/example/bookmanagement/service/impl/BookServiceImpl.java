package com.example.bookmanagement.service.impl;

import com.example.bookmanagement.dto.BookDTO;
import com.example.bookmanagement.dto.PartialDTO;
import com.example.bookmanagement.model.Book;
import com.example.bookmanagement.repository.BookRepository;
import com.example.bookmanagement.service.BookService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public String createBook(@Valid @RequestBody BookDTO bookDTO){
        Book newBook = new Book();
        newBook.setTitle(bookDTO.getTitle());
        newBook.setAuthor(bookDTO.getAuthor());
        newBook.setDescription(bookDTO.getDescription());
        newBook.setAvailable(bookDTO.isAvailable());

        bookRepository.save(newBook);
        return "Book created with id: " + newBook.getId();
    }

    @Override
    public List<BookDTO> searchBooks(String title, String author, Boolean available) {
        List<Book> books;

        if (title != null && author != null && available != null) {
            books = bookRepository.findByTitleAndAuthorContainingIgnoreCaseAndAvailable(title, author, available);
        } else if (title != null && available != null) {
            books = bookRepository.findByTitleContainingIgnoreCaseAndAvailable(title, available);
        } else if (author != null && available != null) {
            books = bookRepository.findByAuthorContainingIgnoreCaseAndAvailable(author, available);
        } else if (title != null) {
            books = bookRepository.findByTitleContainingIgnoreCase(title);
        } else if (author != null) {
            books = bookRepository.findByAuthorContainingIgnoreCase(author);
        } else if (available != null) {
            books = bookRepository.findByAvailable(available);
        } else {
            books = bookRepository.findAll();
        }

        return books.stream()
                .map(book -> new BookDTO(
                        book.getTitle(),
                        book.getAuthor(),
                        book.getDescription(),
                        book.isAvailable()
                )).toList();
    }

    @Override
    public List<String> getAvailableBookTitles() {
        return bookRepository.findByAvailable(true).stream()
                .map(Book::getTitle)
                .toList();
    }

    @Override
    public String updateBook(String id, PartialDTO updates) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        if (updates == null) {
            throw new RuntimeException("No update data provided");
        }

        if (updates.getAvailable() != null) {
            book.setAvailable(updates.getAvailable());
        }

        if (updates.getTitle() != null) {
            if (updates.getTitle().isBlank()) {
                throw new IllegalArgumentException("Title cannot be blank");
            }
            book.setTitle(updates.getTitle());
        }

        if (updates.getDescription() != null) {
            book.setDescription(updates.getDescription());
        }

        if (updates.getAuthor() != null) {
            book.setAuthor(updates.getAuthor());
        }

        bookRepository.save(book);
        return "Book details updated";
    }

    @Override
    public String deleteBook(String id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        bookRepository.delete(book);
        return "Book with id " + id + " deleted";
    }

}
