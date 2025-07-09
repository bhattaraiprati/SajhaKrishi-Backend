package com.example.sajhaKrishi.Controller.serviceController;

import com.example.sajhaKrishi.DTO.order.*;
import com.example.sajhaKrishi.Model.order.*;
import com.example.sajhaKrishi.Services.buyer.OrderService;
import com.example.sajhaKrishi.util.SignatureUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService cartService;

    @PostMapping("/create")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
        logger.info("Creating order for userId: {}", orderDTO.getUserId());
        Order order = cartService.createOrder(orderDTO);
        return ResponseEntity.ok(convertToDTO(order));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
        logger.info("Fetching order by ID: {}", orderId);
        Order order = cartService.getOrderById(orderId);
        return ResponseEntity.ok(convertToDTO(order));
    }

    @PostMapping("/initiate-esewa")
    public ResponseEntity<Object> initiateEsewaPayment(@RequestBody OrderDTO orderDTO) {
        logger.info("Initiating eSewa payment for userId: {}", orderDTO.getUserId());
        Object paymentRequest = cartService.initiateEsewaPayment(orderDTO);
        return ResponseEntity.ok(paymentRequest);
    }

    @GetMapping("/success")
    public ResponseEntity<Void> handleEsewaSuccess(@RequestParam(value = "data", required = false) String data) {
        logger.info("Received eSewa success redirect with data: {}", data);
        try {
            if (data == null) {
                logger.error("Missing data parameter");
                return ResponseEntity.status(302)
                        .header("Location", "http://localhost:5173/order-confirmation?status=failed&error=missing_data")
                        .build();
            }

            // Decode Base64 data
            String decodedData = new String(Base64.getDecoder().decode(data));
            logger.info("Decoded eSewa data: {}", decodedData);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> responseMap = mapper.readValue(decodedData, Map.class);
            logger.info("Parsed eSewa response: {}", responseMap);

            String transactionUuid = responseMap.get("transaction_uuid");
            String totalAmount = responseMap.get("total_amount");
            String transactionCode = responseMap.get("transaction_code");
            String status = responseMap.get("status");
            String productCode = responseMap.get("product_code");
            String signedFieldNames = responseMap.get("signed_field_names");
            String receivedSignature = responseMap.get("signature");
            logger.error("Here is the signature",receivedSignature);
            if (transactionUuid == null || totalAmount == null || transactionCode == null ||
                    status == null || productCode == null || signedFieldNames == null || receivedSignature == null) {
                logger.error("Invalid eSewa data: missing fields in {}", responseMap);
                return ResponseEntity.status(302)
                        .header("Location", "http://localhost:5173/order-confirmation?status=failed&error=invalid_data")
                        .build();
            }

            // Verify signature
            String expectedSignature = SignatureUtil.generateSignature(
                    transactionCode, status, totalAmount, transactionUuid, productCode, signedFieldNames);
            if (!receivedSignature.equals(expectedSignature)) {
                logger.error("Invalid signature: received={}, expected={}", receivedSignature, expectedSignature);
                return ResponseEntity.status(302)
                        .header("Location", "http://localhost:5173/order-confirmation?status=failed&error=invalid_signature")
                        .build();
            }

            boolean isVerified = cartService.verifyEsewaPayment(transactionUuid, totalAmount, productCode, transactionCode);
            String redirectUrl = isVerified
                    ? String.format("http://localhost:5173/order-confirmation?status=success&orderId=%s",
                    cartService.getOrderByTransactionUuid(transactionUuid).getId())
                    : "http://localhost:5173/order-confirmation?status=failed&error=verification_failed";
            logger.info("Redirecting to: {}", redirectUrl);
            return ResponseEntity.status(302)
                    .header("Location", redirectUrl)
                    .build();
        } catch (Exception e) {
            logger.error("Error processing eSewa success: {}", e.getMessage(), e);
            return ResponseEntity.status(302)
                    .header("Location", "http://localhost:5173/order-confirmation?status=failed&error=" + e.getMessage())
                    .build();
        }
    }

    @GetMapping("/failure")
    public ResponseEntity<Void> handleEsewaFailure() {
        logger.info("Handling eSewa failure redirect");
        return ResponseEntity.status(302)
                .header("Location", "http://localhost:5173/order-confirmation?status=failed")
                .build();
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setUserId(order.getUserId());
        dto.setOrderStatus(order.getOrderStatus().name());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setTransactionUuid(order.getTransactionUuid());
        dto.setDeliveryInfo(convertToDeliveryInfoDTO(order.getDeliveryInfo()));
        dto.setItems(order.getItems().stream().map(this::convertToOrderItemDTO).collect(Collectors.toList()));
        dto.setPayment(convertToPaymentDTO(order.getPayment()));
        return dto;
    }

    private DeliveryInfoDTO convertToDeliveryInfoDTO(DeliveryInfo deliveryInfo) {
        DeliveryInfoDTO dto = new DeliveryInfoDTO();
        dto.setFullName(deliveryInfo.getFullName());
        dto.setStreetAddress(deliveryInfo.getStreetAddress());
        dto.setWardNumber(deliveryInfo.getWardNumber());
        dto.setMunicipality(deliveryInfo.getMunicipality());
        dto.setDistrict(deliveryInfo.getDistrict());
        dto.setLandmark(deliveryInfo.getLandmark());
        dto.setPhoneNumber(deliveryInfo.getPhoneNumber());
        dto.setAlternatePhoneNumber(deliveryInfo.getAlternatePhoneNumber());
        dto.setEmail(deliveryInfo.getEmail());
        dto.setDeliveryInstructions(deliveryInfo.getDeliveryInstructions());
        dto.setBillingAddress(convertToBillingAddressDTO(deliveryInfo.getBillingAddress()));
        return dto;
    }

    private BillingAddressDTO convertToBillingAddressDTO(BillingAddress billingAddress) {
        BillingAddressDTO dto = new BillingAddressDTO();
        dto.setStreetAddress(billingAddress.getStreetAddress());
        dto.setMunicipality(billingAddress.getMunicipality());
        dto.setDistrict(billingAddress.getDistrict());
        return dto;
    }

    private OrderItemDTO convertToOrderItemDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(item.getProductId());
        dto.setProductName(item.getProductName());
        dto.setPrice(item.getPrice());
        dto.setQuantity(item.getQuantity());
        dto.setFarmName(item.getFarmName());
        dto.setLocation(item.getLocation());
        dto.setImageUrl(item.getImageUrl());
        return dto;
    }

    private PaymentDTO convertToPaymentDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setPaymentStatus(payment.getPaymentStatus().name());
        dto.setTransactionId(payment.getTransactionId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate() != null
                ? payment.getPaymentDate().format(DateTimeFormatter.ISO_DATE_TIME)
                : null);
        return dto;
    }
}