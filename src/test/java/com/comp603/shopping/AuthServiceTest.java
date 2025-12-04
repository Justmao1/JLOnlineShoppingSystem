package com.comp603.shopping;

import com.comp603.shopping.dao.UserDAO;
import com.comp603.shopping.models.User;
import com.comp603.shopping.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

public class AuthServiceTest {

    private AuthService authService;
    private StubUserDAO stubUserDAO;

    @BeforeEach
    public void setUp() {
        stubUserDAO = new StubUserDAO();
        authService = new AuthService(stubUserDAO);
    }

    @Test
    public void testLoginSuccess() {
        // Setup stub data
        stubUserDAO.addUser(new User(1, "testuser", "password", "test@example.com", "CUSTOMER", 100.0));

        boolean result = authService.login("testuser", "password");
        assertTrue(result, "Login should succeed with correct credentials");
        assertNotNull(authService.getCurrentUser(), "Current user should be set after login");
        assertEquals("testuser", authService.getCurrentUser().getUsername());
    }

    @Test
    public void testLoginFailureWrongPassword() {
        stubUserDAO.addUser(new User(1, "testuser", "password", "test@example.com", "CUSTOMER", 100.0));

        boolean result = authService.login("testuser", "wrongpass");
        assertFalse(result, "Login should fail with wrong password");
        assertNull(authService.getCurrentUser(), "Current user should be null after failed login");
    }

    @Test
    public void testLoginFailureUserNotFound() {
        boolean result = authService.login("nonexistent", "password");
        assertFalse(result, "Login should fail for non-existent user");
    }

    @Test
    public void testRegisterSuccess() {
        boolean result = authService.register("newuser", "password", "new@example.com");
        assertTrue(result, "Registration should succeed for new username");

        User registeredUser = stubUserDAO.getUserByUsername("newuser");
        assertNotNull(registeredUser, "User should be saved in DAO");
        assertEquals("newuser", registeredUser.getUsername());
    }

    @Test
    public void testRegisterDuplicate() {
        stubUserDAO.addUser(new User(1, "existing", "password", "test@example.com", "CUSTOMER", 100.0));

        boolean result = authService.register("existing", "newpass", "new@example.com");
        assertFalse(result, "Registration should fail for existing username");
    }

    @Test
    public void testLogout() {
        stubUserDAO.addUser(new User(1, "testuser", "password", "test@example.com", "CUSTOMER", 100.0));
        authService.login("testuser", "password");
        assertNotNull(authService.getCurrentUser());

        authService.logout();
        assertNull(authService.getCurrentUser(), "Current user should be null after logout");
    }

    // --- Stub DAO Implementation ---
    // This inner class mocks the behavior of UserDAO using an in-memory Map
    private static class StubUserDAO extends UserDAO {
        private Map<String, User> users = new HashMap<>();
        private int nextId = 1;

        public void addUser(User user) {
            users.put(user.getUsername(), user);
        }

        @Override
        public User getUserByUsername(String username) {
            return users.get(username);
        }

        @Override
        public boolean createUser(User user) {
            if (users.containsKey(user.getUsername())) {
                return false;
            }
            // Simulate DB auto-increment
            User savedUser = new User(nextId++, user.getUsername(), user.getPassword(), user.getEmail(), user.getRole(),
                    user.getBalance());
            users.put(user.getUsername(), savedUser);
            return true;
        }

        // We only need to override methods used by AuthService
    }
}
