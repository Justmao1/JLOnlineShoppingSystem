package com.comp603.shopping;

import com.comp603.shopping.models.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void testUserConstructorAndGetters() {
        User user = new User(1, "testuser", "password", "test@example.com", "CUSTOMER", 100.0);

        assertEquals(1, user.getUserId());
        assertEquals("testuser", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("CUSTOMER", user.getRole());
        assertEquals(100.0, user.getBalance());
    }

    @Test
    public void testUserSetters() {
        User user = new User();
        user.setUserId(2);
        user.setUsername("newuser");
        user.setPassword("newpass");
        user.setEmail("new@example.com");
        user.setRole("ADMIN");
        user.setBalance(50.0);

        assertEquals(2, user.getUserId());
        assertEquals("newuser", user.getUsername());
        assertEquals("newpass", user.getPassword());
        assertEquals("new@example.com", user.getEmail());
        assertEquals("ADMIN", user.getRole());
        assertEquals(50.0, user.getBalance());
    }
}
