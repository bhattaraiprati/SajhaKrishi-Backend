package com.example.sajhaKrishi.Model.farmer;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class ExperienceDetails {

    private Integer yearsOfExperience;
    private String farmingType; // "Organic", "Traditional", etc.
    private String certifications;
}
