package com.example.sajhaKrishi.DTO.Buyer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDTO {

    private Long id;
    private Long userId;
    private Long farmerId;
    private Long productId;
    private String productName;
    private Double price;

    private Integer discountPercentage;

    private String description;
    private Integer quantity;
    private String imageUrl;
    private String farmName;
    private String location;


}
