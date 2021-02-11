package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;

import static org.mockito.Mockito.mock;

public class OrderControllerTest {

    private OrderController orderController = mock(OrderController.class);

    private OrderRepository orderRepository = mock(OrderRepository.class);

    private UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);

    }
}
