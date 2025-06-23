package com.example.sajhaKrishi.Model.farmer;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class FarmDetails {

    private String farmName;
    private String gpsCoordinates; // Could be "latitude,longitude"
    private String farmSize; // In Ropani/Hectares
    private String farmSizeUnit; // "Ropani" or "Hectares"
    private String primaryCrops; // Comma-separated values

    private String annualProductionCapacity;
}
