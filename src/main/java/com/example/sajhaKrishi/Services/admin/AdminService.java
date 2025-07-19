package com.example.sajhaKrishi.Services.admin;

import com.example.sajhaKrishi.DTO.Buyer.BuyerKycDTO;
import com.example.sajhaKrishi.DTO.FarmerKycDTO;
import com.example.sajhaKrishi.Model.BuyerKyc;
import com.example.sajhaKrishi.Model.farmer.FarmerKyc;
import com.example.sajhaKrishi.repository.BuyerKycRepo;
import com.example.sajhaKrishi.repository.FarmerKycRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private FarmerKycRepository farmerKycRepository;

    @Autowired
    private BuyerKycRepo buyerKycRepo;

    // Fetch all pending farmer KYC
    public List<FarmerKycDTO> getPendingFarmerKyc() {
        return farmerKycRepository.findByKycStatus("PENDING")
                .stream()
                .map(FarmerKycDTO::new)
                .collect(Collectors.toList());
    }

    // Search farmer KYC by name and status
    public List<FarmerKycDTO> searchFarmerKycByNameAndStatus(String name, String status) {
        return farmerKycRepository.findByUserNameAndStatus(name, status)
                .stream()
                .map(FarmerKycDTO::new)
                .collect(Collectors.toList());
    }

    // Approve farmer KYC
    public FarmerKycDTO approveFarmerKyc(Long id) {
        FarmerKyc kyc = farmerKycRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Farmer KYC not found"));
        kyc.setKycStatus("APPROVED");
        kyc.setVerified(true);
        return new FarmerKycDTO(farmerKycRepository.save(kyc));
    }

    public FarmerKycDTO getFarmerKycById(Long id) {
        FarmerKyc kyc = farmerKycRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Farmer KYC not found"));
        return new FarmerKycDTO(kyc);
    }
    // Decline farmer KYC
    public FarmerKycDTO declineFarmerKyc(Long id) {
        FarmerKyc kyc = farmerKycRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Farmer KYC not found"));
        kyc.setKycStatus("DECLINED");
        kyc.setVerified(false);
        return new FarmerKycDTO(farmerKycRepository.save(kyc));
    }

    // Fetch all pending buyer KYC
    public List<BuyerKycDTO> getPendingBuyerKyc() {
        return buyerKycRepo.findByKycStatus("PENDING")
                .stream()
                .map(BuyerKycDTO::new)
                .collect(Collectors.toList());
    }

    // Search buyer KYC by name and status
    public List<BuyerKycDTO> searchBuyerKycByNameAndStatus(String name, String status) {
        return buyerKycRepo.findByFullNameAndStatus(name, status)
                .stream()
                .map(BuyerKycDTO::new)
                .collect(Collectors.toList());
    }

    // Approve buyer KYC
    public BuyerKycDTO approveBuyerKyc(Long id) {
        BuyerKyc kyc = buyerKycRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Buyer KYC not found"));
        kyc.setKycStatus("APPROVED");
        kyc.setVerified(true);
        return new BuyerKycDTO(buyerKycRepo.save(kyc));
    }

    // Decline buyer KYC
    public BuyerKycDTO declineBuyerKyc(Long id) {
        BuyerKyc kyc = buyerKycRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Buyer KYC not found"));
        kyc.setKycStatus("DECLINED");
        kyc.setVerified(false);
        return new BuyerKycDTO(buyerKycRepo.save(kyc));
    }

    // Get buyer KYC by ID
    public BuyerKycDTO getBuyerKycById(Long id) {
        BuyerKyc kyc = buyerKycRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Buyer KYC not found"));
        return new BuyerKycDTO(kyc);
    }
}