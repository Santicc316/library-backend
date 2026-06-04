package com.example.library.controller;

import com.example.library.model.Book;
import com.example.library.repository.BookRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository repository;

    public BookController(BookRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Book> getAll(@RequestParam(required = false) String genre,
                              @RequestParam(required = false) Boolean available) {
        if (genre != null) return repository.findByGenre(genre);
        if (available != null) return repository.findByAvailable(available);
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Book create(@Valid @RequestBody Book book) {
        return repository.save(book);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> update(@PathVariable Long id, @Valid @RequestBody Book book) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setTitle(book.getTitle());
                    existing.setAuthor(book.getAuthor());
                    existing.setIsbn(book.getIsbn());
                    existing.setPublishedYear(book.getPublishedYear());
                    existing.setGenre(book.getGenre());
                    existing.setDescription(book.getDescription());
                    existing.setAvailable(book.getAvailable());
                    return ResponseEntity.ok(repository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<Book> toggleAvailability(@PathVariable Long id) {
        return repository.findById(id)
                .map(book -> {
                    book.setAvailable(!book.getAvailable());
                    return ResponseEntity.ok(repository.save(book));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
