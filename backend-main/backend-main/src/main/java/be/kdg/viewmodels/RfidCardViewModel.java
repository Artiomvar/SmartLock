package be.kdg.viewmodels;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RfidCardViewModel {
    private Long id;

    @NotEmpty(message = "Name is required.")
    private String name;

    @NotEmpty(message = "Credentials are required.")
    private String credentials;

    private boolean active;

    @NotNull(message = "User ID is required.")
    private Long userId;

    private Long sensorId;
}
