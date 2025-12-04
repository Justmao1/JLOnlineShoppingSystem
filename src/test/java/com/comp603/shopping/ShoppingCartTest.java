package com.comp603.shopping;

import com.comp603.shopping.models.Product;
import com.comp603.shopping.models.ShoppingCart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ShoppingCartTest {

    private ShoppingCart cart;
    private Product p1;
    private Product p2;

    @BeforeEach
    public void setUp() {
        cart = new ShoppingCart();
        p1 = new Product(1, "Book", "A book", 10.0, 5, null, "Books", 0);
        p2 = new Product(2, "E-Book", "An ebook", 20.0, 100, null, "Books", 0);
    }

    @Test
    public void testAddProduct() {
        cart.addProduct(p1);
        assertEquals(1, cart.getItems().size());
        assertEquals(10.0, cart.getTotal());
    }

    @Test
    public void testRemoveProduct() {
        cart.addProduct(p1);
        cart.addProduct(p2);
        cart.removeProduct(p1);
        assertEquals(1, cart.getItems().size());
        assertEquals(20.0, cart.getTotal());
    }

    @Test
    public void testTotalCalculation() {
        cart.addProduct(p1); // 10.0
        cart.addProduct(p2); // 20.0
        assertEquals(30.0, cart.getTotal());
    }

    @Test
    public void testClear() {
        cart.addProduct(p1);
        cart.clear();
        assertTrue(cart.getItems().isEmpty());
        assertEquals(0.0, cart.getTotal());
    }
}
