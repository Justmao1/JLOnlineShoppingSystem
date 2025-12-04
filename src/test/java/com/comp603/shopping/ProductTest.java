package com.comp603.shopping;

import com.comp603.shopping.models.DigitalProduct;
import com.comp603.shopping.models.PhysicalProduct;
import com.comp603.shopping.models.Product;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {

    @Test
    public void testPhysicalProductCreation() {
        PhysicalProduct p = new PhysicalProduct(1, "Laptop", "Gaming Laptop", 1500.0, 10, 2.5, "img.jpg");

        assertEquals(1, p.getProductId());
        assertEquals("Laptop", p.getName());
        assertEquals(1500.0, p.getPrice());
        assertEquals(10, p.getStockQuantity());
        assertEquals(2.5, p.getWeight(), "Weight should be preserved");
        assertEquals("img.jpg", p.getImagePath());
    }

    @Test
    public void testDigitalProductCreation() {
        DigitalProduct p = new DigitalProduct(2, "E-Book", "PDF", 20.0, 100, "http://download", "ebook.jpg");

        assertEquals(2, p.getProductId());
        assertEquals("E-Book", p.getName());
        assertEquals("http://download", p.getDownloadLink(), "Download link should be preserved");
    }

    @Test
    public void testStockManagement() {
        Product p = new PhysicalProduct(1, "Item", "Desc", 10.0, 5, 1.0, null);

        p.setStockQuantity(3);
        assertEquals(3, p.getStockQuantity());

        p.setStockQuantity(0);
        assertEquals(0, p.getStockQuantity());
    }
}
