package com.example.sajhaKrishi.Model;

import jakarta.persistence.*;

@Entity
public class BuyerKyc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String fullName;
    @Column(nullable = false, unique = true)
    private Integer phoneNumber;
    @Column(nullable = false, unique = true)
    private String email;
    private String DateofBirth;
    private String gender;
    private String profilePhotoPath;
    private String province;
    private String district;
    private  String municipality;
    private Integer ward;
    private String streetAddress;
    private String landmark;

    private String citizenshipNumber;
    private String citizenshipFrontImagePath;
    private String citizenshipBackImagePath;
    private String panNumber;

    private String panCardImagePath;

    private Boolean isBusinessUser;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Integer phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateofBirth() {
        return DateofBirth;
    }

    public void setDateofBirth(String dateofBirth) {
        DateofBirth = dateofBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfilePhotoPath() {
        return profilePhotoPath;
    }

    public void setProfilePhotoPath(String profilePhotoPath) {
        this.profilePhotoPath = profilePhotoPath;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public Integer getWard() {
        return ward;
    }

    public void setWard(Integer ward) {
        this.ward = ward;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getCitizenshipNumber() {
        return citizenshipNumber;
    }

    public void setCitizenshipNumber(String citizenshipNumber) {
        this.citizenshipNumber = citizenshipNumber;
    }

    public String getCitizenshipFrontImagePath() {
        return citizenshipFrontImagePath;
    }

    public void setCitizenshipFrontImagePath(String citizenshipFrontImagePath) {
        this.citizenshipFrontImagePath = citizenshipFrontImagePath;
    }

    public String getCitizenshipBackImagePath() {
        return citizenshipBackImagePath;
    }

    public void setCitizenshipBackImagePath(String citizenshipBackImagePath) {
        this.citizenshipBackImagePath = citizenshipBackImagePath;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getPanCardImagePath() {
        return panCardImagePath;
    }

    public void setPanCardImagePath(String panCardImagePath) {
        this.panCardImagePath = panCardImagePath;
    }

    public Boolean getBusinessUser() {
        return isBusinessUser;
    }

    public void setBusinessUser(Boolean businessUser) {
        isBusinessUser = businessUser;
    }
}

