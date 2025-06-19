package com.example.sajhaKrishi.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FarmerKycDTO {
    // Personal & Identity Details
    private String dateOfBirth;
    private String gender;
    private String citizenshipNumber;
    private String citizenshipIssuedDistrict;
    private String citizenshipFrontImagePath;
    private String citizenshipBackImagePath;
    private String permanentAddress;
    private String province;
    private String district;
    private String municipality;
    private String wardNumber;
    private String tole;

    // Farm Details
    private String gpsCoordinates;
    private String farmSize;
    private String farmSizeUnit;
    private String primaryCrops;
    private String seasonalCalendar;
    private String annualProductionCapacity;

    // Experience Details
    private Integer yearsOfExperience;
    private String farmingType;
    private String certifications;
    private String supportingDocsPath;

    // Bank Details
    private String accountName;
    private String accountNumber;
    private String bankName;
    private String branchName;
    private String panNumber;
    private String panCardImagePath;
    private String esewaId;
    private String khaltiId;

}
