package com.example.sajhaKrishi.Services.farmer;

import com.example.sajhaKrishi.Model.User;
import com.example.sajhaKrishi.Model.farmer.Product;
import com.example.sajhaKrishi.repository.ProductRepository;
import com.example.sajhaKrishi.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;


    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Autowired
    UserRepo userRepo;

    public Product saveProduct(Product product, String email) {
        // Set default values if needed

        User user = userRepo.findByEmail(email);
        if (product.getDate() == null) {
            product.setDate(new Date());
        }
        if (product.getStatus() == null) {
            product.setStatus("Active"); // or whatever default status you want
        }
        if (product.getAvailable() == null) {
            product.setAvailable(true);
        }

        // Associate the product with the user
        product.setUser(user);

        return productRepository.save(product);
    }

    // Method to get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Method to get product by ID
    public Product getProductById(String id) {
        return productRepository.findById(id).orElse(null);
    }

    // Method to update a product
    public Product updateProduct(String id, Product productDetails) {
        Product existingProduct = productRepository.findById(id).orElse(null);
        if (existingProduct != null) {
            // Update all fields except ID and user
            existingProduct.setName(productDetails.getName());
            existingProduct.setCategory(productDetails.getCategory());
            existingProduct.setDescription(productDetails.getDescription());
            existingProduct.setQuantity(productDetails.getQuantity());
            existingProduct.setUnitOfMeasurement(productDetails.getUnitOfMeasurement());
            existingProduct.setPrice(productDetails.getPrice());
            existingProduct.setMinimumOrderQuantity(productDetails.getMinimumOrderQuantity());
            existingProduct.setDiscountPrice(productDetails.getDiscountPrice());
            existingProduct.setDeliveryOption(productDetails.getDeliveryOption());
            existingProduct.setDeliveryTime(productDetails.getDeliveryTime());
            existingProduct.setImagePaths(productDetails.getImagePaths());
            existingProduct.setAvailable(productDetails.getAvailable());
            existingProduct.setHarvestDate(productDetails.getHarvestDate());
            existingProduct.setExpiryDate(productDetails.getExpiryDate());
            existingProduct.setStatus(productDetails.getStatus());

            return productRepository.save(existingProduct);
        }
        return null;
    }

    // Method to delete a product
    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }

    public List<Product> getProductsByUser(User user) {
        return productRepository.findByUser(user);
    }

    public List<Product> getAvailableProducts() {
        return productRepository.findByAvailable(true);
    }
}
