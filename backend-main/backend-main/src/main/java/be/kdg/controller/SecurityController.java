package be.kdg.controller;

import be.kdg.repository.UserRepository;
import be.kdg.service.UserService;
import be.kdg.viewmodels.RegistrationViewModel;
import be.kdg.viewmodels.UserViewModel;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SecurityController {
    private final UserService userService;
    private AuthenticationManager authenticationManager;

    public SecurityController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new RegistrationViewModel());
        return "login";
    }


    @PostMapping("/login")
    public String loginUser(RegistrationViewModel user, Model model) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "redirect:/dashboard";
        } catch (AuthenticationException e) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }


    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new RegistrationViewModel());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Validated RegistrationViewModel registrationViewModel,
            BindingResult bindingResult,
            Model model) {

        if (!registrationViewModel.isPasswordMatching()) {
            bindingResult.rejectValue("confirmPassword", "error.user", "Passwords must match");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", registrationViewModel); // Populate form data to redisplay
            return "register"; // Return to the registration form
        }

        try {
            userService.registerUser(registrationViewModel);

            model.addAttribute("message", "Registration successful! Please log in.");
            return "login"; // Redirect to the login page after successful registration
        } catch (IllegalArgumentException e) {
            // Handle application-specific exceptions (e.g., duplicate email or username)
            model.addAttribute("user", registrationViewModel);
            model.addAttribute("errorMessage", e.getMessage());
            return "register"; // Return to the registration form with error
        } catch (Exception e) {
            // Catch any unexpected errors
            model.addAttribute("user", registrationViewModel);
            model.addAttribute("errorMessage", "An unexpected error occurred. Please try again.");
            return "register"; // Return to the registration form with error
        }
    }



}
