package com.example.sajhaKrishi.Controller.serviceController;

import com.example.sajhaKrishi.DTO.Buyer.CartItemDTO;
import com.example.sajhaKrishi.Services.buyer.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/addToCart")
    public ResponseEntity<CartItemDTO> addToCart(@RequestBody CartItemDTO cartItemDTO) {
        try {
            CartItemDTO addedItem = cartService.addToCartItem(cartItemDTO);
            return ResponseEntity.ok(addedItem);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CartItemDTO>> getCartItems(@PathVariable Long userId) {
        try {
            List<CartItemDTO> cartItems = cartService.getCartItems(userId);
            return ResponseEntity.ok(cartItems);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/update/{itemId}")
    public ResponseEntity<CartItemDTO> updateCartItem(@PathVariable Long itemId,
                                                      @RequestBody CartItemDTO cartItemDTO) {
        try {
            CartItemDTO updatedItem = cartService.updateCartItem(itemId, cartItemDTO);
            return ResponseEntity.ok(updatedItem);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<Void> removeCartItem(@PathVariable Long itemId) {
        try {
            cartService.removeCartItem(itemId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        try {
            cartService.clearCart(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/total/{userId}")
    public ResponseEntity<Double> getCartTotal(@PathVariable Long userId) {
        try {
            double total = cartService.getCartTotal(userId);
            return ResponseEntity.ok(total);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/count/{userId}")
    public ResponseEntity<Integer> getCartItemCount(@PathVariable Long userId) {
        try {
            int count = cartService.getCartItemCount(userId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/checkout/{userId}")
    public ResponseEntity<List<CartItemDTO>> moveToCheckout(@PathVariable Long userId) {
        try {
            List<CartItemDTO> checkoutItems = cartService.moveToCheckout(userId);
            return ResponseEntity.ok(checkoutItems);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/complete/{userId}")
    public ResponseEntity<Void> markAsCompleted(@PathVariable Long userId) {
        try {
            cartService.markAsCompleted(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }



}
