package be.kdg.controller;

import be.kdg.domain.UnlockRequest;
import be.kdg.service.Impl.UnlockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/unlock")
public class UnlockController {

    private final UnlockService unlockService;

    public UnlockController(UnlockService unlockService) {
        this.unlockService = unlockService;
    }
    @PostMapping
    public ResponseEntity<String> unlock(@RequestBody UnlockRequest request) {
        try {
            unlockService.handleUnlock(request);
            return ResponseEntity.ok("Unlock successful");
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unlock failed: " + e.getMessage());
        }
    }
}
