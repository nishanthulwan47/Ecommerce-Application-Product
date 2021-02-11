package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    public static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;


    @PostMapping("/submit/{username}")
    public ResponseEntity<UserOrder> submit(@PathVariable String username) {
        log.info("submit the orders", username);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.info("User not found", username);
            return ResponseEntity.notFound().build();
        }
        UserOrder order = UserOrder.createFromCart(user.getCart());
        if (order == null) {
            log.error("Cart is empty");
            return ResponseEntity.notFound().build();
        }
        orderRepository.save(order);
        Cart cart1 = new Cart();
        cart1.setTotal(BigDecimal.valueOf(0));
        user.setCart(cart1);
        userRepository.save(user);
        log.info("Order has been placed", username);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/history/{username}")
    public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
        log.info("Order history of users", username);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.error("User not found", username);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(orderRepository.findByUser(user));
    }
}
