package com.example.bookmanagement.repository;

import com.example.bookmanagement.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BookRepository extends MongoRepository<Book, String>{
    List<Book> findByTitleContainingIgnoreCaseAndAvailable(String title, Boolean available);

    List<Book> findByAuthorContainingIgnoreCaseAndAvailable(String author, Boolean available);

    List<Book> findByTitleAndAuthorContainingIgnoreCaseAndAvailable(String title, String author, Boolean available);

    List<Book> findByAvailable(Boolean available);

    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByAuthorContainingIgnoreCase(String author);
}
