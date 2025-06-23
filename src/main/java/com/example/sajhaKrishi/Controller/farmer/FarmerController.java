package com.example.sajhaKrishi.Controller.farmer;

import com.example.sajhaKrishi.DTO.FarmerKycDTO;
import com.example.sajhaKrishi.Model.farmer.FarmerKyc;
import com.example.sajhaKrishi.Services.farmer.FarmerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/farmer")
public class FarmerController {

    @Autowired
    private FarmerService farmerService;



    @PostMapping("/farmerKyc")
    public ResponseEntity<?> FarmerKyc(@RequestBody FarmerKycDTO farmerKycDTO, Authentication authentication){

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        return farmerService.farmerKycService(farmerKycDTO, email);
    }

    @GetMapping("/getFarmerKYCDetails/{id}")
    public ResponseEntity<?> getFarmerDetails(@PathVariable Long id) {
        try {
//            FarmerKyc farmerKyc = farmerService.getFarmerDetails(id);
            FarmerKycDTO farmerKyc = farmerService.getFarmerDetails(id);
            if (farmerKyc == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Farmer KYC details not found for user ID: " + id);
            }
            return ResponseEntity.ok(farmerKyc);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching farmer details: " + e.getMessage());
        }
    }

}
