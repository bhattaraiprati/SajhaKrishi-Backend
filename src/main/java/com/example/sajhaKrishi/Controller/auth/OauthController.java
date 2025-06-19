package com.example.sajhaKrishi.Controller.auth;

import com.example.sajhaKrishi.Model.User;
import com.example.sajhaKrishi.Services.JWTService;
import com.example.sajhaKrishi.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
public class OauthController {

    @Autowired
    private UserRepo userRepo;

    @Autowired  // Added missing @Autowired annotation
    private JWTService jwtService;

    @Autowired
    private AuthenticationManager authManager;

    @PostMapping("/api/auth/google")
    public ResponseEntity<?> GoogleAuth(@RequestBody Map<String, String> requestBody) {

        String id_token = requestBody.get("id_token");

        if (id_token == null || id_token.trim().isEmpty()) {
            System.out.println("ID token is null or empty");
            return ResponseEntity.badRequest().body(Map.of("error", "ID token is required"));
        }

        try {
            // Create RestTemplate for HTTP requests
            RestTemplate restTemplate = new RestTemplate();

            // Build the Google token verification URL
            String googleTokenUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + id_token;

            // Make the HTTP GET request to Google
            ResponseEntity<Map> response = restTemplate.getForEntity(googleTokenUrl, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> tokenInfo = response.getBody();

                // Extract user information from Google response
                String email = (String) tokenInfo.get("email");
                String name = (String) tokenInfo.get("name");
                String picture = (String) tokenInfo.get("picture");

                System.out.println("Google Auth successful for: " + email);

                // Check if user exists in database
                User existingUser = userRepo.findByEmail(email);
                User user;

                if (existingUser != null) {
                    // User exists, use existing user
                    user = existingUser;
                    System.out.println("Existing user found: " + user.getName());

                    // Optionally update user info from Google (in case name or picture changed)
                    if (name != null && !name.equals(user.getName())) {
                        user.setName(name);
                        userRepo.save(user);
                    }
                } else {
                    // User doesn't exist, create new user
                    user = new User();
                    user.setEmail(email);
                    user.setName(name != null ? name : "Google User");
                    user.setRole("User"); // Default role
                    user.setPassword(null); // Google users don't have passwords

                    // Save the new user
                    user = userRepo.save(user);
                    System.out.println("New Google user created: " + user.getName());
                }

                // Generate JWT token
                String jwtToken = jwtService.generateToken(
                        user.getEmail(),
                        user.getName(),
                        user.getId(),
                        user.getRole()
                );
//                UsernamePasswordAuthenticationToken authToken =
//                        new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
//                Authentication auth = authManager.authenticate(authToken);
//                User u = userRepo.findByEmail(user.getEmail());
//                String token;
//                if (auth.isAuthenticated()) {
//                    token = jwtService.generateToken(u.getEmail(), u.getName(), u.getId(), u.getRole());
//                    return ResponseEntity.ok(token);
//                } else {
//                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
//
//                }
//                // Return success response with token and user info
                return ResponseEntity.ok(jwtToken);

            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid Google token"));
            }

        } catch (HttpClientErrorException e) {
            System.err.println("Google token verification failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
        } catch (Exception e) {
            System.err.println("Error during Google authentication: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Authentication failed: " + e.getMessage()));
        }
    }

}
