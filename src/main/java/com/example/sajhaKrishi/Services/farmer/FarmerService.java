package com.example.sajhaKrishi.Services.farmer;

import com.example.sajhaKrishi.Model.User;
import com.example.sajhaKrishi.Model.farmer.FarmerKyc;
import com.example.sajhaKrishi.repository.FarmerKycRepository;
import com.example.sajhaKrishi.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class FarmerService {

    @Autowired
    private FarmerKycRepository farmerRepo;

    @Autowired UserRepo userRepo;

    public ResponseEntity<?> farmerKycService(FarmerKyc farmerKyc, String email){

        User user = userRepo.findByEmail(email);

        if(user == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }

        // Check if KYC already exists
        FarmerKyc existingKyc = farmerRepo.findByUserEmail(email);
        if(existingKyc != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("KYC Already Exists");
        }

        farmerKyc.setUser(user);

        farmerRepo.save(farmerKyc);
        return ResponseEntity.ok("KYC Saved Successfully");
    }

}
