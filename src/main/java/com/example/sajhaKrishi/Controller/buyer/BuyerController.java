package com.example.sajhaKrishi.Controller.buyer;

import com.example.sajhaKrishi.DTO.Buyer.BuyerKycDTO;
import com.example.sajhaKrishi.Model.BuyerKyc;
import com.example.sajhaKrishi.Services.buyer.BuyerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class BuyerController {

    @Autowired
    private BuyerService buyerService;

    @PostMapping("/buyerKyc")
    public ResponseEntity<?> buyerKyc(@RequestBody BuyerKycDTO kyc){

        return buyerService.KycRegistration(kyc);
    }

    @GetMapping("/api/getkycById/{id}")
    public ResponseEntity<?> kycDetailsById(@PathVariable Long id) {
        try {
            BuyerKycDTO buyerKycDTO = buyerService.getKycByUserId(id);
            if (buyerKycDTO == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("KYC not found for user ID: " + id);
            }
            return ResponseEntity.ok(buyerKycDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
