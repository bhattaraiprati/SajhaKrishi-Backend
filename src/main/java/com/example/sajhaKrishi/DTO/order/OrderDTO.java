package com.example.sajhaKrishi.DTO.order;

import lombok.Data;

import java.util.List;

@Data
public class OrderDTO {
    private Long userId;
    private String orderStatus;
    private Double totalAmount;
    private String transactionUuid;
    private DeliveryInfoDTO deliveryInfo;
    private List<OrderItemDTO> items;
    private PaymentDTO payment;
}
