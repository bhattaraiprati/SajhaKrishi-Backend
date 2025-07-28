package com.example.sajhaKrishi.Model.farmer;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class BankDetails {
    private String esewaQrImagePath;
    private String esewaId;
}
