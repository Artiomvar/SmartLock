package be.kdg.viewmodels;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FingerprintViewModel {
    private Long id;

    @NotEmpty(message = "Name is required" )
    private String name;

    private boolean isActive;

    @NotEmpty(message = "credentials is required")
    private String credential;

    @NotNull(message = "userId is required")
    private String userId;


}
