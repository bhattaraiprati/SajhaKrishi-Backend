package com.example.sajhaKrishi.Model.order;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingAddress {
    @Column(name = "street_address")
    private String streetAddress;

    @Column(name = "municipality")
    private String municipality;

    @Column(name = "district")
    private String district;
}
