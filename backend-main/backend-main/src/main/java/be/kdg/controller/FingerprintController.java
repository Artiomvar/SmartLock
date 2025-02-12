package be.kdg.controller;

import be.kdg.domain.Fingerprint;
import be.kdg.domain.Nfc;
import be.kdg.domain.User;
import be.kdg.service.Impl.FingerprintServiceImpl;
import be.kdg.service.Impl.UserServiceImpl;
import be.kdg.viewmodels.FingerprintViewModel;
import be.kdg.viewmodels.NfcViewModel;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class FingerprintController {
    private final FingerprintServiceImpl fingerprintService;
    private final UserServiceImpl userService;

    public FingerprintController(FingerprintServiceImpl fingerprintService, UserServiceImpl userService) {
        this.fingerprintService = fingerprintService;
        this.userService = userService;
    }

    @GetMapping("/fingerPrint")
    public String getAllFingerprints(Model model, Authentication authentication) {
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        String username = authentication.getName();
        Optional<User> user = userService.findByName(username);

        if ("ROLE_ADMIN".equalsIgnoreCase(role)) {
            model.addAttribute("fingerprints", fingerprintService.findAll());
        } else if ("ROLE_USER".equalsIgnoreCase(role)) {
            model.addAttribute("fingerprints", fingerprintService.findFingerprintsByUserId(user.get().getId()));
        }

        return "fingerprint/fingerprint_list";
    }
    @GetMapping("/fingerPrint/add")
    public String addFingerprint(Model model) {
        model.addAttribute("fingerprint", new FingerprintViewModel());
        model.addAttribute("users",userService.getAllUsers());
        return "fingerprint/add_fingerprint";
    }

    @PostMapping("/fingerprint/add")
    public String processForm(@Valid @ModelAttribute("fingerprint") FingerprintViewModel fingerprintViewModel
    , BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));
            model.addAttribute("fingerprint", fingerprintViewModel);
            return "fingerprint/add_fingerprint";
        }
        fingerprintService.save(fingerprintViewModel);
        return "redirect:/fingerPrint";
    }

    @GetMapping("/fingerprint/update/{id}")
    public String updateNfc(@PathVariable("id") Long id, Model model) {
        Fingerprint fingerprint = fingerprintService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nfc not found"));
        FingerprintViewModel fingerprintViewModel = new FingerprintViewModel();
        fingerprintViewModel.setId(fingerprint.getId());
        fingerprintViewModel.setName(fingerprint.getName());
        fingerprintViewModel.setCredential(fingerprint.getCredentials());
        fingerprintViewModel.setActive(fingerprint.isActive());
        fingerprintViewModel.setUserId(String.valueOf(fingerprint.getUser().getId()));

        model.addAttribute("fingerprintCard", fingerprintViewModel);
        model.addAttribute("users", userService.getAllUsers());
        return "fingerprint/update_fingerprint";

    }

    @PostMapping("/fingerprint/update/{id}")
    public String update(@PathVariable("id") Long id,
                         @ModelAttribute("fingerprintCard") @Valid FingerprintViewModel fingerprintViewModel,
                         BindingResult bindingResult ,Model model) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));
            model.addAttribute("fingerprintCard", fingerprintViewModel);
            model.addAttribute("users", userService.getAllUsers());
            return "fingerprint/update_fingerprint";
        }

        Fingerprint fingerprint = fingerprintService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nfc not found"));
        fingerprint.setName(fingerprint.getName());
        fingerprint.setCredentials(fingerprint.getCredentials());
        fingerprint.setActive(fingerprint.isActive());
        fingerprint.setUser(userService.getUserById(Integer.parseInt(String.valueOf(fingerprint.getUser().getId()))));

        fingerprintService.save(fingerprint);
        return "redirect:/fingerPrint";

    }

    @GetMapping("/fingerprint/delete/{id}")
    public String deleteFingerprint(@PathVariable Long id) {
        fingerprintService.deleteById(id);
        return "redirect:/fingerPrint";

    }

    @PostMapping("/fingerprint/toggle/{id}")
    public ResponseEntity<Void> toggleNfc(@PathVariable("id") Long id) {
        Fingerprint fingerprint = fingerprintService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid RFID ID: " + id));
        fingerprint.setActive(!fingerprint.isActive());
        fingerprintService.save(fingerprint);
        return ResponseEntity.noContent().build();
    }
}
