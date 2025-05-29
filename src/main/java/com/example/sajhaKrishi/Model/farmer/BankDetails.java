package com.example.sajhaKrishi.Model.farmer;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class BankDetails {

    private String accountName;
    private String accountNumber;
    private String bankName;
    private String branchName;
    private String panNumber;
    private String panCardImagePath;
    private String esewaId;
    private String khaltiId;
}
