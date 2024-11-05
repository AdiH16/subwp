package org.example.subwp.controller;

import org.example.subwp.model.Loan;
import org.example.subwp.service.BookService;
import org.example.subwp.service.LoanService;
import org.example.subwp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String listLoans(Model model) {
        model.addAttribute("loans", loanService.getAllLoans());
        return "loans/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("loan", new Loan());
        model.addAttribute("books", bookService.getAllBooks());
        model.addAttribute("users", userService.getAllUsers());
        return "loans/create";
    }

    @PostMapping
    public String createLoan(@Valid @ModelAttribute("loan") Loan loan, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("books", bookService.getAllBooks());
            model.addAttribute("users", userService.getAllUsers());
            return "loans/create";
        }
        loanService.saveLoan(loan);
        return "redirect:/loans";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Loan loan = loanService.getLoanById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid loan Id:" + id));
        model.addAttribute("loan", loan);
        model.addAttribute("books", bookService.getAllBooks());
        model.addAttribute("users", userService.getAllUsers());
        return "loans/edit";
    }

    @PostMapping("/{id}")
    public String updateLoan(@PathVariable Long id, @Valid @ModelAttribute("loan") Loan loan,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("books", bookService.getAllBooks());
            model.addAttribute("users", userService.getAllUsers());
            return "loans/edit";
        }
        loan.setId(id);
        loanService.saveLoan(loan);
        return "redirect:/loans";
    }

    @GetMapping("/{id}/delete")
    public String deleteLoan(@PathVariable Long id) {
        loanService.deleteLoanById(id);
        return "redirect:/loans";
    }
}