package org.example.subwp.controller;

import org.example.subwp.model.Author;
import org.example.subwp.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/authors")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @GetMapping
    public String listAuthors(Model model) {
        model.addAttribute("authors", authorService.getAllAuthors());
        return "authors/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("author", new Author());
        return "authors/create";
    }

    @PostMapping
    public String createAuthor(@Valid @ModelAttribute("author") Author author, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "authors/create";
        }
        authorService.saveAuthor(author);
        return "redirect:/authors";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Author author = authorService.getAuthorById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid author Id:" + id));
        model.addAttribute("author", author);
        return "authors/edit";
    }

    @PostMapping("/{id}")
    public String updateAuthor(@PathVariable Long id, @Valid @ModelAttribute("author") Author author,
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            author.setId(id);
            return "authors/edit";
        }
        authorService.saveAuthor(author);
        return "redirect:/authors";
    }

    @GetMapping("/{id}/delete")
    public String deleteAuthor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            authorService.deleteAuthorById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Autor je uspješno obrisan.");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ne možete obrisati ovog autora jer postoje posuđene knjige povezane s njim.");
        }
        return "redirect:/authors";
    }
}
