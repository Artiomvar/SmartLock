package be.kdg.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnlockRequest {

    private String mac;            // MAC address of the lock being accessed
    private String ipAddress;      // IP address of the lock being accessed
    private String method;         // Unlock method (e.g., RFID, Fingerprint, NFC, etc.)
    private String credentials;    // Credentials provided for the unlock attempt
}