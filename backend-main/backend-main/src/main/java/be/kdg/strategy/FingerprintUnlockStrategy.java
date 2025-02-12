package be.kdg.strategy;

import be.kdg.domain.Fingerprint;
import be.kdg.domain.Sensor;
import be.kdg.domain.User;
import be.kdg.service.FingerprintService;
import be.kdg.service.SensorService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component("Fingerprint")
public class FingerprintUnlockStrategy implements UnlockStrategy {

    private final SensorService sensorService;
    private final FingerprintService fingerprintService;
    private User validatedUser; // Store the user regardless of validation result

    public FingerprintUnlockStrategy(SensorService sensorService, FingerprintService fingerprintService) {
        this.sensorService = sensorService;
        this.fingerprintService = fingerprintService;
        this.validatedUser = null; // Initialize to null
    }

    @Override
    public boolean validate(Long lockId, String credentials) {
        // Reset validated user
        validatedUser = null;

        // Attempt to find the fingerprint by credentials
        Optional<Fingerprint> fingerprintOptional = fingerprintService.findByCredentials(credentials);
        if (fingerprintOptional.isEmpty()) {
            System.out.println("Fingerprint not found.");
            return false;
        }
        Fingerprint fingerprint = fingerprintOptional.get();

        // Always retrieve the user associated with the fingerprint
        validatedUser = fingerprint.getUser();

        // If no user is associated, reject the attempt
        if (validatedUser == null) {
            System.out.println("No user associated with the fingerprint.");
            return false;
        }

        // Validate the Sensor (Lock) by lockId
        Sensor sensor = sensorService.findById(lockId.intValue());
        if (sensor == null) {
            System.out.println("Sensor not found.");
            return false;
        }

        // Check if the sensor supports Fingerprint and is active
        if (!sensor.isFingerprint()) { // Assuming `isFingerprint()` checks if the sensor supports Fingerprint
            System.out.println("Sensor is either inactive or does not support Fingerprint.");
            return false;
        }

        // Check if the fingerprint is active
        if (!fingerprint.isActive()) {
            System.out.println("Fingerprint is inactive.");
            return false;
        }

        // Check if the user is active
        if (!validatedUser.isActive()) {
            System.out.println("User is inactive.");
            return false;
        }

        // Check if the user has access to the sensor
        if (!sensor.getAuthorizedUsers().contains(validatedUser)) {
            System.out.println("User does not have access to this sensor.");
            return false;
        }

        // Check if the current time is within the user's allowed time range
        LocalDateTime now = LocalDateTime.now();
        if (validatedUser.getTemporaryAccessStart() != null && validatedUser.getTemporaryAccessEnd() != null) {
            if (now.isBefore(validatedUser.getTemporaryAccessStart()) || now.isAfter(validatedUser.getTemporaryAccessEnd())) {
                System.out.println("Current time is outside of the user's allowed access time.");
                return false;
            }
        } else {
            System.out.println("User has unrestricted access time.");
        }

        // All validations passed
        return true;
    }

    @Override
    public User getValidatedUser() {
        if (validatedUser == null) {
            throw new IllegalStateException("Validation not performed or failed.");
        }
        return validatedUser;
    }
}
