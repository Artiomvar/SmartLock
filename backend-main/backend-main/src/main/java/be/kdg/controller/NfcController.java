package be.kdg.controller;

import be.kdg.domain.Nfc;
import be.kdg.domain.RfidCard;
import be.kdg.domain.User;
import be.kdg.service.Impl.NfcServiceImpl;
import be.kdg.service.Impl.UserServiceImpl;
import be.kdg.viewmodels.NfcViewModel;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping
public class NfcController {
    private final NfcServiceImpl nfcService;
    private final UserServiceImpl userService;

    public NfcController(NfcServiceImpl nfcService, UserServiceImpl userService) {
        this.nfcService = nfcService;
        this.userService = userService;
    }

    @GetMapping("/nfc")
    public String showNfcForm(Model model, Authentication authentication) {
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        String username = authentication.getName();
        Optional<User> user = userService.findByName(username);

        if ("ROLE_ADMIN".equalsIgnoreCase(role)) {
            model.addAttribute("nfcs", nfcService.findAll());
        } else if ("ROLE_USER".equalsIgnoreCase(role)) {
            model.addAttribute("nfcs", nfcService.findNfcsByUserId(user.get().getId()));
        }

        return "nfc/nfc_list";
    }




    @GetMapping("/nfc/add")
    public String addNfc(Model model) {
        model.addAttribute("nfc", new NfcViewModel());
        model.addAttribute("users", userService.getAllUsers());
        return "nfc/add_nfc";
    }



    @PostMapping("/nfc/add")
    public String addNfc(@Valid @ModelAttribute("nfc") NfcViewModel nfcViewModel, BindingResult bindingResult,Model model,Authentication authentication) {
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        String username = authentication.getName();
        Optional<User> user = userService.findByName(username);
        nfcViewModel.setUserID(String.valueOf(user.get().getId()));
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));

            model.addAttribute("nfc", nfcViewModel);
            return "nfc/add_nfc";

        }

        nfcService.save(nfcViewModel);
        return "redirect:/nfc";

    }


    @GetMapping("/nfc/update/{id}")
    public String updateNfc(@PathVariable("id") Long id, Model model,Authentication authentication) {
        Nfc nfc = nfcService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nfc not found"));
        NfcViewModel nfcViewModel = new NfcViewModel();
        nfcViewModel.setId(nfc.getId());
        nfcViewModel.setName(nfc.getName());
        nfcViewModel.setCredentials(nfc.getCredentials());
        nfcViewModel.setActive(nfc.isActive());
        nfcViewModel.setUserID(String.valueOf(nfc.getUser().getId()));

        model.addAttribute("nfcCard", nfcViewModel);
        model.addAttribute("users", userService.getAllUsers());
        return "nfc/update_nfc";

    }

    @PostMapping("/nfc/update/{id}")
    public String update(@PathVariable("id") Long id,
                         @ModelAttribute("nfcCard") @Valid NfcViewModel nfcViewModel,
                         BindingResult bindingResult ,Model model) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));
            model.addAttribute("nfcCard", nfcViewModel);
            model.addAttribute("users", userService.getAllUsers());
            return "nfc/update_nfc";
        }

        Nfc nfc = nfcService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nfc not found"));
        nfc.setName(nfcViewModel.getName());
        nfc.setCredentials(nfcViewModel.getCredentials());
        nfc.setActive(nfcViewModel.isActive());
        nfc.setUser(userService.getUserById(Integer.parseInt(nfcViewModel.getUserID())));

        nfcService.save(nfc);
        return "redirect:/nfc";

    }


    @GetMapping("/nfc/delete/{id}")
    public String deleteNfc(@PathVariable("id") Long id) {
        nfcService.deleteById(id);
        return "redirect:/nfc";

    }

    @PostMapping("/nfc/toggle/{id}")
    public ResponseEntity<Void> toggleNfc(@PathVariable("id") Long id) {
        Nfc nfc = nfcService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid RFID ID: " + id));
        nfc.setActive(!nfc.isActive());
        nfcService.save(nfc);
        return ResponseEntity.noContent().build();
    }
}
