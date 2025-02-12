package be.kdg.controller;

import be.kdg.domain.RfidCard;
import be.kdg.domain.Sensor;
import be.kdg.domain.User;
import be.kdg.service.RfidCardService;
import be.kdg.service.UserService;
import be.kdg.viewmodels.RfidCardViewModel;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

@Controller
public class RfidController {
    private final RfidCardService rfidCardService;
    private final UserService userService;

    public RfidController(RfidCardService rfidCardService, UserService userService) {
        this.rfidCardService = rfidCardService;
        this.userService = userService;
    }

    @GetMapping("/rfid")
    public String getAllRfid(Model model,Authentication authentication) {

        String role = authentication.getAuthorities().iterator().next().getAuthority();
        String username = authentication.getName();
        Optional<User> user= userService.findByName(username);

        if ("ROLE_ADMIN".equalsIgnoreCase(role)) {
            model.addAttribute("rfids", rfidCardService.findAll());

        } else if ("ROLE_USER".equalsIgnoreCase(role)) {
            model.addAttribute("rfids", rfidCardService.findByUserId(user.get().getId()));
        }



        return "rfid/rfid_list";
    }

    @GetMapping("/rfid/add")
    public String showAddRfidForm(@RequestParam(value = "userId", required = false) Long userId, Model model, Authentication authentication) {
        model.addAttribute("rfid", new RfidCardViewModel());
        model.addAttribute("users", userService.getAllUsers()); // Fetch all users
        model.addAttribute("selectedUserId", userId); // Pass the selected user ID back to the view

        // Fetch sensors for the selected user
        if (userId != null) {
            model.addAttribute("sensors", userService.getUserById(userId.intValue()).getAccessibleSensors());
        } else {
            model.addAttribute("sensors", List.of()); // Empty list if no user is selected
        }

        return "rfid/add_rfid";
    }



    @PostMapping("/rfid/add")
    public String addRfid(@Valid @ModelAttribute("rfid") RfidCardViewModel viewModel, BindingResult bindingResult, Model model,Authentication authentication) {
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        String username = authentication.getName();
        Optional<User> user= userService.findByName(username);

        if ("ROLE_ADMIN".equalsIgnoreCase(role)) {

            if (bindingResult.hasErrors()) {
                bindingResult.getFieldErrors().forEach(error ->
                        System.out.println("Field: " + error.getField() + " - Message: " + error.getDefaultMessage())
                );
                model.addAttribute("rfid", viewModel);
                return "rfid/add_rfid";
            }

            rfidCardService.save(viewModel,user.get());



        } else if ("ROLE_USER".equalsIgnoreCase(role)) {

            if (bindingResult.hasErrors()) {
                bindingResult.getFieldErrors().forEach(error ->
                        System.out.println("Field: " + error.getField() + " - Message: " + error.getDefaultMessage())
                );
                model.addAttribute("rfid", viewModel);
                return "rfid/add_rfid";
            }

            rfidCardService.save(viewModel,user.get());


        }

        return "redirect:/rfid";

    }

    @GetMapping("/rfid/update/{id}")
    public String updateRfidForm(@PathVariable("id") Long id, Model model) {
        RfidCard rfidCard = rfidCardService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid RFID ID: " + id));

        // Create a ViewModel with the necessary data
        RfidCardViewModel viewModel = new RfidCardViewModel();
        viewModel.setId(rfidCard.getId());
        viewModel.setName(rfidCard.getName());
        viewModel.setCredentials(rfidCard.getCredentials());
        viewModel.setActive(rfidCard.isActive());
        viewModel.setUserId(rfidCard.getUser().getId()); // Set the userId (not the full user object)

        model.addAttribute("rfidCard", viewModel);  // Pass the ViewModel to the form
        model.addAttribute("users", userService.getAllUsers()); // Provide the users list for the select dropdown
        return "rfid/update_rfid";
    }


    @PostMapping("/rfid/update/{id}")
    public String updateRfid(@PathVariable("id") Long id,
                             @Valid @ModelAttribute("rfidCard") RfidCardViewModel viewModel,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            System.out.println("Binding errors: " + bindingResult.getAllErrors());
            model.addAttribute("rfidCard", viewModel);
            model.addAttribute("users", userService.getAllUsers()); // Provide the users list in case of errors
            return "rfid/update_rfid";  // Return the form with errors
        }

        // Find the RFID card and update its user ID based on the form submission
        RfidCard rfidCard = rfidCardService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid RFID ID: " + id));

        // Update the RfidCard with the new data
        rfidCard.setName(viewModel.getName());
        rfidCard.setCredentials(viewModel.getCredentials());
        rfidCard.setActive(viewModel.isActive());
        // Assuming the userId is valid, set the user
        rfidCard.setUser(userService.getUserById(viewModel.getUserId().intValue()));

        rfidCardService.save(rfidCard);  // Save the updated RFID card

        return "redirect:/rfid";  // Redirect to the list of RFID cards
    }

    @GetMapping("/rfid/delete/{id}")
    public String deleteRfid(@PathVariable("id") Long id) {
        rfidCardService.deleteById(id);
        return "redirect:/rfid";
    }

    @PostMapping("/rfid/toggle/{id}")
    public ResponseEntity<Void> toggleRfid(@PathVariable("id") Long id) {
        RfidCard rfidCard = rfidCardService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid RFID ID: " + id));
        rfidCard.setActive(!rfidCard.isActive());
        rfidCardService.save(rfidCard);
        return ResponseEntity.noContent().build();
    }
}
