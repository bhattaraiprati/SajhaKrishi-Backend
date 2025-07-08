package com.example.sajhaKrishi.Services.buyer;

import com.example.sajhaKrishi.DTO.Buyer.CartItemDTO;
import com.example.sajhaKrishi.Model.buyer.CartItem;
import com.example.sajhaKrishi.Model.buyer.CartStatus;
import com.example.sajhaKrishi.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    public CartItemDTO addToCartItem(CartItemDTO cartItemDTO) {
        // Check if item already exists in cart for the user
        List<CartItem> existingItems = cartRepository.findByUserId(cartItemDTO.getUserId());

        Optional<CartItem> existingItem = existingItems.stream()
                .filter(item -> item.getProductId().equals(cartItemDTO.getProductId())
                        && item.getStatus() == CartStatus.ACTIVE)
                .findFirst();

        CartItem cartItem;

        if (existingItem.isPresent()) {
            // Update quantity if item already exists
            cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + cartItemDTO.getQuantity());
        } else {
            // Create new cart item
            cartItem = CartItem.builder()
                    .userId(cartItemDTO.getUserId())
                    .productId(cartItemDTO.getProductId())
                    .productName(cartItemDTO.getProductName())
                    .price(cartItemDTO.getPrice())
                    .description(cartItemDTO.getDescription())
                    .quantity(cartItemDTO.getQuantity())
                    .imageUrl(cartItemDTO.getImageUrl())
                    .farmName(cartItemDTO.getFarmName())
                    .location(cartItemDTO.getLocation())
                    .status(CartStatus.ACTIVE)
                    .build();
        }

        CartItem savedItem = cartRepository.save(cartItem);
        return convertToDTO(savedItem);
    }

    public List<CartItemDTO> getCartItems(Long userId) {
        List<CartItem> cartItems = cartRepository.findByUserId(userId);
        return cartItems.stream()
                .filter(item -> item.getStatus() == CartStatus.ACTIVE)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CartItemDTO updateCartItem(Long itemId, CartItemDTO cartItemDTO) {
        Optional<CartItem> optionalCartItem = cartRepository.findById(itemId);

        if (optionalCartItem.isPresent()) {
            CartItem cartItem = optionalCartItem.get();
            cartItem.setQuantity(cartItemDTO.getQuantity());
//            cartItem.setPrice(cartItemDTO.getPrice());

            CartItem updatedItem = cartRepository.save(cartItem);
            return convertToDTO(updatedItem);
        }

        throw new RuntimeException("Cart item not found with id: " + itemId);
    }

    public void removeCartItem(Long itemId) {
        Optional<CartItem> optionalCartItem = cartRepository.findById(itemId);

        if (optionalCartItem.isPresent()) {
            cartRepository.deleteById(itemId);
        } else {
            throw new RuntimeException("Cart item not found with id: " + itemId);
        }
    }

    public void clearCart(Long userId) {
        List<CartItem> cartItems = cartRepository.findByUserId(userId);
        cartItems.forEach(item -> {
            if (item.getStatus() == CartStatus.ACTIVE) {
                cartRepository.delete(item);
            }
        });
    }

    public double getCartTotal(Long userId) {
        List<CartItem> cartItems = cartRepository.findByUserId(userId);
        return cartItems.stream()
                .filter(item -> item.getStatus() == CartStatus.ACTIVE)
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    public int getCartItemCount(Long userId) {
        List<CartItem> cartItems = cartRepository.findByUserId(userId);
        return cartItems.stream()
                .filter(item -> item.getStatus() == CartStatus.ACTIVE)
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public List<CartItemDTO> moveToCheckout(Long userId) {
        List<CartItem> cartItems = cartRepository.findByUserId(userId);

        cartItems.forEach(item -> {
            if (item.getStatus() == CartStatus.ACTIVE) {
                item.setStatus(CartStatus.CHECKOUT);
                cartRepository.save(item);
            }
        });

        return cartItems.stream()
                .filter(item -> item.getStatus() == CartStatus.CHECKOUT)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void markAsCompleted(Long userId) {
        List<CartItem> cartItems = cartRepository.findByUserId(userId);

        cartItems.forEach(item -> {
            if (item.getStatus() == CartStatus.CHECKOUT) {
                item.setStatus(CartStatus.COMPLETED);
                cartRepository.save(item);
            }
        });
    }

    private CartItemDTO convertToDTO(CartItem cartItem) {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(cartItem.getId());
        dto.setUserId(cartItem.getUserId());
        dto.setProductId(cartItem.getProductId());
        dto.setProductName(cartItem.getProductName());
        dto.setPrice(cartItem.getPrice());
        dto.setDescription(cartItem.getDescription());
        dto.setQuantity(cartItem.getQuantity());
        dto.setImageUrl(cartItem.getImageUrl());
        dto.setFarmName(cartItem.getFarmName());
        dto.setLocation(cartItem.getLocation());
        return dto;
    }
}
