package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController,"itemRepository", itemRepository);
        Item item = new Item();
        item.setId(1L);
        item.setName("Round Widget");
        item.setPrice(BigDecimal.valueOf(2.99));
        item.setDescription("A widget that is round");
        Item item1 = new Item();
        item1.setName("Square Widget");
        item1.setPrice(BigDecimal.valueOf(1.99));
        item1.setDescription("A widget that is square");
        when(itemRepository.findAll()).thenReturn(Collections.singletonList(item));
        when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(item));
        when(itemRepository.findByName("Round Widget")).thenReturn(Collections.singletonList(item));
    }

    @Test
    public void getItems() {
        ResponseEntity<List<Item>> itemResponseEntity = itemController.getItems();
        assertNotNull(itemResponseEntity);
        assertEquals(200, itemResponseEntity.getStatusCodeValue());
        List<Item> itemList = itemResponseEntity.getBody();
        Optional<Item> itemOptional = itemRepository.findById(1L);
        assertNotNull(itemOptional);
        assertEquals(itemList.get(0).getId(), itemOptional.get().getId());
    }

    @Test
    public void findItemsById() {
        ResponseEntity<Item> responseEntity = itemController.getItemById(1L);
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        Item item = responseEntity.getBody();
        assertNotNull(item);
        assertEquals("Round Widget", item.getName());
        assertEquals(Long.valueOf(1), item.getId());
    }

    @Test
    public void findItemByItsName() {
        ResponseEntity<List<Item>> responseEntityList = itemController.getItemsByName("Round Widget");
        assertNotNull(responseEntityList);
        assertEquals(200, responseEntityList.getStatusCodeValue());
        Item item1 = responseEntityList.getBody().get(0);
        assertNotNull(item1);
        assertEquals("Round Widget", item1.getName());
    }
}
