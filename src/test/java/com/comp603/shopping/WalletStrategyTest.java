package com.comp603.shopping;

import com.comp603.shopping.models.User;
import com.comp603.shopping.services.WalletStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Mocking UserDAO would be ideal here, but for simplicity we test the logic that relies on User object state
public class WalletStrategyTest {

    private WalletStrategy walletStrategy;
    private StubUserDAO stubUserDAO;
    private User user;

    @BeforeEach
    public void setUp() {
        stubUserDAO = new StubUserDAO();
        user = new User(1, "testuser", "password", "test@example.com", "CUSTOMER", 100.0);
        // Simulate user existing in DB
        stubUserDAO.updateUserBalance(1, 100.0);

        walletStrategy = new WalletStrategy(user, stubUserDAO);
    }

    @Test
    public void testPaySuccess() {
        boolean result = walletStrategy.pay(50.0);

        assertTrue(result, "Payment should succeed with sufficient balance");
        assertEquals(50.0, user.getBalance(), "User object balance should be updated locally");
        assertEquals(50.0, stubUserDAO.getBalance(1), "DB balance should be updated");
    }

    @Test
    public void testPayFailure() {
        boolean result = walletStrategy.pay(150.0);

        assertFalse(result, "Payment should fail with insufficient balance");
        assertEquals(100.0, user.getBalance(), "User balance should remain unchanged");
        assertEquals(100.0, stubUserDAO.getBalance(1), "DB balance should remain unchanged");
    }

    // Stub DAO for testing
    private static class StubUserDAO extends com.comp603.shopping.dao.UserDAO {
        private java.util.Map<Integer, Double> balances = new java.util.HashMap<>();

        @Override
        public boolean updateUserBalance(int userId, double newBalance) {
            balances.put(userId, newBalance);
            return true;
        }

        // Helper for test verification
        public double getBalance(int userId) {
            return balances.getOrDefault(userId, 0.0);
        }
    }
}
