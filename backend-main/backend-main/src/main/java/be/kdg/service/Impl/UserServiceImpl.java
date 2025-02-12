package be.kdg.service.Impl;

import be.kdg.domain.*;
import be.kdg.enums.*;
import be.kdg.mapper.UserMapper;
import be.kdg.repository.UserRepository;
import be.kdg.service.UserService;
import be.kdg.viewmodels.RegistrationViewModel;
import be.kdg.viewmodels.UserViewModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAllOrderById(); // Retrieve all users
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username); // Retrieve user by username
    }

    @Override
    public User getUserById(int id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("invalide user" + id)); // Retrieve user by ID or return null if not found
    }

    @Override
    public void saveUser(UserViewModel userViewModel) {
        User user = UserMapper.toEntity(userViewModel);
        if(Objects.nonNull(user)){
        userRepository.save(user); // Save or update the user
        }else {
                log.error("UserViewModel is null");
        }

    }

    @Override
    public long getUserCount() {
        return userRepository.count();
    }

    @Override
    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

    @Override
    public void registerUser(RegistrationViewModel registrationViewModel) {
        if (!registrationViewModel.isPasswordMatching()) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        Optional<User> existingUser = userRepository.findByEmail(registrationViewModel.getEmail());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("A user with this email already exists.");
        }

        Optional<User> existingUsername = userRepository.findByUsername(registrationViewModel.getUsername());
        if (existingUsername.isPresent()) {
            throw new IllegalArgumentException("A user with this username already exists.");
        }

        User user = new User();
        user.setUsername(registrationViewModel.getUsername());
        user.setEmail(registrationViewModel.getEmail());
        user.setPassword(passwordEncoder.encode(registrationViewModel.getPassword())); // Encrypt the password
        user.setActive(true);
        user.setUserType(UserType.valueOf(registrationViewModel.getUserType()));

        UserDetails userDetails = new UserDetails();
        userDetails.setFullName(registrationViewModel.getFullName());
        userDetails.setPhoneNumber(registrationViewModel.getPhoneNumber());
        userDetails.setDateOfBirth(registrationViewModel.getDateOfBirth());
        userDetails.setGender(registrationViewModel.getGender());

        userDetails.setCountry(Country.valueOf(registrationViewModel.getCountry()));
        userDetails.setHearAboutUs(HearAboutUs.valueOf(registrationViewModel.getHearAboutUs()));
        userDetails.setMaritalStatus(MaritalStatus.valueOf(registrationViewModel.getMaritalStatus()));


        user.setUserDetails(userDetails);


        userRepository.save(user);
    }

    @Override
    public User loginUser(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        return user;
    }

    @Override
    public UserViewModel getUserDetails(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + id + " not found."));

        return UserMapper.toViewModel(user);
    }

    @Override
    public Optional<User> findByName(String name) {
        return userRepository.findByUsername(name);
    }

    @Override
    public void updateUserRole(int id, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + id + " not found."));

        user.setRole(String.valueOf(UserRole.valueOf(role)));

        userRepository.save(user);

        log.info("User ID " + id + " role updated to: " + role);
    }


    @Override
    public Map<UserType, Double> findUserTypePercentages() {
        List<User> users = userRepository.findAll();
        long totalUsers = users.size();

        if (totalUsers == 0) {
            throw new IllegalArgumentException("No users found to calculate percentages.");
        }

        Map<UserType, Long> userCountByType = users.stream()
                .filter(user -> user.getUserType() != null) // Filter out users with null UserType
                .collect(Collectors.groupingBy(User::getUserType, Collectors.counting()));

        return userCountByType.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> (entry.getValue() * 100.0) / totalUsers
                ));
    }

    @Override
    public List<String> findAllUsersWithCountries() {
        return userRepository.findAllUsersWithCountries();
    }
}
