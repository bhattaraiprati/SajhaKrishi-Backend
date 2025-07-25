package com.example.sajhaKrishi.Controller.auth;

import com.example.sajhaKrishi.Model.User;
import com.example.sajhaKrishi.Services.JWTService;
import com.example.sajhaKrishi.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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
                User user = userRepo.findByEmail(email);
                if (user == null) {
                    user = new User();
                    user.setEmail(email);
                    user.setName(name != null ? name : "Google User");
                    user.setRole("buyer");
                    user.setPassword(null);
                    user = userRepo.save(user);
                }

// Create an authenticated token
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);

                String jwtToken = jwtService.generateToken(
                        user.getEmail(), user.getName(), user.getId(), user.getRole()
                );
                System.out.println("Generated Google JWT: " + jwtToken);
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
