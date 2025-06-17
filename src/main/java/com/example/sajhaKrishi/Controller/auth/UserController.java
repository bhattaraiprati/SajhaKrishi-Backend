package com.example.sajhaKrishi.Controller.auth;

import com.example.sajhaKrishi.Model.User;
import com.example.sajhaKrishi.Services.UserService;
import com.example.sajhaKrishi.repository.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserRepo repo;

    @Autowired
    private UserService service;

    private  BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(12);


    @GetMapping("/")
    public String greet(HttpServletRequest request){

        return " Hello from the pratik portal \n"+ request.getSession().getId();
    }

    @GetMapping("/test-auth")
    public String testAuth(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return "Authenticated as: " + userDetails.getUsername() +
                "\nRoles: " + userDetails.getAuthorities();
    }
    @PostMapping("/registers")
    public ResponseEntity<String> Register(@RequestBody User u){
        return service.registerUser(u);
    }
    @PostMapping("/userLogin")
    public ResponseEntity<?> userLogin(@RequestBody User user) {
        System.out.println("Login attempt for user: " + user.getEmail());
        return service.verify(user);
    }

}
