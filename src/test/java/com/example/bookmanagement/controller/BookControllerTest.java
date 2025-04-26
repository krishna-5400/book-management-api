package com.example.bookmanagement.controller;

import com.example.bookmanagement.dto.BookDTO;
import com.example.bookmanagement.dto.PartialDTO;
import com.example.bookmanagement.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper; // To convert DTO to JSON

    private BookDTO bookDTO;

    @BeforeEach
    void setUp() {
        bookDTO = new BookDTO("Test Title", "Test Author", "Test Desc", true);
    }


    @Test
    void testCreateBook_shouldReturnSuccessMessage() throws Exception {
        when(bookService.createBook(any(BookDTO.class)))
                .thenReturn("Book created with id: mock-id");

        mockMvc.perform(post("/api/books/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Book created with id: mock-id"));
    }

    @Test
    void testCreateBook_whenInvalidInputs_shouldReturnBadRequest() throws Exception {
        List<String> invalidJsonBodies = List.of(
                """
                {
                  "author": "Author Only",
                  "description": "Some desc",
                  "available": true
                }
                """,
                """
                {
                  "title": "Title Only",
                  "description": "Some desc",
                  "available": true
                }
                """,
                """
                {
                  "title": "",
                  "author": "Author",
                  "description": "Some desc",
                  "available": true
                }
                """,
                """
                {
                  "title": "Title",
                  "author": "",
                  "description": "Some desc",
                  "available": true
                }
                """
        );

        for (String invalidJson : invalidJsonBodies) {
            mockMvc.perform(post("/api/books/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").exists());
        }
    }
    @Test
    void testCreateBook_whenTitleIsLong_shouldReturnBadRequest() throws Exception {
        String invalidJson = """
                {
                    "title": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                    "author": "Some Author",
                    "description": "Some description",
                    "available": true
                }
                """;
        mockMvc.perform(post("/api/books/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Title cannot exceed 100 characters")));
    }
    @Test
    void testCreateBook_whenAuthorIsLong_shouldReturnBadRequest() throws Exception {
        String invalidJson = """
                {
                    "title": "Some Title",
                    "author": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                    "description": "Some description",
                    "available": true
                }
                """;
        mockMvc.perform(post("/api/books/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Author cannot exceed 50 characters")));
    }
    @Test
    void testCreateBook_whenDescIsLong_shouldReturnBadRequest() throws Exception {
        String invalidJson = """
                {
                    "title": "Some Title",
                    "author": "Some Author",
                    "description": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                    "available": true
                }
                """;
        mockMvc.perform(post("/api/books/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Description cannot exceed 300 characters")));
    }
    @Test
    void testGetAllBooks_shouldReturnListOfBooks() throws Exception {
        when(bookService.searchBooks(null, null, null)).thenReturn(List.of(bookDTO));

        mockMvc.perform(get("/api/books/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Title"))
                .andExpect(jsonPath("$[0].author").value("Test Author"))
                .andExpect(jsonPath("$[0].description").value("Test Desc"))
                .andExpect(jsonPath("$[0].available").value(true));
    }
    @Test
    void testsearchBooks_shouldReturnBook() throws Exception {
        when(bookService.searchBooks("Test Title", "Test Author", true)).thenReturn(List.of(bookDTO));

        mockMvc.perform(get("/api/books/search")
                        .param("title","Test Title")
                        .param("author", "Test Author")
                        .param("available", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Title"))
                .andExpect(jsonPath("$[0].author").value("Test Author"))
                .andExpect(jsonPath("$[0].description").value("Test Desc"))
                .andExpect(jsonPath("$[0].available").value(true));
    }
    @Test
    void testsearchBooks_whenTitleIsNotNull_shouldReturnBook() throws Exception {
        when(bookService.searchBooks("Test Title", null, null)).thenReturn(List.of(bookDTO));

        mockMvc.perform(get("/api/books/search")
                        .param("title", "Test Title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Title"))
                .andExpect(jsonPath("$[0].author").value("Test Author"))
                .andExpect(jsonPath("$[0].description").value("Test Desc"))
                .andExpect(jsonPath("$[0].available").value(true));
    }
    @Test
    void testsearchBooks_whenAuthorIsNotNull_shouldReturnBook() throws Exception {
        when(bookService.searchBooks(null, "Test Author", null)).thenReturn(List.of(bookDTO));

        mockMvc.perform(get("/api/books/search")
                        .param("author", "Test Author"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Title"))
                .andExpect(jsonPath("$[0].author").value("Test Author"))
                .andExpect(jsonPath("$[0].description").value("Test Desc"))
                .andExpect(jsonPath("$[0].available").value(true));
    }
    @Test
    void testsearchBooks_whenAvailableIsNotNull_shouldReturnBook() throws Exception {
        when(bookService.searchBooks(null, null, true)).thenReturn(List.of(bookDTO));

        mockMvc.perform(get("/api/books/search")
                        .param("available", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Title"))
                .andExpect(jsonPath("$[0].author").value("Test Author"))
                .andExpect(jsonPath("$[0].description").value("Test Desc"))
                .andExpect(jsonPath("$[0].available").value(true));
    }
    @Test
    void testupdateBook_shouldUpdateBook() throws Exception {
        PartialDTO updates = new PartialDTO();
        updates.setTitle("New Test Title");
        when(bookService.updateBook(eq("mock-id"), any(PartialDTO.class)))
                .thenReturn("Book details updated");

        mockMvc.perform(patch("/api/books/{id}", "mock-id")
                    .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(content().string("Book details updated"));

    }
    @Test
    void testupdateBook_withNullPartialDTO_shouldNotUpdate() throws Exception {
        when(bookService.updateBook(eq("mock-id"), any(PartialDTO.class)))
                .thenThrow(new RuntimeException("No update data provided"));

        mockMvc.perform(patch("/api/books/{id}", "mock-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PartialDTO())))
                .andExpect(status().isNotFound());
    }
}