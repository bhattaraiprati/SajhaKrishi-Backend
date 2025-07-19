package com.example.sajhaKrishi.Controller.admin;

import com.example.sajhaKrishi.DTO.Buyer.BuyerKycDTO;
import com.example.sajhaKrishi.DTO.FarmerKycDTO;
import com.example.sajhaKrishi.Model.BuyerKyc;
import com.example.sajhaKrishi.Model.farmer.FarmerKyc;
import com.example.sajhaKrishi.Services.admin.AdminService;
import com.example.sajhaKrishi.repository.BuyerKycRepo;
import com.example.sajhaKrishi.repository.FarmerKycRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private FarmerKycRepository farmerKycRepository;

    @Autowired
    private BuyerKycRepo buyerKycRepo;
    @Autowired
    private AdminService adminService;


    @GetMapping("/farmer-kyc/pending")
    public ResponseEntity<List<FarmerKycDTO>> getPendingFarmerKyc() {
        return ResponseEntity.ok(adminService.getPendingFarmerKyc());
    }

    @GetMapping("/farmer-kyc/search")
    public ResponseEntity<List<FarmerKycDTO>> searchFarmerKyc(
            @RequestParam String name,
            @RequestParam String status) {
        return ResponseEntity.ok(adminService.searchFarmerKycByNameAndStatus(name, status));
    }

    @PutMapping("/farmer-kyc/{id}/approve")
    public ResponseEntity<FarmerKycDTO> approveFarmerKyc(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.approveFarmerKyc(id));
    }

    @PutMapping("/farmer-kyc/{id}/decline")
    public ResponseEntity<FarmerKycDTO> declineFarmerKyc(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.declineFarmerKyc(id));
    }

    // Get all pending buyer KYC
    @GetMapping("/buyer-kyc/pending")
    public ResponseEntity<List<BuyerKycDTO>> getPendingBuyerKyc() {
        return ResponseEntity.ok(adminService.getPendingBuyerKyc());
    }

    // Search buyer KYC by name and status
    @GetMapping("/buyer-kyc/search")
    public ResponseEntity<List<BuyerKycDTO>> searchBuyerKyc(
            @RequestParam String name,
            @RequestParam String status) {
        return ResponseEntity.ok(adminService.searchBuyerKycByNameAndStatus(name, status));
    }

    // Approve buyer KYC
    @PutMapping("/buyer-kyc/{id}/approve")
    public ResponseEntity<BuyerKycDTO> approveBuyerKyc(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.approveBuyerKyc(id));
    }

    // Decline buyer KYC
    @PutMapping("/buyer-kyc/{id}/decline")
    public ResponseEntity<BuyerKycDTO> declineBuyerKyc(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.declineBuyerKyc(id));
    }

    // Get buyer KYC by ID
    @GetMapping("/buyer-kyc/{id}")
    public ResponseEntity<BuyerKycDTO> getBuyerKycById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getBuyerKycById(id));
    }
}