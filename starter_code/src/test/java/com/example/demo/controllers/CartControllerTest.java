package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class CartControllerTest {

    private CartController cartController = mock(CartController.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private ItemRepository itemRepository = mock(ItemRepository.class);

    private UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
        Cart cart = new Cart();
        User user = new User();
        user.setId(0);
        user.setUsername("testUser");
        user.setPassword("testPassword");
        user.setCart(cart);
        when(userRepository.findByUsername("testUser")).thenReturn(user);

        Item item = new Item();
        item.setId(0L);
        item.setName("Square Widget");
        item.setPrice(BigDecimal.valueOf(1.99));
        item.setDescription("A widget that is square");
        when(itemRepository.findById(0L)).thenReturn(java.util.Optional.of(item));
        when(itemRepository.findByName("Square Widget")).thenReturn(Collections.singletonList(item));
    }

    @Test
    public void addToCartTest() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(0L);
        modifyCartRequest.setQuantity(2);
        modifyCartRequest.setUsername("testUser");
        ResponseEntity<Cart> cartResponseEntity = cartController.addTocart(modifyCartRequest);

        assertNotNull(cartResponseEntity);
        assertEquals(200, cartResponseEntity.getStatusCodeValue());

        Cart cart = cartResponseEntity.getBody();
        assertNotNull(cart);
        Optional<Item> itemOptional = itemRepository.findById(0L);
        BigDecimal bigDecimalTotal = itemOptional.get().getPrice().multiply(BigDecimal.valueOf(2));
        assertEquals(bigDecimalTotal, cart.getTotal());
    }

    @Test
    public void deleteFromCart() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("testUser");
        modifyCartRequest.setItemId(0L);
        modifyCartRequest.setQuantity(2);
        ResponseEntity<Cart> cartResponseEntity = cartController.addTocart(modifyCartRequest);
        assertNotNull(cartResponseEntity);
        assertEquals(200, cartResponseEntity.getStatusCodeValue());
        Cart cart = cartResponseEntity.getBody();
        assertNotNull(cart);
        ResponseEntity<Cart> cartResponseEntity1 = cartController.removeFromcart(modifyCartRequest);
        assertEquals(cartResponseEntity1.getStatusCodeValue(), HttpStatus.OK.value());
        assertEquals(cart.getItems().size(), 0);
    }

    @Test
    public void showItemsInCart() {
        ResponseEntity<Cart> cartResponseEntity = cartController.showItemsInCart("unknown");
        assertNotNull(cartResponseEntity);
        assertEquals(404, cartResponseEntity.getStatusCodeValue());
        cartResponseEntity = cartController.showItemsInCart("testUser");
        assertNotNull(cartResponseEntity);
        assertEquals(200, cartResponseEntity.getStatusCodeValue());
    }
}
