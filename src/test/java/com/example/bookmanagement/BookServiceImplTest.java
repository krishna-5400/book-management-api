package com.example.bookmanagement;


import com.example.bookmanagement.dto.BookDTO;
import com.example.bookmanagement.dto.PartialDTO;
import com.example.bookmanagement.model.Book;
import com.example.bookmanagement.repository.BookRepository;
import com.example.bookmanagement.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {
    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;

    private String title;
    private String author;
    private String description;

    @BeforeEach
    void setUp() {
        title = "Test Title";
        author = "Test Author";
        description = "Test Description";
    }
    @Test
    void testCreateBook_shouldSaveSuccessfully() {
        BookDTO dto = new BookDTO(title, author, description, true);
        Book savedBook = new Book(dto.getTitle(), dto.getAuthor(), dto.getDescription(), dto.isAvailable());

        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        String result = bookService.createBook(dto);
        assertTrue(result.contains("Book created with id: "));
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testSearchBooks_whenTitleAndAvailable_shouldCallTitleAvailableRepoMethod(){
        List<Book> mockBooks = List.of(new Book(title, author, description,true));
        when(bookRepository.findByTitleContainingIgnoreCaseAndAvailable(title, true))
                .thenReturn(mockBooks);

        List<BookDTO> result = bookService.searchBooks(title, null, true);
        assertEquals(1, result.size());
        assertEquals(title, result.get(0).getTitle());
        verify(bookRepository).findByTitleContainingIgnoreCaseAndAvailable(title, true);
    }
    @Test
    void testSearchBooks_whenTitleAndAuthorAndTitleAvailable_shouldCallTitleAndAuthorAndAvailable() {
        List<Book> mockBooks = List.of(new Book(title, author, description,true));
        when(bookRepository.findByTitleAndAuthorContainingIgnoreCaseAndAvailable(title, author, true))
                .thenReturn(mockBooks);

        List<BookDTO> result = bookService.searchBooks(title, author, true);
        assertEquals(1, result.size());
        assertEquals(title, result.get(0).getTitle());
        assertEquals(author, result.get(0).getAuthor());
        verify(bookRepository).findByTitleAndAuthorContainingIgnoreCaseAndAvailable(title, author, true);
    }
    @Test
    void testSearchBooks_whenAuthorAndAvailable_shouldCallAuthorAndAvailable() {
        List<Book> mockBooks = List.of(new Book(title, author, description,true));
        when(bookRepository.findByAuthorContainingIgnoreCaseAndAvailable(author, true))
                .thenReturn(mockBooks);

        List<BookDTO> result = bookService.searchBooks(null ,author, true);
        assertEquals(1, result.size());
        assertEquals(author, result.get(0).getAuthor());
        verify(bookRepository).findByAuthorContainingIgnoreCaseAndAvailable(author, true);
    }
    @Test
    void testSearchBooks_whenTitle_shouldCallTitleRepoMethod() {
        List<Book> mockBooks = List.of(new Book(title, author, description, true));
        when(bookRepository.findByTitleContainingIgnoreCase(title))
                .thenReturn(mockBooks);

        List<BookDTO> result = bookService.searchBooks(title, null, null);
        assertEquals(1, result.size());
        assertEquals(title, result.get(0).getTitle());
        verify(bookRepository).findByTitleContainingIgnoreCase(title);
    }
    @Test
    void testSearchBooks_whenAuthor_shouldCallAuthorAvailableRepoMethod() {
        List<Book> mockBooks = List.of(new Book(title, author, description, true));
        when(bookRepository.findByAuthorContainingIgnoreCase(author))
                .thenReturn(mockBooks);

        List<BookDTO> result = bookService.searchBooks(null, author, null);
        assertEquals(1, result.size());
        assertEquals(author, result.get(0).getAuthor());
        verify(bookRepository).findByAuthorContainingIgnoreCase(author);
    }
    @Test
    void testSearchBooks_shouldCallfindAll() {
        List<Book> mockBooks = List.of(new Book(title, author, description, true), new Book("t", "a", "d", true));
        when(bookRepository.findAll())
                .thenReturn(mockBooks);

        List<BookDTO> result = bookService.searchBooks(null, null, null);
        assertEquals(2, result.size());
        assertEquals(title, result.get(0).getTitle());
        assertEquals(author,result.get(0).getAuthor());
        assertEquals("t", result.get(1).getTitle());
        assertEquals("a", result.get(1).getAuthor());
        verify(bookRepository).findAll();
    }
    @Test
    void updateBook_whenUpdateDataIsNull_shouldThrowRuntimeException() {
        String id = "mock-id";
        Book existingBook = new Book(title, author, description, true);

        when(bookRepository.findById(id)).thenReturn(Optional.of(existingBook));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> bookService.updateBook(id, null)
        );

        assertEquals("No update data provided", ex.getMessage());
        verify(bookRepository).findById(id);
        verify(bookRepository, never()).save(any());
    }
    @Test
    void testUpdateBook_whenValidPartialDTO_shouldUpdateAndSaveBook() {
        String id = "mock-id";
        Book existingBook = new Book(title, author, description, true);
        PartialDTO updates = new PartialDTO();
        updates.setTitle("New Title");

        when(bookRepository.findById(id)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenReturn(existingBook);

        String result = bookService.updateBook(id, updates);

        assertTrue(result.contains("Book details updated"));
        assertEquals("New Title", existingBook.getTitle());
        verify(bookRepository).save(existingBook);
    }
    @Test
    void testUpdateBook_whenTitleIsEmpty_shouldThrowIllegalArgumentException() {
        String id = "mock-id";
        Book existingBook = new Book(title, author, description, true);
        PartialDTO updates = new PartialDTO();
        updates.setTitle(" ");

        when(bookRepository.findById(id)).thenReturn(Optional.of(existingBook));

        IllegalArgumentException ex = assertThrows(
          IllegalArgumentException.class,
                () -> bookService.updateBook(id, updates)
        );

        assertEquals("Title cannot be blank", ex.getMessage());
        verify(bookRepository, never()).save(any());
    }
    @Test
    void testDeleteBook_whenBookNotFound_shouldThrowRuntimeException() {
        String id = "missing-id";

        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> bookService.deleteBook(id)
        );

        assertEquals("Book not found with id: "+ id, ex.getMessage());
        verify(bookRepository, never()).delete((any()));
    }
    @Test
    void testDeleteBook_whenValidId_shouldDeleteBook() {
        String id = "mock-id";
        Book existingBook = new Book(title, author, description, true);

        when(bookRepository.findById(id)).thenReturn(Optional.of(existingBook));

        String result = bookService.deleteBook(id);

        assertTrue(result.contains("Book with id " + id + " deleted"));
        verify(bookRepository).delete(existingBook);
    }
}
