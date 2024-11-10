package org.example.subwp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.subwp.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UniqueISBNValidator implements ConstraintValidator<UniqueISBN, String> {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public void initialize(UniqueISBN constraintAnnotation) {

    }

    @Override
    public boolean isValid(String isbn, ConstraintValidatorContext context) {
        if (isbn == null || isbn.isEmpty()) {
            return true;
        }
        return bookRepository.findByIsbn(isbn) == null;
    }
}
