package com.example.sajhaKrishi.Model;

import com.example.sajhaKrishi.Model.farmer.FarmerKyc;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private Long number;
    private String email;
    private String password;
    private  String role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("user")
    private BuyerKyc buyerKyc;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("user")
    private FarmerKyc farmerKyc;

    // Helper methods
    public void setBuyerKyc(BuyerKyc buyerKyc) {
        this.buyerKyc = buyerKyc;
        if (buyerKyc != null) {
            buyerKyc.setUser(this);
        }
    }

    public void setFarmerKyc(FarmerKyc farmerKyc) {
        this.farmerKyc = farmerKyc;
        if (farmerKyc != null) {
            farmerKyc.setUser(this);
        }
    }

}