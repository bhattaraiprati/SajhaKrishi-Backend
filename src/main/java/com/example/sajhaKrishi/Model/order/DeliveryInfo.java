package com.example.sajhaKrishi.Model.order;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor

public class DeliveryInfo {
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "street_address", nullable = false)
    private String streetAddress;

    @Column(name = "ward_number")
    private String wardNumber;

    @Column(name = "municipality", nullable = false)
    private String municipality;

    @Column(name = "district", nullable = false)
    private String district;

    @Column(name = "landmark")
    private String landmark;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "alternate_phone_number")
    private String alternatePhoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "delivery_instructions")
    private String deliveryInstructions;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "streetAddress", column = @Column(name = "billing_street_address")),
            @AttributeOverride(name = "municipality", column = @Column(name = "billing_municipality")),
            @AttributeOverride(name = "district", column = @Column(name = "billing_district"))
    })
    private BillingAddress billingAddress;
}
