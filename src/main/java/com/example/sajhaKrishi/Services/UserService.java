package com.example.sajhaKrishi.Services;

import com.example.sajhaKrishi.Model.User;
import com.example.sajhaKrishi.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String verify(User user) {
        try {
            // Create authentication token with provided credentials
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());

            // Authenticate using the AuthenticationManager
            Authentication auth = authManager.authenticate(authToken);

            if (auth.isAuthenticated()) {
                // You might want to generate a JWT token here
                return jwtService.generateToken(user.getEmail(), user.getName());
            } else {
                return "Authentication failed";
            }
        } catch (BadCredentialsException e) {
            return "Invalid email or password";
        } catch (Exception e) {
            // Log the exception for debugging
            e.printStackTrace();
            return "Authentication error: " + e.getMessage();
        }
    }

    public ResponseEntity<String> registerUser(User user) {
        // Check if user already exists
        User existingUser = userRepo.findByEmail(user.getEmail());
        if (existingUser != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists");
        }

        // Encode password and save user
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepo.save(user);

        return ResponseEntity.ok("User registered successfully");
    }


}
