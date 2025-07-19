package com.example.sajhaKrishi.Model.farmer;

import com.example.sajhaKrishi.Model.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class FarmerKyc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"farmerKyc", "buyerKyc"})
    private User user;

    // Page 1: Personal & Identity Details
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
    private Integer wardNumber;
    private String tole;


    // Page 2: Farm & Production Details
    @Embedded
    private FarmDetails farmDetails;

    // Page 3: Experience & Certifications
    @Embedded
    private ExperienceDetails experienceDetails;

    // Page 4: Bank & Payment Details
    @Embedded
    private BankDetails bankDetails;

    // New fields for KYC status
    private String kycStatus = "PENDING"; // Default to PENDING
    private Boolean verified = false; // Default to false

}
