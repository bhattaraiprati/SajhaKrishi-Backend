package com.example.sajhaKrishi.DTO;

import com.example.sajhaKrishi.Model.farmer.FarmerKyc;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FarmerKycDTO {
    // Personal & Identity Details

    // Constructor
    public FarmerKycDTO(FarmerKyc farmerKyc) {
        this.id = farmerKyc.getId();
        this.userId = farmerKyc.getUser() != null ? farmerKyc.getUser().getId() : null;

        this.dateOfBirth = farmerKyc.getDateOfBirth();
        this.gender = farmerKyc.getGender();
        this.citizenshipNumber = farmerKyc.getCitizenshipNumber();
        this.citizenshipIssuedDistrict = farmerKyc.getCitizenshipIssuedDistrict();
        this.citizenshipFrontImagePath = farmerKyc.getCitizenshipFrontImagePath();
        this.citizenshipBackImagePath = farmerKyc.getCitizenshipBackImagePath();
        this.permanentAddress = farmerKyc.getPermanentAddress();
        this.province = farmerKyc.getProvince();
        this.district = farmerKyc.getDistrict();
        this.municipality = farmerKyc.getMunicipality();
        this.wardNumber = String.valueOf(farmerKyc.getWardNumber());
        this.tole = farmerKyc.getTole();

        // Farm Details
        if (farmerKyc.getFarmDetails() != null) {
            this.farmName = farmerKyc.getFarmDetails().getFarmName();
            this.gpsCoordinates = farmerKyc.getFarmDetails().getGpsCoordinates();
            this.farmSize = farmerKyc.getFarmDetails().getFarmSize();
            this.farmSizeUnit = farmerKyc.getFarmDetails().getFarmSizeUnit();
            this.primaryCrops = farmerKyc.getFarmDetails().getPrimaryCrops();
            this.annualProductionCapacity = farmerKyc.getFarmDetails().getAnnualProductionCapacity();
        }

        // Experience
        this.yearsOfExperience = farmerKyc.getExperienceDetails().getYearsOfExperience();
        this.farmingType = farmerKyc.getExperienceDetails().getFarmingType();
        this.certifications = farmerKyc.getExperienceDetails().getCertifications();
        this.supportingDocsPath = farmerKyc.getExperienceDetails().getSupportingDocsPath();

        // Bank Details
        this.accountName = farmerKyc.getBankDetails().getAccountName();
        this.accountNumber = farmerKyc.getBankDetails().getAccountNumber();
        this.bankName = farmerKyc.getBankDetails().getBankName();
        this.branchName = farmerKyc.getBankDetails().getBranchName();
        this.panNumber = farmerKyc.getBankDetails().getPanNumber();
        this.panCardImagePath = farmerKyc.getBankDetails().getPanCardImagePath();
        this.esewaId = farmerKyc.getBankDetails().getEsewaId();
        this.khaltiId = farmerKyc.getBankDetails().getKhaltiId();
    }

    private Long id;
    private Long userId;
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
    private String farmName;
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
