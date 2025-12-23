package pl.edu.pjwstk.byt;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AdminTest {

    @Test
    void shouldCreateAdminUser() {
        Admin admin = new Admin("admin1", "admin@example.com");

        assertNotNull(admin);
        assertEquals("admin1", admin.getUsername());
        assertTrue(admin.isActive());
    }

    @Test
    void shouldAccessDashboard() {
        Admin admin = new Admin("admin1", "admin@example.com");
        assertDoesNotThrow(admin::accessDashboard);
    }

    @Test
    void shouldGrantAndRevokeAccess() {
        Admin admin = new Admin("admin1", "admin@example.com");
        User targetUser = new RegularUser("target", "target@example.com");

        // Initial state
        assertTrue(targetUser.isActive());

        // Revoke
        admin.revokeAccess(targetUser);
        assertFalse(targetUser.isActive(), "User should be inactive after revoke");

        // Grant
        admin.grantAccess(targetUser);
        assertTrue(targetUser.isActive(), "User should be active after grant");
    }

    @Test
    void shouldHandleNullUserSafely() {
        Admin admin = new Admin("admin1", "admin@example.com");
        assertDoesNotThrow(() -> admin.grantAccess(null));
        assertDoesNotThrow(() -> admin.revokeAccess(null));
    }
}
