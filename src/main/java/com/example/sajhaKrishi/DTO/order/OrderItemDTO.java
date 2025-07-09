package com.example.sajhaKrishi.DTO.order;

import lombok.Data;

@Data
public class OrderItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Double price;
    private Integer quantity;
    private String farmName;
    private String location;
    private String imageUrl;
}
