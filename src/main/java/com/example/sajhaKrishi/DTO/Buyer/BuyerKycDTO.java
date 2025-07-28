package com.example.sajhaKrishi.DTO.Buyer;

import com.example.sajhaKrishi.Model.BuyerKyc;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyerKycDTO {

    public BuyerKycDTO() {
    }

    private long id;
    private long userId;
    private String fullName;
    private String phoneNumber;
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
    private String panNumber;
    private String businessRegistrationImagePath;
    private String kycStatus;
    private Boolean verified;

    // Constructor to map BuyerKyc to BuyerKycDTO
    public BuyerKycDTO(BuyerKyc kyc) {
        this.id = kyc.getId();
        this.userId = kyc.getUser() != null ? kyc.getUser().getId() : 0L;
        this.fullName = kyc.getFullName();
        this.phoneNumber = kyc.getPhoneNumber();
        this.email = kyc.getEmail();
        this.dateOfBirth = kyc.getDateOfBirth();
        this.gender = kyc.getGender();
        this.profilePhotoPath = kyc.getProfilePhotoPath();
        this.province = kyc.getProvince();
        this.district = kyc.getDistrict();
        this.municipality = kyc.getMunicipality();
        this.ward = kyc.getWard();
        this.streetAddress = kyc.getStreetAddress();
        this.landmark = kyc.getLandmark();
        this.citizenshipNumber = kyc.getCitizenshipNumber();
        this.businessRegistrationImagePath = kyc.getBusinessRegistrationImagePath();
        this.panNumber = kyc.getPanNumber();
        this.kycStatus = kyc.getKycStatus();
        this.verified = kyc.getVerified();
    }

}