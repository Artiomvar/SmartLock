package be.kdg.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "sensors")
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String mac;

    @Column(nullable = false)
    private boolean status;

    @Column(nullable = false)
    private boolean rfid;

    @Column(nullable = false)
    private boolean nfc;

    @Column(nullable = false)
    private boolean fingerprint;

    @Column(nullable = false)
    private String ipAddress; // Add this line

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "accessibleSensors")
    private Set<User> authorizedUsers;


    public void removeAuthorizedUser(User user) {
       this.authorizedUsers.remove(user);
        user.getAccessibleSensors().remove(this);
    }
}
