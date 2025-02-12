package be.kdg.strategy;

import be.kdg.domain.Nfc;
import be.kdg.domain.Sensor;
import be.kdg.domain.User;
import be.kdg.service.NfcService;
import be.kdg.service.SensorService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component("NFC")
public class NFCUnlockStrategy implements UnlockStrategy {

    private final SensorService sensorService;
    private final NfcService nfcService;
    private User validatedUser; // Store the user regardless of validation result

    public NFCUnlockStrategy(SensorService sensorService, NfcService nfcService) {
        this.sensorService = sensorService;
        this.nfcService = nfcService;
        this.validatedUser = null; // Initialize to null
    }

    @Override
    public boolean validate(Long lockId, String credentials) {
        // Reset validated user
        validatedUser = null;

        // Attempt to find the NFC card by credentials
        Optional<Nfc> nfcOptional = nfcService.findByCredentials(credentials);
        if (nfcOptional.isEmpty()) {
            System.out.println("NFC card not found.");
            return false;
        }
        Nfc nfc = nfcOptional.get();

        // Always retrieve the user associated with the NFC card
        validatedUser = nfc.getUser();

        // If no user is associated, reject the attempt
        if (validatedUser == null) {
            System.out.println("No user associated with the NFC card.");
            return false;
        }

        // Validate the Sensor (Lock) by lockId
        Sensor sensor = sensorService.findById(lockId.intValue());
        if (sensor == null) {
            System.out.println("Sensor not found.");
            return false;
        }

        // Check if the sensor supports NFC and is active
        if (!sensor.isNfc()) { // Assuming `isNfc()` checks if the sensor supports NFC
            System.out.println("Sensor is either inactive or does not support NFC.");
            return false;
        }

        // Check if the NFC card is active
        if (!nfc.isActive()) {
            System.out.println("NFC card is inactive.");
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
