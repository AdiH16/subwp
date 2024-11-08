package org.example.subwp.controller;

import org.example.subwp.model.User;
import org.example.subwp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "users/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "users/register";
        }


        if (userService.usernameExists(user.getUsername())) {
            result.rejectValue("username", "error.user", "Korisničko ime već postoji");
            return "users/register";
        }


        if (userService.emailExists(user.getEmail())) {
            result.rejectValue("email", "error.user", "Email već postoji");
            return "users/register";
        }


        Optional<User> registeredUser = userService.registerUser(user);
        if (registeredUser.isEmpty()) {
            model.addAttribute("error", "Greška prilikom registracije. Pokušajte ponovo.");
            return "users/register";
        }

        return "redirect:/login";
    }
}
