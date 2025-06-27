package com.example.sajhaKrishi.Controller.auth;

import com.example.sajhaKrishi.DTO.OtpRequestDTO;
import com.example.sajhaKrishi.DTO.UserDTO;
import com.example.sajhaKrishi.Model.PasswordResetOtp;
import com.example.sajhaKrishi.Model.User;
import com.example.sajhaKrishi.Services.EmailSendService;
import com.example.sajhaKrishi.Services.OtpUtil;
import com.example.sajhaKrishi.Services.UserService;
import com.example.sajhaKrishi.repository.PasswordResetOtpRepository;
import com.example.sajhaKrishi.repository.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private UserRepo repo;

    @Autowired
    private UserService service;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private  BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(12);

    private final EmailSendService emailSendService;

    private final PasswordResetOtpRepository passwordResetOtpRepository;

    @Autowired
    public UserController( EmailSendService emailSendService, PasswordResetOtpRepository passwordResetOtpRepository) {
        this.emailSendService = emailSendService;
        this.passwordResetOtpRepository = passwordResetOtpRepository;
    }

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

    @PostMapping("/api/checkEmail")
    public ResponseEntity<?> checkEmail(@RequestBody User userData){
        String email = userData.getEmail();
        User user = repo.findByEmail(email);
        if(user == null){
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("Provided Email not Found");
        }
        PasswordResetOtp passwordResetOtp1 = passwordResetOtpRepository.findByUserId(user.getId());
        if (passwordResetOtp1 != null){
            passwordResetOtpRepository.delete(passwordResetOtp1);

        }

        String otp = OtpUtil.generateOtp();
        // Save OTP in PasswordResetOtp table
        PasswordResetOtp passwordResetOtp = new PasswordResetOtp();
        passwordResetOtp.setUser(user);
        passwordResetOtp.setOtp(otp);
        passwordResetOtp.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        passwordResetOtp.setUsed(false);
        passwordResetOtpRepository.save(passwordResetOtp);

        // Send OTP via email
        String subject = "Password Reset OTP";
        String text = "Your OTP for password reset is: " + otp + "\nThis OTP is valid for 10 minutes.";

        try {
            emailSendService.sendSimpleEmail(email, subject, text);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send OTP: " + e.getMessage());
        }

        return ResponseEntity.ok("OTP sent to your email");
    }


    @PostMapping("/api/checkOTP")
    public ResponseEntity<?> checkOtp( @RequestBody OtpRequestDTO otpRequestDTO){
    String email = otpRequestDTO.getEmail();
    String otp = otpRequestDTO.getOtp();

        User user = repo.findByEmail(email);
        if(user == null){
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("Provided Email not Found");
        }

        Optional<PasswordResetOtp> passwordResetOtpOpt = passwordResetOtpRepository.findLatestValidByUserId(user.getId());
        if (passwordResetOtpOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No valid OTP found or OTP has expired");
        }
        PasswordResetOtp passwordResetOtp = passwordResetOtpOpt.get();
        if (!passwordResetOtp.getOtp().equals(otp)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP");
        }

        if (passwordResetOtp.isUsed()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP is already used");
        }

        // Mark OTP as used
        passwordResetOtp.setUsed(true);
        passwordResetOtpRepository.save(passwordResetOtp);

        return ResponseEntity.ok("OTP verified successfully");

    }

    @PostMapping("/api/updatePassword")
    public ResponseEntity<?> UpdatePassword(@RequestBody User userData){
        String email = userData.getEmail();
        String password = userData.getPassword();

        User user = repo.findByEmail(email);
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
        repo.save(user);
        return ResponseEntity.ok("Password changed successfully");

    }

    @PostMapping("/api/user/getUserDetailsById/{id}")
    public  ResponseEntity<?> getUserDetailsById(@PathVariable Long id){

        UserDTO user = repo.findUserById(id);

        if(user == null){
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.ok(user);


    }

}
