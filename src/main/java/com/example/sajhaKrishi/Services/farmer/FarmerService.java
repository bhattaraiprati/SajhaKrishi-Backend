package com.example.sajhaKrishi.Services.farmer;

import com.example.sajhaKrishi.DTO.FarmerKycDTO;
import com.example.sajhaKrishi.Model.User;
import com.example.sajhaKrishi.Model.farmer.BankDetails;
import com.example.sajhaKrishi.Model.farmer.ExperienceDetails;
import com.example.sajhaKrishi.Model.farmer.FarmDetails;
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

    @Autowired
    private  UserRepo userRepo;

    public ResponseEntity<?> farmerKycService(FarmerKycDTO farmerKycDTO, String email) {
        User user = userRepo.findByEmail(email);
        if(user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }

        // Check if KYC already exists
        if(farmerRepo.findByUserEmail(email) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("KYC Already Exists");
        }

        // Convert DTO to Entity
        FarmerKyc farmerKyc = new FarmerKyc();
        farmerKyc.setUser(user);

        // Set personal details
        farmerKyc.setDateOfBirth(farmerKycDTO.getDateOfBirth());
        farmerKyc.setGender(farmerKycDTO.getGender());
        farmerKyc.setCitizenshipNumber(farmerKycDTO.getCitizenshipNumber());
        farmerKyc.setCitizenshipIssuedDistrict(farmerKycDTO.getCitizenshipIssuedDistrict());
        farmerKyc.setPermanentAddress(farmerKycDTO.getPermanentAddress());
        farmerKyc.setProvince(farmerKycDTO.getProvince());
        farmerKyc.setDistrict(farmerKycDTO.getDistrict());
        farmerKyc.setMunicipality(farmerKycDTO.getMunicipality());
        farmerKyc.setWardNumber(farmerKycDTO.getWardNumber()); // Convert String to Integer
        farmerKyc.setTole(farmerKycDTO.getTole());
        farmerKyc.setRejectionReason(farmerKycDTO.getRejectionReason());
        farmerKyc.setKycStatus("Pending");
        farmerKyc.setVerified(false);
        farmerKyc.setProfileImagePath(farmerKycDTO.getProfileImagePath());

        // Set farm details
        FarmDetails farmDetails = new FarmDetails();
        farmDetails.setFarmName(farmerKycDTO.getFarmName());
        farmDetails.setFarmSize(farmerKycDTO.getFarmSize());
        farmDetails.setFarmSizeUnit(farmerKycDTO.getFarmSizeUnit());
        farmDetails.setPrimaryCrops(farmerKycDTO.getPrimaryCrops());
        farmDetails.setAnnualProductionCapacity(farmerKycDTO.getAnnualProductionCapacity());
        farmerKyc.setFarmDetails(farmDetails);

        // Set experience details
        ExperienceDetails experienceDetails = new ExperienceDetails();
        experienceDetails.setYearsOfExperience(farmerKycDTO.getYearsOfExperience());
        experienceDetails.setFarmingType(farmerKycDTO.getFarmingType());
        experienceDetails.setCertifications(farmerKycDTO.getCertifications());
        farmerKyc.setExperienceDetails(experienceDetails);

        // Set bank details
        BankDetails bankDetails = new BankDetails();
        bankDetails.setEsewaId(farmerKycDTO.getEsewaId());
        bankDetails.setEsewaQrImagePath(farmerKycDTO.getEsewaQrImagePath());
        farmerKyc.setBankDetails(bankDetails);

        farmerRepo.save(farmerKyc);
        return ResponseEntity.ok("KYC Saved Successfully");
    }

    public FarmerKycDTO getFarmerDetails(Long id){
        FarmerKyc farmerKyc = farmerRepo.findByUserId(id);
        return farmerKyc != null ? new FarmerKycDTO(farmerKyc) : null;
    }
}
