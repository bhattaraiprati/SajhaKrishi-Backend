package com.example.sajhaKrishi.Controller.farmer;

import com.example.sajhaKrishi.DTO.farmer.ProductDTO;
import com.example.sajhaKrishi.Model.User;
import com.example.sajhaKrishi.Model.farmer.Product;
import com.example.sajhaKrishi.Model.order.Order;
import com.example.sajhaKrishi.Services.buyer.OrderService;
import com.example.sajhaKrishi.Services.farmer.ProductService;
import com.example.sajhaKrishi.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", exposedHeaders = "Authorization")
public class ProductController {

    private final ProductService productService;
    private final UserRepo userRepository;
    private  final OrderService orderService;

    @Autowired
    public ProductController(ProductService productService, UserRepo userRepository, OrderService orderService) {
        this.productService = productService;
        this.userRepository = userRepository;
        this.orderService = orderService;
    }
//    @CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", exposedHeaders = "Authorization")
@PostMapping("/farmer/addProduct")
public ResponseEntity<ProductDTO> createProduct(
        @RequestBody ProductDTO productDTO,
        Authentication authentication
) {
    // Get user email from JWT token
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    String email = userDetails.getUsername();

    Product savedProduct = productService.saveProduct(productDTO, email);

    // Convert entity back to DTO
    ProductDTO savedDTO = new ProductDTO();
    savedDTO.setId(savedProduct.getId());
    savedDTO.setUserId(savedProduct.getUser().getId());
    savedDTO.setDate(savedProduct.getDate());
    savedDTO.setStatus(savedProduct.getStatus());
    savedDTO.setName(savedProduct.getName());
    savedDTO.setCategory(savedProduct.getCategory());
    savedDTO.setDescription(savedProduct.getDescription());
    savedDTO.setQuantity(savedProduct.getQuantity());
    savedDTO.setUnitOfMeasurement(savedProduct.getUnitOfMeasurement());
    savedDTO.setPrice(savedProduct.getPrice());
    savedDTO.setMinimumOrderQuantity(savedProduct.getMinimumOrderQuantity());
    savedDTO.setDiscountPrice(savedProduct.getDiscountPrice());
    savedDTO.setDeliveryOption(savedProduct.getDeliveryOption());
    savedDTO.setDeliveryTime(savedProduct.getDeliveryTime());
    savedDTO.setImagePaths(savedProduct.getImagePaths());
    savedDTO.setAvailable(savedProduct.getAvailable());
    savedDTO.setHarvestDate(savedProduct.getHarvestDate());
    savedDTO.setExpiryDate(savedProduct.getExpiryDate());

    return ResponseEntity.ok(savedDTO);
}

    @GetMapping("/api/getAll")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/api/farmer/getByFarmerId")
    public ResponseEntity<List<Product>> getProductByFarmerId(Authentication authentication){

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        List<Product> products = productService.getProductByFarmerId(email);

        return  ResponseEntity.ok(products);
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        Product product = productService.getProductById(id);
        if (product != null) {
            return ResponseEntity.ok(product);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/farmer/products/category/{category}")
    public ResponseEntity<?> getProductByCategory(
            @PathVariable String category,
            Authentication authentication) {

        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();

            List<Product> products = productService.getProductsByCategory(category, email);

            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving products: " + e.getMessage());
        }
    }

    @GetMapping("/farmer/products/status/{status}")
    public ResponseEntity<?> getProductByStatus(
            @PathVariable String status,
            Authentication authentication) {

        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();

            List<Product> products = productService.getProductsByStatus(status, email);

            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving products: " + e.getMessage());
        }
    }

    @PatchMapping("/farmer/product/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id,
                                                 @RequestBody ProductDTO productDetails, Authentication authentication) {

        Product updatedProduct = productService.updateProduct(id, productDetails);
        if (updatedProduct != null) {
            return ResponseEntity.ok(updatedProduct);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/api/farmer/deleteProduct/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-products")
    public ResponseEntity<List<Product>> getUserProducts(@AuthenticationPrincipal User user) {
        List<Product> products = productService.getProductsByUser(user);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Product>> getAvailableProducts() {
        List<Product> products = productService.getAvailableProducts();
        return ResponseEntity.ok(products);
    }


    @GetMapping("/api/farmer/product/{item}")
    public ResponseEntity<?> searchProduct(@PathVariable String item) {
        List<Product> products = productService.getProductBySearch(item);
        if(products.isEmpty()) {  // Check for empty list instead of null
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product Not Found");
        }
        return ResponseEntity.ok(products);
    }




}
