package com.example.sajhaKrishi.Services.farmer;

import com.example.sajhaKrishi.DTO.farmer.ProductDTO;
import com.example.sajhaKrishi.DTO.order.OrderDTO;
import com.example.sajhaKrishi.Model.User;
import com.example.sajhaKrishi.Model.farmer.Product;
import com.example.sajhaKrishi.Services.buyer.OrderService;
import com.example.sajhaKrishi.repository.ProductRepository;
import com.example.sajhaKrishi.repository.UserRepo;
import lombok.extern.flogger.Flogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final ProductRepository productRepository;


    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Autowired
    UserRepo userRepo;

    public Product saveProduct(ProductDTO productDTO, String email) {
        // Get user from email (extracted from JWT token)
        User user = userRepo.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Map DTO to Entity
        Product product = new Product();
        product.setUser(user);
        product.setDate(productDTO.getDate() != null ? productDTO.getDate() : LocalDate.now()
        );
        product.setStatus(productDTO.getStatus() != null ? productDTO.getStatus() : "Active");
        product.setAvailable(productDTO.getAvailable() != null ? productDTO.getAvailable() : true);
        product.setName(productDTO.getName());
        product.setCategory(productDTO.getCategory());
        product.setDescription(productDTO.getDescription());
        product.setQuantity(productDTO.getQuantity());
        product.setUnitOfMeasurement(productDTO.getUnitOfMeasurement());
        product.setPrice(productDTO.getPrice());
        product.setMinimumOrderQuantity(productDTO.getMinimumOrderQuantity());
        product.setDiscountPrice(productDTO.getDiscountPrice());
        product.setDeliveryOption(productDTO.getDeliveryOption());
        product.setDeliveryTime(productDTO.getDeliveryTime());
        product.setImagePaths(productDTO.getImagePaths());
        product.setHarvestDate(productDTO.getHarvestDate());
        product.setExpiryDate(productDTO.getExpiryDate());

        return productRepository.save(product);
    }

    // Method to get all products
    public List<Product> getAllProducts() {
        String status = "Active";

        List<Product> products = productRepository.findAllByStatus(status);
        List<Product> availableProducts = new ArrayList<>();

        for (Product product : products) {
            // Check if available quantity is less than minimum order quantity
            if (product.getQuantity() < product.getMinimumOrderQuantity()) {
                // Update status to "Pause" and set available to false
                product.setStatus("Pause");
                product.setAvailable(false);
                productRepository.save(product);

                logger.info("Product {} status changed to Pause due to insufficient quantity. " +
                                "Available: {}, Minimum required: {}",
                        product.getName(), product.getQuantity(), product.getMinimumOrderQuantity());
            } else {
                // Only add products that meet minimum quantity requirements
                availableProducts.add(product);
            }
        }

        return availableProducts;
    }

    // Method to get product by ID
    public Product getProductById(String id) {
        return productRepository.findById(id).orElse(null);
    }

    public List<Product> getProductByFarmerId( String email){

        User user = userRepo.findByEmail(email);
        return productRepository.findByUser(user);
    }

    // Method to update a product
    public Product updateProduct(String id, ProductDTO productDetails) {
        Product existingProduct = productRepository.findById(id).orElse(null);
        if (existingProduct != null) {

            // Validate status change before applying updates
            if (productDetails.getStatus() != null) {
                validateStatusChange(existingProduct, productDetails.getStatus(), productDetails);
            }

            // Only update fields that are not null
            if (productDetails.getName() != null) {
                existingProduct.setName(productDetails.getName());
            }
            if (productDetails.getCategory() != null) {
                existingProduct.setCategory(productDetails.getCategory());
            }
            if (productDetails.getDescription() != null) {
                existingProduct.setDescription(productDetails.getDescription());
            }
            if (productDetails.getQuantity() != null) {
                existingProduct.setQuantity(productDetails.getQuantity());
            }
            if (productDetails.getUnitOfMeasurement() != null) {
                existingProduct.setUnitOfMeasurement(productDetails.getUnitOfMeasurement());
            }
            if (productDetails.getPrice() != null) {
                existingProduct.setPrice(productDetails.getPrice());
            }
            if (productDetails.getMinimumOrderQuantity() != null) {
                existingProduct.setMinimumOrderQuantity(productDetails.getMinimumOrderQuantity());
            }
            if (productDetails.getDiscountPrice() != null) {
                existingProduct.setDiscountPrice(productDetails.getDiscountPrice());
            }
            if (productDetails.getDeliveryOption() != null) {
                existingProduct.setDeliveryOption(productDetails.getDeliveryOption());
            }
            if (productDetails.getDeliveryTime() != null) {
                existingProduct.setDeliveryTime(productDetails.getDeliveryTime());
            }
            if (productDetails.getImagePaths() != null) {
                existingProduct.setImagePaths(productDetails.getImagePaths());
            }
            if (productDetails.getAvailable() != null) {
                existingProduct.setAvailable(productDetails.getAvailable());
            }
            if (productDetails.getHarvestDate() != null) {
                existingProduct.setHarvestDate(productDetails.getHarvestDate());
            }
            if (productDetails.getExpiryDate() != null) {
                existingProduct.setExpiryDate(productDetails.getExpiryDate());
            }
            if (productDetails.getStatus() != null) {
                existingProduct.setStatus(productDetails.getStatus());
            }

            // Only auto-update status if status wasn't manually set
            if (productDetails.getStatus() == null) {
                checkAndUpdateProductStatus(existingProduct);
            }

            return productRepository.save(existingProduct);
        }
        return null;
    }

    private void validateStatusChange(Product existingProduct, String newStatus, ProductDTO productDetails) {
        // Check if trying to activate a product
        if ("Active".equalsIgnoreCase(newStatus) &&
                (!"Active".equalsIgnoreCase(existingProduct.getStatus()))) {

            // Get the current quantity (use updated quantity if provided, otherwise existing)
            Integer currentQuantity = productDetails.getQuantity() != null ?
                    productDetails.getQuantity() : existingProduct.getQuantity();

            // Get the minimum order quantity (use updated value if provided, otherwise existing)
            Integer minimumOrderQuantity = productDetails.getMinimumOrderQuantity() != null ?
                    productDetails.getMinimumOrderQuantity() : existingProduct.getMinimumOrderQuantity();

            // Validate quantity requirements
            if (currentQuantity < minimumOrderQuantity) {
                throw new IllegalArgumentException(
                        String.format("Cannot activate product '%s'. Available quantity (%d) is less than minimum order quantity (%d). " +
                                        "Please increase the available quantity to at least %d or decrease the minimum order quantity to %d or less.",
                                existingProduct.getName(), currentQuantity, minimumOrderQuantity, minimumOrderQuantity, currentQuantity)
                );
            }
        }
    }

    private void checkAndUpdateProductStatus(Product product) {
        // If quantity is less than minimum order quantity, pause the product
        if (product.getQuantity() < product.getMinimumOrderQuantity()) {
            product.setStatus("Pause");
            product.setAvailable(false);

            logger.info("Product {} automatically paused due to insufficient quantity. " +
                            "Available: {}, Minimum required: {}",
                    product.getName(), product.getQuantity(), product.getMinimumOrderQuantity());
        }
        // If quantity is sufficient and product was paused due to quantity, reactivate it
        else if (product.getQuantity() >= product.getMinimumOrderQuantity() &&
                "Pause".equals(product.getStatus())) {
            product.setStatus("Active");
            product.setAvailable(true);

            logger.info("Product {} reactivated due to sufficient quantity. " +
                            "Available: {}, Minimum required: {}",
                    product.getName(), product.getQuantity(), product.getMinimumOrderQuantity());
        }
    }
    // Method to delete a product
    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }

    public Integer getProductCount(Long id){
        Integer productCount = productRepository.countById(id);

        return productCount;
    }



    public List<Product> getProductsByUser(User user) {
        return productRepository.findByUser(user);
    }

    public List<Product> getAvailableProducts() {
        return productRepository.findByAvailable(true);
    }

    public List<Product> getProductsByCategory(String category, String email) {
        User farmer = userRepo.findByEmail(email);
        if(farmer == null){
            new RuntimeException("User not found");
        }

        return "All".equalsIgnoreCase(category)
                ? productRepository.findByUser(farmer)
                : productRepository.findByUserIdAndCategory(farmer.getId(), category);
    }

    public List<Product> getProductsByStatus(String status, String email) {
        User farmer = userRepo.findByEmail(email);
        if(farmer == null){
            new RuntimeException("User not found");
        }

        return "All".equalsIgnoreCase(status)
                ? productRepository.findByUser(farmer)
                : productRepository.findByUserIdAndStatus(farmer.getId(), status);
    }

    public List<Product> getProductBySearch(String item) {
        // Using the same search term for both name and category
        return productRepository.findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(item, item);
    }
}
