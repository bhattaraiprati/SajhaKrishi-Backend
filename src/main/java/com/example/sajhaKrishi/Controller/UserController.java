package com.example.sajhaKrishi.Controller;

import com.example.sajhaKrishi.Model.User;
import com.example.sajhaKrishi.Services.UserService;
import com.example.sajhaKrishi.repository.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        return " Hello from the pratik \n"+ request.getSession().getId();
    }

    @PostMapping("/registers")
    public ResponseEntity<String> Register(@RequestBody User u){
        User user = repo.findByEmail(u.getEmail());
        System.out.println(user);
        if (user == null){
            String encryptedPwd = bcrypt.encode(u.getPassword());
            u.setPassword(encryptedPwd);
            repo.save(u);
            return ResponseEntity.ok("User registered successfully");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists");
    }

    @PostMapping("/userLogin")
    public String userLogin(@RequestBody User user) {
        System.out.println("Login attempt for user: " + user.getEmail());
        return service.verify(user);
    }

}
