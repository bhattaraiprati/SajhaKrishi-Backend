package com.example.sajhaKrishi.DTO.farmer;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ProductDTO {
    private Long id; // Optional for creation, useful for updates

    private Long userId;  // To associate with the User entity by ID

    private LocalDate date;  // Format: YYYY-MM-DD

    private String status;

    private String name;
    private String category;
    private String description;

    private Integer quantity;
    private String unitOfMeasurement;
    private Integer price;
    private Integer minimumOrderQuantity;
    private Integer discountPrice;

    private List<String> deliveryOption;

    private String deliveryTime;

    private List<String> imagePaths;

    private Boolean available;

    private String harvestDate;  // Use "yyyy-MM-dd" string if coming from frontend
    private String expiryDate;
}
