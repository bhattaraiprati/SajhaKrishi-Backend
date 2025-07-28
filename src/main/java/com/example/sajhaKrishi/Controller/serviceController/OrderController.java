package com.example.sajhaKrishi.Controller.serviceController;

import com.example.sajhaKrishi.DTO.order.*;
import com.example.sajhaKrishi.Model.Notification;
import com.example.sajhaKrishi.Model.User;
import com.example.sajhaKrishi.Model.order.*;
import com.example.sajhaKrishi.Services.NotificationService;
import com.example.sajhaKrishi.Services.buyer.OrderService;
import com.example.sajhaKrishi.repository.UserRepo;
import com.example.sajhaKrishi.util.SignatureUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService cartService;
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepo userRepository;

    @PostMapping("/create")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
        logger.info("Creating order for userId: {}", orderDTO.getUserId());
        Order order = cartService.createOrder(orderDTO);

        // Send notification to farmer about new order
        sendNewOrderNotificationToFarmer(order);
        return ResponseEntity.ok(convertToDTO(order));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrdersByOrderId(@PathVariable Long orderId) {
        logger.info("Fetching order by ID: {}", orderId);
        Order order = cartService.getOrderById(orderId);
        return ResponseEntity.ok(convertToDTO(order));
    }

    @GetMapping("/getOrder/{id}")
    public ResponseEntity<?> getOrderByUserId(@PathVariable Long id) {
        List<OrderDTO> orderDTO = cartService.getOrderByUserId(id);
        return ResponseEntity.ok(orderDTO);
    }

    @GetMapping("/getOrder")
    public ResponseEntity<?> getOrderById(Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            User user = userRepository.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(404).body("User not found with email: " + email);
            }
            // Fetch all orders for the user
            List<Order> orders = cartService.getOrdersByUserId(user.getId());
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while fetching orders: " + e.getMessage());
        }
    }

    @PatchMapping("/updateStatus/{id}")
    public ResponseEntity<?> updateOrderByStatus(@PathVariable Long id, @RequestBody OrderDTO orderDTO) {
        try {
            if (orderDTO.getOrderStatus() == null || orderDTO.getOrderStatus().isEmpty()) {
                logger.warn("Order status is required for order ID: {}", id);
                return ResponseEntity.badRequest().body("Order status is required");
            }

            Order updatedOrder = cartService.updateOrderByStatus(id, orderDTO);
            if (updatedOrder == null) {
                logger.warn("Order not found for ID: {}", id);
                return ResponseEntity.notFound().build();
            }

            // Send notification for order status change
            sendOrderStatusNotification(updatedOrder);

            logger.info("Successfully updated status for order ID: {} to {}", id, orderDTO.getOrderStatus());
            return ResponseEntity.ok(updatedOrder);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid order status for order ID: {} - {}", id, e.getMessage());
            return ResponseEntity.badRequest().body("Invalid order status: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating order status for ID: {} - {}", id, e.getMessage(), e);
            return ResponseEntity.status(500).body("Failed to update order status: " + e.getMessage());
        }
    }

    private void sendOrderStatusNotification(Order order) {
        try {
            String title = "Order Status Updated";
            String message = String.format(
                    "Your order #%d status changed to %s",
                    order.getId(),
                    order.getOrderStatus()
            );

            // Create and send notification
            Notification notification = notificationService.createOrderNotification(
                    order.getUserId(),
                    title,
                    message,
                    order.getId()
            );

            logger.info("Sent order status notification for order ID: {}", order.getId());
        } catch (Exception e) {
            logger.error("Error sending order status notification: {}", e.getMessage());
        }
    }


    private void sendNewOrderNotificationToFarmer(Order order) {
        try {
            String title = "New Order Received!";
            String message = String.format(
                    "You have received a new order #%d with total amount Rs. %.2f",
                    order.getId(),
                    order.getTotalAmount()
            );

            // Create and send notification to farmer
            Notification notification = notificationService.createNewOrderNotification(
                    order.getFarmerId(), // Send to farmer, not buyer
                    title,
                    message,
                    order.getId()
            );

            logger.info("Sent new order notification to farmer ID: {} for order ID: {}",
                    order.getFarmerId(), order.getId());
        } catch (Exception e) {
            logger.error("Error sending new order notification to farmer: {}", e.getMessage());
        }
    }
    private String createOrderStatusMessage(Order order) {
        String status = order.getOrderStatus().name();
        switch (status) {
            case "PENDING":
                return "Your order #" + order.getId() + " is now pending and being processed.";
            case "CONFIRMED":
                return "Great news! Your order #" + order.getId() + " has been confirmed.";
            case "SHIPPED":
                return "Your order #" + order.getId() + " has been shipped and is on the way.";
            case "DELIVERED":
                return "Your order #" + order.getId() + " has been delivered successfully.";
            case "CANCELLED":
                return "Your order #" + order.getId() + " has been cancelled.";
            default:
                return "Your order #" + order.getId() + " status has been updated to " + status.toLowerCase() + ".";
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterOrders(
            Authentication authentication,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String search) {

        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            User user = userRepository.findByEmail(email);

            if (user == null) {
                return ResponseEntity.status(404).body("User not found with email: " + email);
            }

            List<Order> filteredOrders = cartService.filterOrders(user.getId(), status, startDate, endDate, search);
            return ResponseEntity.ok(filteredOrders);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while filtering orders: " + e.getMessage());
        }
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