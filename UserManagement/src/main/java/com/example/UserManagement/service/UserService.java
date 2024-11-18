package com.example.UserManagement.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.UserManagement.entity.User;
import com.example.UserManagement.exception.UserNotFoundException;
import com.example.UserManagement.repository.UserRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

//    public User getUserById(Long id) {
//        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
//    }
    
//    public User getUserById(Long id) {
//        return userRepository.findById(id)
//            .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));
//    }
    
    @CircuitBreaker(name = "myServiceCircuitBreaker", fallbackMethod = "fallbackGetUserById")
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));
    }
    
 // Fallback method
    public User fallbackGetUserById(Long id, Throwable throwable) {
        System.out.println("Fallback triggered for ID " + id + " due to: " + throwable.getMessage());
        
        // Returning a default User object or handling as per business logic
        User fallbackUser = new User();
        fallbackUser.setId(id);
        fallbackUser.setUsername("Fallback User");
        fallbackUser.setEmail("fallback@example.com");
        return fallbackUser;
    }


    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setPassword(userDetails.getPassword());
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

