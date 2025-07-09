package com.example.sajhaKrishi.DTO.order;

import lombok.Data;

@Data
public class PaymentDTO {
    private String paymentMethod;
    private String paymentStatus;
    private String transactionId;
    private Double amount;
    private String paymentDate;
}
