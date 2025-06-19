package com.example.sajhaKrishi.Model.farmer;

import com.example.sajhaKrishi.Model.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({
            "name", "number", "email", "password", "role",
            "buyerKyc", "farmerKyc"  // Ignore all user details except id
    })
    private User user;

    private LocalDate date;
    private String status;

    private String name;
    private String category;
    private String description;
    private Integer quantity;
    private String unitOfMeasurement;
    private Integer price;
    private Integer minimumOrderQuantity;
    private Integer discountPrice;
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "product_delivery_options",
            joinColumns = @JoinColumn(name = "product_id")
    )
    @Column(name = "delivery_option")
    private List<String> deliveryOption;

    private String deliveryTime;
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "product_images",
            joinColumns = @JoinColumn(name = "product_id")
    )
    private List<String> imagePaths;
    private Boolean available;  // true if available for sale
    private String harvestDate;   // when it was harvested (for freshness)
    private String expiryDate;


}
