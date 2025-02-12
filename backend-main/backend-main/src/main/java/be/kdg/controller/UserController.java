package be.kdg.controller;

import be.kdg.domain.User;
import be.kdg.mapper.UserMapper;
import be.kdg.service.UserService;
import be.kdg.viewmodels.UserViewModel;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user-list")
    public String getUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "user/user_list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/create")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new UserViewModel());
        return "user/create_user";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public String createUser(
            @Valid UserViewModel userViewModel,
            BindingResult errors,
            Model model
    ) {
        if (errors.hasErrors()) {
            errors.getAllErrors().forEach(error -> {
                log.error(error.getDefaultMessage());
            });
            model.addAttribute("user", userViewModel);
            model.addAttribute("validationErrors", errors.getAllErrors());
            return "user/create_user";
        }

        userService.saveUser(userViewModel);
        return "redirect:/users/user-list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public String getUserById(@PathVariable int id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            model.addAttribute("error", "User not found");
            return "error/404";
        }
        model.addAttribute("user", user);
        return "user/user_details";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public String updateUserForm(@PathVariable("id") int id, Model model) {
        User user = userService.getUserById(id);
        UserViewModel userViewModel = UserMapper.toViewModel(user);
        userViewModel.setId(user.getId());
        userViewModel.setUsername(user.getUsername());
        userViewModel.setEmail(user.getEmail());
        userViewModel.setPassword(user.getPassword());
        userViewModel.setTemporaryAccessStart(user.getTemporaryAccessStart());
        userViewModel.setTemporaryAccessEnd(user.getTemporaryAccessEnd());
        model.addAttribute("user", userViewModel);
       // model.addAttribute("roles", roleService.getAllRoles());
        return "user/user-update";
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/edit/{id}")
    public String editUser(@Valid @PathVariable("id") int id, @ModelAttribute UserViewModel userViewModel, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("user", userViewModel);
            return "user/user-update";
        }
            User user = userService.getUserById(id);
            user.setId(userViewModel.getId());
            user.setPassword(userViewModel.getPassword());
            user.setUsername(userViewModel.getUsername());
            user.setEmail(userViewModel.getEmail());
            user.setTemporaryAccessStart(userViewModel.getTemporaryAccessStart());
            user.setTemporaryAccessEnd(userViewModel.getTemporaryAccessEnd());
            userService.saveUser(userViewModel);
        return "redirect:/users/user-list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") int id) {
        userService.deleteUser(id);
        return "redirect:/users/user-list";
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update-role")
    public String updateUserRole(@RequestParam Long id, @RequestParam String role) {
        userService.updateUserRole(Math.toIntExact(id), role);
        return "redirect:/users/user-list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/details/{id}")
    public String getUserDetails(@PathVariable Long id, Model model) {
        User user = userService.getUserById(Math.toIntExact(id));
        model.addAttribute("user", user);
        return "user/user-details";
    }


}

