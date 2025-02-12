    package be.kdg.viewmodels;

    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.NotNull;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    @Data
    @NoArgsConstructor
    public class SensorViewModel {
        private int id;

        @NotBlank(message = "Sensor name cannot be blank")
        private String name;

        @NotBlank(message = "MAC address cannot be blank")
        private String mac;

        @NotNull(message = "Status cannot be null")
        private Boolean status;

        private boolean fingerPrint;

        private boolean nfc;

        private boolean rfidCard;

        @NotBlank(message = "IP address cannot be blank")
        private String ipAddress;

    }