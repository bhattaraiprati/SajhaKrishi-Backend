package com.example.sajhaKrishi.DTO.Buyer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyerKycDTO {

    private long id;
    private long userId; // Reference to User entity by ID
    private String fullName;
    private Integer phoneNumber;
    private String email;
    private String dateOfBirth;
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


}
