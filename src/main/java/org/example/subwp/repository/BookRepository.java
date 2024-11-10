package org.example.subwp.repository;

import org.example.subwp.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Book findByIsbn(String isbn);
    List<Book> findByAvailableCopiesGreaterThan(int availableCopies);
    List<Book> findByTitleContainingIgnoreCase(String title);
}
