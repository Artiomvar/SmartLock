package be.kdg.viewmodels;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationViewModel {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "Confirm Password is required")
    private String confirmPassword;

    @NotBlank(message = "Full Name is required")
    private String fullName;

    @NotBlank(message = "Phone Number is required")
//    @Pattern(regexp = "\\+?[0-9\\s-]{10,15}", message = "Phone Number must be valid")
    private String phoneNumber;

    @Past(message = "Date of Birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotBlank(message = "Marital Status is required")
    private String maritalStatus;

    @NotBlank(message = "User Type is required")
    private String userType;

    private String address;

    @NotNull(message = "You must agree to the terms and conditions")
    private Boolean agreedToTerms;

//    @NotEmpty(message = "At least one language must be selected")
    private String country; // Multi-select field

//    @NotBlank(message = "How you heard about us is required")
    private String hearAboutUs;
    

    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
    }
}