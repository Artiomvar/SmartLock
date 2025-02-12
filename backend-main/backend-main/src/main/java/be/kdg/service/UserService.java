package be.kdg.service;

import be.kdg.domain.User;
import be.kdg.enums.UserType;
import be.kdg.viewmodels.RegistrationViewModel;
import be.kdg.viewmodels.UserViewModel;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers(); // Retrieve all users

    Optional<User> getUserByUsername(String username); // Retrieve a user by username

    User getUserById(int id); // Retrieve a user by ID

    void saveUser(UserViewModel viewModel); // Save or update a user

    long getUserCount();

    void deleteUser(int id);

    // Method to register a new user
    void registerUser(RegistrationViewModel registrationViewModel);

    // Method to authenticate a user during login
    User loginUser(String email, String password);

    void updateUserRole(int id, String role);

    UserViewModel getUserDetails(int id);
    Optional<User> findByName(String name);

    Map<UserType, Double> findUserTypePercentages();

    List<String> findAllUsersWithCountries();


    
    
}
