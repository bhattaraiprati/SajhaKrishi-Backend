package com.example.sajhaKrishi.Controller.farmer;

import com.example.sajhaKrishi.Model.farmer.FarmerKyc;
import com.example.sajhaKrishi.Services.farmer.FarmerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
public class FarmerController {

    @Autowired
    private FarmerService farmerService;

    @PostMapping("/farmerKyc")
    public ResponseEntity<?> FarmerKyc(@RequestBody FarmerKyc farmerKyc, Authentication authentication){

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        return farmerService.farmerKycService(farmerKyc, email);
    }


}
