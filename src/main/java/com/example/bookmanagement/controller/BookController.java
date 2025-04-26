package com.example.bookmanagement.controller;

import com.example.bookmanagement.dto.BookDTO;
import com.example.bookmanagement.dto.PartialDTO;
import com.example.bookmanagement.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/create")
    @Operation(summary = "Enter a new book",
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Book data to be created",
            content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = BookDTO.class),
                        examples = @ExampleObject(
                                name = "Sample Book",
                                summary = "A sample book object",
                                value = """
                            {
                              "title": "Clean Code",
                              "author": "Robert C. Martin",
                              "description": "A Handbook of Agile Software Craftsmanship",
                              "available": true
                            }
                            """
                            )
                    )
            )
    )
    public String createBook(@Valid @RequestBody BookDTO bookDTO){
        return bookService.createBook(bookDTO);
    }
    @GetMapping("/search")
    @Operation(summary = "Get information of Books")
    public List<BookDTO> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Boolean available
        ){
        return bookService.searchBooks(title, author, available);
    }
    @GetMapping("/check")
    @Operation(summary = "Check names of available books")
    public List<String> getAvailableBookTitles(){
        return bookService.getAvailableBookTitles();
    }
    @PatchMapping("/{id}")
    @Operation(summary = "Update details of the book")
    public String updateBook(@PathVariable String id, @RequestBody PartialDTO updates) {
        return bookService.updateBook(id, updates);
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book",
    responses = {
        @ApiResponse(responseCode = "200", description = "Book deleted"),
        @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public String deleteBook(@PathVariable String id){
        return bookService.deleteBook(id);
    }
}
