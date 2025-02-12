package be.kdg.viewmodels;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NfcViewModel {
    private Long id;

    @NotEmpty(message = "name is required")
    private String name;


    @NotEmpty(message = "credentials is required")
    private String credentials;


    private boolean active;

    @NotNull(message = "id is required")
    private String userID;

}
