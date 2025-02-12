package be.kdg.domain;

import be.kdg.enums.UserType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private String email;

    @Builder.Default
    private String role = "USER";

    @Enumerated(EnumType.STRING)
    private UserType userType;

    private boolean active;

    @ManyToMany
    @JoinTable(
            name = "user_sensors",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "sensor_id")
    )
    private Set<Sensor> accessibleSensors;

    private LocalDateTime temporaryAccessStart;
    private LocalDateTime temporaryAccessEnd;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private Set<RfidCard> rfidCards;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserDetails userDetails;

    public User(String admin, String password123, String mail) {
        this.username = admin;
        this.password = password123;
        this.email = mail;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
        userDetails.setUser(this);
    }
}