package com.fashionstore.backend.controller;

import com.fashionstore.backend.model.*;
import com.fashionstore.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired private CartRepository cartRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;

    // GET cart by user
    @GetMapping("/{userId}")
    public List<CartItem> getCart(@PathVariable Long userId) {
        return cartRepository.findByUserId(userId);
    }

    // POST add to cart
    @PostMapping
    public ResponseEntity<?> addToCart(@RequestBody Map<String, Long> body) {
        Long userId    = body.get("userId");
        Long productId = body.get("productId");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Already in cart → qty increase
        var existing = cartRepository.findByUserIdAndProductId(userId, productId);
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + 1);
            return ResponseEntity.ok(cartRepository.save(item));
        }

        CartItem item = new CartItem();
        item.setUser(user);
        item.setProduct(product);
        item.setQuantity(1);
        return ResponseEntity.ok(cartRepository.save(item));
    }

    // DELETE cart item
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeItem(@PathVariable Long id) {
        cartRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Removed from cart!"));
    }

    // DELETE all cart items for user
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> clearCart(@PathVariable Long userId) {
        cartRepository.deleteByUserId(userId);
        return ResponseEntity.ok(Map.of("message", "Cart cleared!"));
    }
}