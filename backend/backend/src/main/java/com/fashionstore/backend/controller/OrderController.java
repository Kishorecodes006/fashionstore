package com.fashionstore.backend.controller;

import com.fashionstore.backend.model.*;
import com.fashionstore.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CartRepository cartRepository;

    // GET all orders
    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // GET order by ID
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET orders by user
    @GetMapping("/user/{userId}")
    public List<Order> getUserOrders(@PathVariable Long userId) {
        return orderRepository.findByUserId(userId);
    }

    // POST place order
    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody Map<String, Object> body) {
        try {
            Long userId = Long.valueOf(body.get("userId").toString());
            String address = body.get("address").toString();
            String paymentMode = body.getOrDefault("paymentMode", "COD").toString();
            Double totalAmount = Double.valueOf(body.get("totalAmount").toString());

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Order order = new Order();
            order.setUser(user);
            order.setAddress(address);
            order.setPaymentMode(paymentMode);
            order.setTotalAmount(totalAmount);
            order.setStatus(Order.Status.PENDING);

            // Order Items
            List<Map<String, Object>> items =
                    (List<Map<String, Object>>) body.get("items");
            List<OrderItem> orderItems = new ArrayList<>();

            for (Map<String, Object> item : items) {
                Long productId = Long.valueOf(item.get("productId").toString());
                Integer qty    = Integer.valueOf(item.get("quantity").toString());
                Double price   = Double.valueOf(item.get("price").toString());

                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                // Stock reduce பண்ணு
                product.setStock(product.getStock() - qty);
                productRepository.save(product);

                OrderItem oi = new OrderItem();
                oi.setOrder(order);
                oi.setProduct(product);
                oi.setQuantity(qty);
                oi.setPrice(price);
                orderItems.add(oi);
            }

            order.setItems(orderItems);
            Order saved = orderRepository.save(order);

            // Cart clear பண்ணு
            cartRepository.deleteByUserId(userId);

            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // PUT update status
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return orderRepository.findById(id).map(order -> {
            order.setStatus(Order.Status.valueOf(status));
            return ResponseEntity.ok(orderRepository.save(order));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE order
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        orderRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Order deleted!"));
    }
}