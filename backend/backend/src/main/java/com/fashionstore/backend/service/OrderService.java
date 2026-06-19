package com.fashionstore.backend.service;

import com.fashionstore.backend.model.*;
import com.fashionstore.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CartRepository cartRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found!"));
    }

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order placeOrder(Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        String address = body.get("address").toString();
        String paymentMode = body.getOrDefault("paymentMode", "COD").toString();
        Double totalAmount = Double.valueOf(body.get("totalAmount").toString());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setPaymentMode(paymentMode);
        order.setTotalAmount(totalAmount);
        order.setStatus(Order.Status.PENDING);

        List<Map<String, Object>> items =
                (List<Map<String, Object>>) body.get("items");
        List<OrderItem> orderItems = new ArrayList<>();

        for (Map<String, Object> item : items) {
            Long productId = Long.valueOf(item.get("productId").toString());
            Integer qty    = Integer.valueOf(item.get("quantity").toString());
            Double price   = Double.valueOf(item.get("price").toString());

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found!"));

            // Stock reduce
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

        // Cart clear
        cartRepository.deleteByUserId(userId);

        return saved;
    }

    public Order updateStatus(Long id, String status) {
        Order order = getById(id);
        order.setStatus(Order.Status.valueOf(status));
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}