package org.example.subwp.controller;

import org.example.subwp.model.User;
import org.example.subwp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController( PasswordEncoder passwordEncoder) {

        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "users/register";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<User> userOpt = Optional.ofNullable(userService.findUserById(id));
        if (userOpt.isPresent()) {
            model.addAttribute("user", userOpt.get());
            return "users/edit";
        }
        return "redirect:/users";
    }



    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id, @Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "users/edit";
        }

        Optional<User> existingUserOpt = Optional.ofNullable(userService.findUserById(id));
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            existingUser.setUsername(user.getUsername());
            existingUser.setEmail(user.getEmail());
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setRole(user.getRole());
            if (user.getPassword() != null && !user.getPassword().isEmpty() && !"********".equals(user.getPassword())) {

                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            userService.updateUser(existingUser);
        } else {
            throw new IllegalArgumentException("Korisnik sa ID " + id + " nije pronađen.");
        }

        return "redirect:/users";
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
