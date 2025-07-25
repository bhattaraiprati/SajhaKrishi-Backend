package com.example.sajhaKrishi.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BuyerKyc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")  // This is the only mapping needed for the foreign key
    @JsonIgnoreProperties({"farmerKyc", "buyerKyc"})
    private User user;


    private String fullName;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false, unique = true)
    private String email;

    @NotNull
    @Column(unique = true)
    private String DateofBirth;

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

    // New fields for KYC status
    private String kycStatus = "PENDING"; // Default to PENDING
    private Boolean verified = false; // Default to false
}

