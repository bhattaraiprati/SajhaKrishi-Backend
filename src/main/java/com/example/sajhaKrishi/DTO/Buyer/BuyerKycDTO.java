package com.example.sajhaKrishi.DTO.Buyer;

import com.example.sajhaKrishi.Model.BuyerKyc;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyerKycDTO {

    private long id;
    private long userId;
    private String fullName;
    private Integer phoneNumber;
    private String email;
    private String dateOfBirth; // Fixed typo
    private String gender;
    private String profilePhotoPath;
    private String province;
    private String district;
    private String municipality;
    private Integer ward;
    private String streetAddress;
    private String landmark;
    private String citizenshipNumber;
    private String citizenshipFrontImagePath;
    private String citizenshipBackImagePath;
    private String panNumber;
    private String panCardImagePath;
    private String kycStatus;
    private Boolean verified;

    // Constructor to map BuyerKyc to BuyerKycDTO
    public BuyerKycDTO(BuyerKyc kyc) {
        this.id = kyc.getId();
        this.userId = kyc.getUser() != null ? kyc.getUser().getId() : 0L;
        this.fullName = kyc.getFullName();
        this.phoneNumber = kyc.getPhoneNumber();
        this.email = kyc.getEmail();
        this.dateOfBirth = kyc.getDateofBirth();
        this.gender = kyc.getGender();
        this.profilePhotoPath = kyc.getProfilePhotoPath();
        this.province = kyc.getProvince();
        this.district = kyc.getDistrict();
        this.municipality = kyc.getMunicipality();
        this.ward = kyc.getWard();
        this.streetAddress = kyc.getStreetAddress();
        this.landmark = kyc.getLandmark();
        this.citizenshipNumber = kyc.getCitizenshipNumber();
        this.citizenshipFrontImagePath = kyc.getCitizenshipFrontImagePath();
        this.citizenshipBackImagePath = kyc.getCitizenshipBackImagePath();
        this.panNumber = kyc.getPanNumber();
        this.panCardImagePath = kyc.getPanCardImagePath();
        this.kycStatus = kyc.getKycStatus();
        this.verified = kyc.getVerified();
    }
}