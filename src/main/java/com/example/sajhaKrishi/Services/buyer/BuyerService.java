package com.example.sajhaKrishi.Services.buyer;

import com.example.sajhaKrishi.DTO.Buyer.BuyerKycDTO;
import com.example.sajhaKrishi.Model.BuyerKyc;
import com.example.sajhaKrishi.Model.User;
import com.example.sajhaKrishi.repository.BuyerKycRepo;
import com.example.sajhaKrishi.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class BuyerService {

    @Autowired(required = true)
    private BuyerKycRepo kycRepo;

    @Autowired
    private UserRepo userRepo;



    public ResponseEntity<?> KycRegistration(BuyerKycDTO kycDTO){
        try{
            // Convert DTO to Entity
            BuyerKyc kyc = convertDTOToEntity(kycDTO);

            kycRepo.save(kyc);
            return ResponseEntity.ok("Successfully register kyc form");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    private BuyerKyc convertDTOToEntity(BuyerKycDTO dto) {
        BuyerKyc kyc = new BuyerKyc();

        // Set all fields from DTO to Entity
        kyc.setId(dto.getId());
        kyc.setFullName(dto.getFullName());
        kyc.setPhoneNumber(dto.getPhoneNumber());
        kyc.setEmail(dto.getEmail());
        kyc.setDateofBirth(dto.getDateOfBirth()); // Note: keeping original field name from entity
        kyc.setGender(dto.getGender());
        kyc.setProfilePhotoPath(dto.getProfilePhotoPath());
        kyc.setProvince(dto.getProvince());
        kyc.setDistrict(dto.getDistrict());
        kyc.setMunicipality(dto.getMunicipality());
        kyc.setWard(dto.getWard());
        kyc.setStreetAddress(dto.getStreetAddress());
        kyc.setLandmark(dto.getLandmark());
        kyc.setCitizenshipNumber(dto.getCitizenshipNumber());
        kyc.setCitizenshipFrontImagePath(dto.getCitizenshipFrontImagePath());
        kyc.setCitizenshipBackImagePath(dto.getCitizenshipBackImagePath());
        kyc.setPanNumber(dto.getPanNumber());
        kyc.setPanCardImagePath(dto.getPanCardImagePath());

        // Fetch and set User entity
        if (dto.getUserId() != 0) {
            User user = userRepo.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + dto.getUserId()));
            kyc.setUser(user);
        }

        return kyc;
    }


}
