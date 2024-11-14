package org.example.subwp.controller;

import org.example.subwp.model.Author;
import org.example.subwp.model.Book;
import org.example.subwp.model.Category;
import org.example.subwp.service.AuthorService;
import org.example.subwp.service.BookService;
import org.example.subwp.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
    public String listBooks(@RequestParam(value = "title", required = false) String title, Model model) {
        List<Book> books;


        if (title != null && !title.isEmpty()) {
            books = bookService.findBooksByTitle(title);
        } else {
            books = bookService.getAllBooks();
        }

        model.addAttribute("books", books);
        model.addAttribute("searchQuery", title);
        return "books/list";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.getAllAuthors());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "books/create";
    }

    @PostMapping("/new")
    public String createBook(@Valid @ModelAttribute("book") Book book, BindingResult result, Model model) {
        System.out.println("Creating book with ISBN: " + book.getIsbn());

        if (result.hasErrors()) {
            model.addAttribute("authors", authorService.getAllAuthors());
            model.addAttribute("categories", categoryService.getAllCategories());
            return "books/create";
        }


        Book existingBook = bookService.findByIsbn(book.getIsbn());
        if (existingBook != null) {
            result.rejectValue("isbn", "error.book", "ISBN već postoji!");
            model.addAttribute("authors", authorService.getAllAuthors());
            model.addAttribute("categories", categoryService.getAllCategories());
            return "books/create";
        }

        Author author = authorService.getAuthorById(book.getAuthor().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid author Id"));
        book.setAuthor(author);

        Category category = categoryService.getCategoryById(book.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id"));
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
    public String updateBook(@PathVariable Long id,
                             @Valid @ModelAttribute("book") Book book,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("authors", authorService.getAllAuthors());
            model.addAttribute("categories", categoryService.getAllCategories());
            return "books/edit";
        }

        Book existingBook = bookService.findByIsbn(book.getIsbn());
        if (existingBook != null && !existingBook.getId().equals(id)) {
            result.rejectValue("isbn", "error.book", "ISBN već postoji!");
            model.addAttribute("authors", authorService.getAllAuthors());
            model.addAttribute("categories", categoryService.getAllCategories());
            return "books/edit";
        }

        bookService.saveBook(book);
        return "redirect:/books";
    }


    @GetMapping("/{id}/delete")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteBookById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Knjiga je uspješno obrisana.");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ne možete obrisati ovu knjigu jer postoje posuđene kopije povezane s njom.");
        }
        return "redirect:/books";
    }

    @GetMapping("/books/search")
    public String searchBooks(@RequestParam("title") String title, Model model) {
        List<Book> books = bookService.findBooksByTitle(title);
        model.addAttribute("books", books);
        return "books";
    }



}

