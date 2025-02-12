package be.kdg.controller;

import be.kdg.domain.Sensor;
import be.kdg.service.Impl.UnlockService;
import be.kdg.service.SensorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/register")
public class RegisterController {

    private final UnlockService unlockService;
    private final SensorService sensorService;

    public RegisterController(UnlockService unlockService, SensorService sensorService) {
        this.unlockService = unlockService;
        this.sensorService = sensorService;
    }

    @PostMapping
    public ResponseEntity<String> register(@RequestBody Map<String, String> payload) {
        try {
            if (!payload.containsKey("method")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing 'method' in request payload");
            }

            String method = payload.get("method");
            String id = payload.get("lockId");
            Sensor sensor = sensorService.findById(Integer.valueOf(id));
            String credentials = unlockService.sendRegisterRequestToArduino(method, sensor.getIpAddress());

            return ResponseEntity.ok(credentials);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Register failed: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

}
