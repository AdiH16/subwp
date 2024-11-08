package org.example.subwp.service;

import org.example.subwp.model.Book;
import org.example.subwp.model.Loan;
import org.example.subwp.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookService bookService;

    public Loan saveLoan(Loan loan) {

        Long bookId = loan.getBook().getId();
        Optional<Book> bookOpt = bookService.getBookById(bookId);

        if (bookOpt.isEmpty()) {
            System.out.println("Book not found.");
            return null;
        }

        Book book = bookOpt.get();

        System.out.println("Book found: " + book.getTitle());
        System.out.println("Available copies before loan: " + book.getAvailableCopies());

        Integer availableCopies = book.getAvailableCopies();

        if (availableCopies == null) {
            availableCopies = 0;
        }


        if (availableCopies > 0) {
            book.setAvailableCopies(availableCopies - 1);
            bookService.saveBook(book);
        } else {
            System.out.println("No copies available to rent.");
        }

        return loanRepository.save(loan);
    }

    public List<Loan> getLoansByUserId(Long userId) {
        return loanRepository.findByUserId(userId);
    }

    public void deleteLoanById(Long id) {
        Optional<Loan> loanOpt = loanRepository.findById(id);
        if (loanOpt.isPresent()) {
            Loan loan = loanOpt.get();
            Optional<Book> bookOpt = bookService.getBookById(loan.getBook().getId());

            if (bookOpt.isPresent()) {
                Book book = bookOpt.get();

                book.setAvailableCopies(book.getAvailableCopies() + 1);
                bookService.saveBook(book);
            }

            loanRepository.deleteById(id);
        }
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public Optional<Loan> getLoanById(Long id) {
        return loanRepository.findById(id);
    }
}
