package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController = mock(OrderController.class);

    private OrderRepository orderRepository = mock(OrderRepository.class);

    private UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
        Cart cart = new Cart();
        User user = new User();
        Item item = new Item();

        item.setDescription("Apple Products");
        item.setPrice(BigDecimal.valueOf(100.00));
        item.setName("AirPods Pro");
        item.setId(1L);

        cart.addItem(item);
        user.setId(0);
        user.setUsername("testUser");
        user.setPassword("testPassword");
        user.setCart(cart);

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(userRepository.findByUsername("UserNotFound")).thenReturn(null);
    }

    @Test
    public void SubmitOrderWithUser() {
        ResponseEntity<UserOrder> userOrderResponseEntity = orderController.submit("testUser");
        assertNotNull(userOrderResponseEntity);
        assertEquals(200, userOrderResponseEntity.getStatusCodeValue());
        UserOrder userOrder = userOrderResponseEntity.getBody();
        assertNotNull(userOrder);
        assertEquals(1, userOrder.getItems().size());
    }

    @Test
    public void SubmitOrderWithoutUser() {
        ResponseEntity<UserOrder> userOrderResponseEntity = orderController.submit("UserNotFound");
        assertNotNull(userOrderResponseEntity);
        assertEquals(404, userOrderResponseEntity.getStatusCodeValue());
    }

    @Test
    public void GetOrderForUsers() {
        ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser("testUser");
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        List<UserOrder> userOrderList = responseEntity.getBody();
        assertNotNull(userOrderList);
    }

    @Test
    public void OrdersForNonUsers() {
        ResponseEntity<UserOrder> listResponseEntity = orderController.submit("UserNotFound");
        assertNotNull(listResponseEntity);
        assertEquals(404, listResponseEntity.getStatusCodeValue());
    }
}
