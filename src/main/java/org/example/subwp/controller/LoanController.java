package org.example.subwp.controller;

import org.example.subwp.model.Book;
import org.example.subwp.model.Loan;
import org.example.subwp.model.User;
import org.example.subwp.service.BookService;
import org.example.subwp.service.LoanService;
import org.example.subwp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @GetMapping
    public String listLoans(Model model, Authentication authentication) {
        String currentUsername = authentication.getName();
        User currentUser = userService.getUserByUsername(currentUsername).orElse(null);

        List<Loan> loans;
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        if (isAdmin) {
            loans = loanService.getAllLoans();
        } else if (currentUser != null) {
            loans = loanService.getLoansByUserId(currentUser.getId());
        } else {
            loans = List.of();
        }

        model.addAttribute("loans", loans);
        return "loans/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Loan loan = new Loan();
        loan.setLoanDate(new Date());
        model.addAttribute("loan", loan);
        model.addAttribute("books", bookService.getAvailableBooks());
        model.addAttribute("users", userService.getAllUsers());
        return "loans/create";
    }


    @PostMapping
    public String createLoan(@Valid @ModelAttribute("loan") Loan loan, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("books", bookService.getAvailableBooks());
            return "loans/create";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.getUserByUsername(username).orElse(null);

        if (user == null) {
            model.addAttribute("error", "User not found.");
            return "loans/create";
        }

        loan.setUser(user);
        loan.setLoanDate(new Date());
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