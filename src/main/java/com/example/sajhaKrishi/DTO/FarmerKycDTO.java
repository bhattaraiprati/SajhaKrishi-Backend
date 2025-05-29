package com.example.sajhaKrishi.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FarmerKycDTO {
    // Page 1
    private String dateOfBirth;
    private String gender;
    private String citizenshipNumber;
    private String citizenshipIssuedDistrict;
    private String permanentAddress;

    // Page 2
    private String gpsCoordinates;
    private Double farmSize;
    private String farmSizeUnit;
    private String primaryCrops;
    private String seasonalCalendar;
    private String annualProductionCapacity;

    // Page 3
    private Integer yearsOfExperience;
    private String farmingType;
    private String associatedCooperatives;
    private String certifications;

    // Page 4
    private String accountName;
    private String accountNumber;
    private String bankName;
    private String branchName;
    private String panNumber;
    private String esewaId;
    private String khaltiId;
}
