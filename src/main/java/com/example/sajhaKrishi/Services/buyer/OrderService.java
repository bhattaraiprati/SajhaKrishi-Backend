package com.example.sajhaKrishi.Services.buyer;

import com.example.sajhaKrishi.DTO.order.DeliveryInfoDTO;
import com.example.sajhaKrishi.DTO.order.OrderDTO;
import com.example.sajhaKrishi.DTO.order.OrderItemDTO;
import com.example.sajhaKrishi.DTO.order.PaymentDTO;
import com.example.sajhaKrishi.Model.order.*;
import com.example.sajhaKrishi.repository.OrderRepository;
import com.example.sajhaKrishi.util.SignatureUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String ESEWA_GATEWAY_URL = "https://rc-epay.esewa.com.np";
    private static final String ESEWA_PRODUCT_CODE = "EPAYTEST";
    private static final String SUCCESS_URL = "http://localhost:8080/api/orders/success";
    private static final String FAILURE_URL = "http://localhost:8080/api/orders/failure";

    public Order createOrder(OrderDTO orderDTO) {
        Order order = Order.builder()
                .userId(orderDTO.getUserId())
                .farmerId(orderDTO.getFarmerId())
                .orderStatus(OrderStatus.valueOf(orderDTO.getOrderStatus()))
                .totalAmount(orderDTO.getTotalAmount())
                .transactionUuid(orderDTO.getTransactionUuid())
                .deliveryInfo(convertToDeliveryInfo(orderDTO.getDeliveryInfo()))
                .items(convertToOrderItems(orderDTO.getItems()))
                .payment(convertToPayment(orderDTO.getPayment()))
                .build();
        logger.info("Creating order for userId: {}, transactionUuid: {}", orderDTO.getUserId(), orderDTO.getTransactionUuid());
        return orderRepository.save(order);
    }

    public List<Order> getOrdersByUserId(Long userId) {
        logger.info("Fetching orders for user ID: {}", userId);
        try {
            return orderRepository.findByFarmerId(userId);
        } catch (Exception e) {
            logger.error("Error fetching orders for user ID: {}. Error: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to fetch orders for user ID: " + userId, e);
        }
    }

    public Order getOrderById(Long orderId) {
        logger.info("Fetching order by ID: {}", orderId);
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
    }

    public Order getOrderByTransactionUuid(String transactionUuid) {
        logger.info("Fetching order by transactionUuid: {}", transactionUuid);
        return orderRepository.findByTransactionUuid(transactionUuid)
                .orElseThrow(() -> new RuntimeException("Order not found with transactionUuid: " + transactionUuid));
    }

    public Object initiateEsewaPayment(OrderDTO orderDTO) {
        String transactionUuid = UUID.randomUUID().toString();
        orderDTO.setTransactionUuid(transactionUuid);
        logger.info("Initiating eSewa payment with transaction_uuid: {}", transactionUuid);
        logger.info("Received totalAmount: {}", orderDTO.getTotalAmount());

        Order order = createOrder(orderDTO);
        String totalAmount = String.format("%.2f", orderDTO.getTotalAmount()); // Use two decimal places
        String signedFieldNames = "total_amount,transaction_uuid,product_code";
        String signature = SignatureUtil.generateSignature(totalAmount, transactionUuid, ESEWA_PRODUCT_CODE);

        EsewaPaymentRequest paymentRequest = new EsewaPaymentRequest(
                totalAmount,
                "0",
                totalAmount,
                transactionUuid,
                ESEWA_PRODUCT_CODE,
                "0",
                "0",
                SUCCESS_URL,
                FAILURE_URL,
                signedFieldNames,
                signature,
                order.getId()
        );

        logger.info("eSewa payment request payload: {}", paymentRequest);
        return paymentRequest;
    }

    public boolean verifyEsewaPayment(String transactionUuid, String totalAmount, String productCode, String transactionCode) {
        String formattedTotalAmount = String.format("%.2f", Double.parseDouble(totalAmount)); // Ensure two decimal places
        String url = String.format("%s/api/epay/transaction/status/?product_code=%s&total_amount=%s&transaction_uuid=%s",
                ESEWA_GATEWAY_URL, productCode, formattedTotalAmount, transactionUuid);
        logger.info("Verifying eSewa payment with URL: {}", url);

        try {
            EsewaStatusResponse response = restTemplate.getForObject(url, EsewaStatusResponse.class);
            logger.info("eSewa status response: {}", response);
            if (response == null) {
                logger.error("Null response from eSewa status API");
                return false;
            }
            if ("COMPLETE".equals(response.getStatus())) {
                logger.info("Payment verified for transactionUuid: {}", transactionUuid);
                Order order = orderRepository.findByTransactionUuid(transactionUuid)
                        .orElseThrow(() -> new RuntimeException("Order not found"));
                order.getPayment().setTransactionId(transactionCode);
                order.getPayment().setPaymentStatus(PaymentStatus.COMPLETED);
                order.setOrderStatus(OrderStatus.PENDING);
                orderRepository.save(order);
                return true;
            }
            logger.warn("Payment not complete, status: {}", response.getStatus());
            return false;
        } catch (Exception e) {
            logger.error("Error verifying eSewa payment: {}", e.getMessage(), e);
            return false;
        }
    }

    public void markAsCompleted(Long userId) {
        logger.info("Marking cart as completed for userId: {}", userId);
    }

    private DeliveryInfo convertToDeliveryInfo(DeliveryInfoDTO dto) {
        return new DeliveryInfo(
                dto.getFullName(),
                dto.getStreetAddress(),
                dto.getWardNumber(),
                dto.getMunicipality(),
                dto.getDistrict(),
                dto.getLandmark(),
                dto.getPhoneNumber(),
                dto.getAlternatePhoneNumber(),
                dto.getEmail(),
                dto.getDeliveryInstructions(),
                new BillingAddress(
                        dto.getBillingAddress().getStreetAddress(),
                        dto.getBillingAddress().getMunicipality(),
                        dto.getBillingAddress().getDistrict()
                )
        );
    }

    private List<OrderItem> convertToOrderItems(List<OrderItemDTO> dtos) {
        return dtos.stream().map(dto -> new OrderItem(
                dto.getProductId(),
                dto.getProductName(),
                dto.getPrice(),
                dto.getQuantity(),
                dto.getFarmName(),
                dto.getLocation(),
                dto.getImageUrl()
        )).collect(Collectors.toList());
    }

    private Payment convertToPayment(PaymentDTO dto) {
        return new Payment(
                dto.getPaymentMethod(),
                PaymentStatus.valueOf(dto.getPaymentStatus()),
                dto.getTransactionId(),
                dto.getAmount(),
                dto.getPaymentDate() != null
                        ? LocalDateTime.parse(dto.getPaymentDate(), DateTimeFormatter.ISO_DATE_TIME)
                        : null
        );
    }
}

class EsewaPaymentRequest {
    private String amount;
    private String tax_amount;
    private String total_amount;
    private String transaction_uuid;
    private String product_code;
    private String product_service_charge;
    private String product_delivery_charge;
    private String success_url;
    private String failure_url;
    private String signed_field_names;
    private String signature;
    private Long orderId;

    public EsewaPaymentRequest(String amount, String tax_amount, String total_amount,
                               String transaction_uuid, String product_code,
                               String product_service_charge, String product_delivery_charge,
                               String success_url, String failure_url,
                               String signed_field_names, String signature, Long orderId) {
        this.amount = amount;
        this.tax_amount = tax_amount;
        this.total_amount = total_amount;
        this.transaction_uuid = transaction_uuid;
        this.product_code = product_code;
        this.product_service_charge = product_service_charge;
        this.product_delivery_charge = product_delivery_charge;
        this.success_url = success_url;
        this.failure_url = failure_url;
        this.signed_field_names = signed_field_names;
        this.signature = signature;
        this.orderId = orderId;
    }

    public String getAmount() { return amount; }
    public String getTax_amount() { return tax_amount; }
    public String getTotal_amount() { return total_amount; }
    public String getTransaction_uuid() { return transaction_uuid; }
    public String getProduct_code() { return product_code; }
    public String getProduct_service_charge() { return product_service_charge; }
    public String getProduct_delivery_charge() { return product_delivery_charge; }
    public String getSuccess_url() { return success_url; }
    public String getFailure_url() { return failure_url; }
    public String getSigned_field_names() { return signed_field_names; }
    public String getSignature() { return signature; }
    public Long getOrderId() { return orderId; }

    @Override
    public String toString() {
        return "EsewaPaymentRequest{" +
                "amount='" + amount + '\'' +
                ", tax_amount='" + tax_amount + '\'' +
                ", total_amount='" + total_amount + '\'' +
                ", transaction_uuid='" + transaction_uuid + '\'' +
                ", product_code='" + product_code + '\'' +
                ", product_service_charge='" + product_service_charge + '\'' +
                ", product_delivery_charge='" + product_delivery_charge + '\'' +
                ", success_url='" + success_url + '\'' +
                ", failure_url='" + failure_url + '\'' +
                ", signed_field_names='" + signed_field_names + '\'' +
                ", signature='" + signature + '\'' +
                ", orderId=" + orderId +
                '}';
    }
}

class EsewaStatusResponse {
    private String status;
    private String transaction_code;
    private String total_amount;
    private String transaction_uuid;
    private String product_code;
    private String success_url;
    private String ref_id;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTransaction_code() { return transaction_code; }
    public void setTransaction_code(String transaction_code) { this.transaction_code = transaction_code; }
    public String getTotal_amount() { return total_amount; }
    public void setTotal_amount(String total_amount) { this.total_amount = total_amount; }
    public String getTransaction_uuid() { return transaction_uuid; }
    public void setTransaction_uuid(String transaction_uuid) { this.transaction_uuid = transaction_uuid; }
    public String getProduct_code() { return product_code; }
    public void setProduct_code(String product_code) { this.product_code = product_code; }
    public String getSuccess_url() { return success_url; }
    public void setSuccess_url(String success_url) { this.success_url = success_url; }
    public String getRef_id() { return ref_id; }
    public void setRef_id(String ref_id) { this.ref_id = ref_id; }

    @Override
    public String toString() {
        return "EsewaStatusResponse{status='" + status + "', transaction_code='" + transaction_code +
                "', total_amount='" + total_amount + "', transaction_uuid='" + transaction_uuid +
                "', product_code='" + product_code + "', ref_id='" + ref_id + "'}";
    }
}