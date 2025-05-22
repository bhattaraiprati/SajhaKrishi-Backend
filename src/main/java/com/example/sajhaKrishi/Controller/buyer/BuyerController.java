package com.example.sajhaKrishi.Controller.buyer;

import com.example.sajhaKrishi.Model.BuyerKyc;
import com.example.sajhaKrishi.Services.buyer.BuyerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BuyerController {

    @Autowired
    private BuyerService buyerService;

    @PostMapping("/buyerKyc")
    public ResponseEntity<?> buyerKyc(@RequestBody BuyerKyc kyc){

        return buyerService.KycRegistration(kyc);
    }

//    @GetMapping("/kyc")
//    public String kycDetails(){
//        return "kyc details";
//    }
}
