package org.example.subwp.controller;

import org.example.subwp.model.Author;
import org.example.subwp.model.Book;
import org.example.subwp.model.Category;
import org.example.subwp.service.AuthorService;
import org.example.subwp.service.BookService;
import org.example.subwp.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "books/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.getAllAuthors());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "books/create";
    }

    @PostMapping
    public String createBook(@Valid @ModelAttribute("book") Book book, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("authors", authorService.getAllAuthors());
            model.addAttribute("categories", categoryService.getAllCategories());
            return "books/create";
        }

        Long authorId = book.getAuthor().getId();
        Author author = authorService.getAuthorById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid author Id:" + authorId));
        book.setAuthor(author);

        Long categoryId = book.getCategory().getId();
        Category category = categoryService.getCategoryById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + categoryId));
        book.setCategory(category);

        bookService.saveBook(book);
        return "redirect:/books";
    }


    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + id));
        model.addAttribute("book", book);
        model.addAttribute("authors", authorService.getAllAuthors());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "books/edit";
    }

    @PostMapping("/{id}")
    public String updateBook(@PathVariable Long id, @Valid @ModelAttribute("book") Book book,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            book.setId(id);
            model.addAttribute("authors", authorService.getAllAuthors());
            model.addAttribute("categories", categoryService.getAllCategories());
            return "books/edit";
        }
        bookService.saveBook(book);
        return "redirect:/books";
    }

    @GetMapping("/{id}/delete")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteBookById(id);
        return "redirect:/books";
    }
}
