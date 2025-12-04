package com.comp603.shopping;

import com.comp603.shopping.models.Product;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {

    @Test
    public void testProductCreation() {
        Product p = new Product(1, "Laptop", "Gaming Laptop", 1500.0, 10, "img.jpg", "Electronics", 0);

        assertEquals(1, p.getProductId());
        assertEquals("Laptop", p.getName());
        assertEquals(1500.0, p.getPrice());
        assertEquals(10, p.getStockQuantity());
        assertEquals("Electronics", p.getCategory());
        assertEquals("img.jpg", p.getImagePath());
    }

    @Test
    public void testStockManagement() {
        Product p = new Product(1, "Item", "Desc", 10.0, 5, null, "General", 0);

        p.setStockQuantity(3);
        assertEquals(3, p.getStockQuantity());

        p.setStockQuantity(0);
        assertEquals(0, p.getStockQuantity());
    }
}
