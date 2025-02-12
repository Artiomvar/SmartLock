package be.kdg.domain;

import be.kdg.enums.Country;
import be.kdg.enums.HearAboutUs;
import be.kdg.enums.MaritalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "user_details")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String fullName;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String gender;
    @Enumerated(EnumType.STRING)
    private HearAboutUs hearAboutUs;
    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;
    @Enumerated(EnumType.STRING)
    private Country country;
    private boolean agreedToTerms;
}