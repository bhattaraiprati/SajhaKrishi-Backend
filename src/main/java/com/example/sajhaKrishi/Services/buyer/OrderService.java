package com.example.sajhaKrishi.Services.buyer;

import com.example.sajhaKrishi.DTO.farmer.ProductDTO;
import com.example.sajhaKrishi.DTO.order.*;
import com.example.sajhaKrishi.Model.farmer.Product;
import com.example.sajhaKrishi.Model.order.*;
import com.example.sajhaKrishi.Services.farmer.ProductService;
import com.example.sajhaKrishi.repository.OrderRepository;
import com.example.sajhaKrishi.util.SignatureUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private ProductService productService;

    @Autowired
    private RestTemplate restTemplate;

    private static final String ESEWA_GATEWAY_URL = "https://rc-epay.esewa.com.np";
    private static final String ESEWA_PRODUCT_CODE = "EPAYTEST";
    private static final String SUCCESS_URL = "http://localhost:8080/api/orders/success";
    private static final String FAILURE_URL = "http://localhost:8080/api/orders/failure";

    @Transactional
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


//        logger.info("Creating order for userId: {}, transactionUuid: {}", orderDTO.getUserId(), orderDTO.getTransactionUuid());

        Order repoOrder = orderRepository.save(order);
        // Update product quantities
        updateProductQuantities(orderDTO.getItems());

        return repoOrder;
    }

    private void updateProductQuantities(List<OrderItemDTO> items) {
        for (OrderItemDTO item : items) {
            Product product = productService.getProductById(item.getProductId().toString());
            if (product != null) {
                int newQuantity = product.getQuantity() - item.getQuantity();

                // Update the product quantity
                ProductDTO updateDTO = new ProductDTO();
                updateDTO.setQuantity(newQuantity);

                // If quantity becomes 0, mark as unavailable
                if (newQuantity <= 0) {
                    updateDTO.setAvailable(false);
                    updateDTO.setQuantity(0);
                }

                productService.updateProduct(product.getId().toString(), updateDTO);

                logger.info("Updated product {} quantity from {} to {}",
                        product.getName(), product.getQuantity(), newQuantity);
            }
        }
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

    public List<OrderDTO> getOrderByUserId(Long id) {
        List<Order> orders = orderRepository.findByUserId(id);
        if (orders.isEmpty()) {
            throw new RuntimeException("No orders found for user ID: " + id);
        }
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Order updateOrderByStatus(Long id, OrderDTO orderDTO) {
        Order orderDetails = orderRepository.findById(id).orElse(null);

        if (orderDetails == null) {
            logger.warn("Order not found for ID: {}", id);
            return null;
        }

        try {
            OrderStatus newStatus = OrderStatus.valueOf(orderDTO.getOrderStatus());
//            if (newStatus == OrderStatus.CANCELLED) {
//                throw new IllegalArgumentException("Cannot change status to CANCELED");
//            }
            orderDetails.setOrderStatus(newStatus);
            Order updatedOrder = orderRepository.save(orderDetails);
            logger.info("Order ID: {} status updated to {}", id, newStatus);
            return updatedOrder;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid order status: {} for order ID: {}", orderDTO.getOrderStatus(), id);
            throw new IllegalArgumentException("Invalid order status: " + orderDTO.getOrderStatus());
        }
    }

    public List<Order> filterOrders(Long farmerId, String status, LocalDateTime startDate, LocalDateTime endDate, String searchTerm) {
        logger.info("Filtering orders for farmer ID: {}, status: {}, date range: {} to {}, search: {}",
                farmerId, status, startDate, endDate, searchTerm);

        try {
            if (status != null && !status.isEmpty() && !status.equals("ALL")) {
                return orderRepository.findByFarmerIdAndOrderStatus(farmerId, OrderStatus.valueOf(status));
            }

            if (startDate != null && endDate != null) {
                return orderRepository.findByFarmerIdAndCreatedAtBetween(farmerId, startDate, endDate);
            }

            if (searchTerm != null && !searchTerm.isEmpty()) {
                try {
                    Long orderId = Long.parseLong(searchTerm);
                    return orderRepository.findByFarmerIdAndId(farmerId, orderId);
                } catch (NumberFormatException e) {
                    return orderRepository.findByFarmerIdAndDeliveryInfoFullNameContainingIgnoreCase(farmerId, searchTerm);
                }
            }

            // Default: return all orders for the farmer
            return orderRepository.findByFarmerId(farmerId);
        } catch (Exception e) {
            logger.error("Error filtering orders: {}", e.getMessage());
            throw new RuntimeException("Failed to filter orders: " + e.getMessage());
        }
    }

    public Order getOrderByTransactionUuid(String transactionUuid) {
        logger.info("Fetching order by transactionUuid: {}", transactionUuid);
        return orderRepository.findByTransactionUuid(transactionUuid)
                .orElseThrow(() -> new RuntimeException("Order not found with transactionUuid: " + transactionUuid));
    }


    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setFarmerId(order.getFarmerId());
        dto.setOrderStatus(order.getOrderStatus().name());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setTransactionUuid(order.getTransactionUuid());
        dto.setDeliveryInfo(convertToDeliveryInfoDTO(order.getDeliveryInfo()));
        dto.setItems(order.getItems().stream()
                .map(this::convertToOrderItemDTO)
                .collect(Collectors.toList()));
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

