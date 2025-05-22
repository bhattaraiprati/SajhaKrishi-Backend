package com.example.sajhaKrishi.Services.buyer;

import com.example.sajhaKrishi.Model.BuyerKyc;
import com.example.sajhaKrishi.repository.BuyerKycRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class BuyerService {

    @Autowired(required = true)
    private BuyerKycRepo kycRepo;

    public ResponseEntity<?> KycRegistration(BuyerKyc kyc){
        try{
            kycRepo.save(kyc);
            return ResponseEntity.ok("Successfully register kyc form");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error"+e.getMessage());
        }
    }

}
