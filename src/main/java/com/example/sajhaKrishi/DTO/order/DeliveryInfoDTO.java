package com.example.sajhaKrishi.DTO.order;

import lombok.Data;

@Data
public class DeliveryInfoDTO {
    private String fullName;
    private String streetAddress;
    private String wardNumber;
    private String municipality;
    private String district;
    private String landmark;
    private String phoneNumber;
    private String alternatePhoneNumber;
    private String email;
    private String deliveryInstructions;
    private BillingAddressDTO billingAddress;
}