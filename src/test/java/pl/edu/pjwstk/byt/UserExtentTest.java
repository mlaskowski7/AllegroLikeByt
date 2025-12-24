package pl.edu.pjwstk.byt;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserExtentTest {

    private static final String EXTENT_FILE = "User_extent.ser";

    @BeforeEach
    @AfterEach
    void cleanUp() throws Exception {
        // Clear extent via reflection or just rely on new check
        // Ideally we should clear the static list.
        var field = User.class.getDeclaredField("extent");
        field.setAccessible(true);
        ((List<?>) field.get(null)).clear();

        File file = new File(EXTENT_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void shouldPersistRegularUsers() throws Exception {
        RegularUser user1 = new RegularUser("u1", "u1@e.com");
        RegularUser user2 = new RegularUser("u2", "u2@e.com");

        assertEquals(2, User.getExtent().size());

        User.saveExtent();

        // Clear memory
        var field = User.class.getDeclaredField("extent");
        field.setAccessible(true);
        ((List<?>) field.get(null)).clear();
        assertEquals(0, User.getExtent().size());

        // Load
        User.loadExtent();

        List<User> loaded = User.getExtent();
        assertEquals(2, loaded.size());
        assertTrue(loaded.stream().anyMatch(u -> u.getUsername().equals("u1")));
    }

    @Test
    void shouldPersistMixedUserTypes() throws Exception {
        new RegularUser("reg", "reg@e.com");
        new Admin("adm", "adm@e.com");
        new AnalystAdmin("ana", "ana@e.com");

        assertEquals(3, User.getExtent().size());
        User.saveExtent();

        var field = User.class.getDeclaredField("extent");
        field.setAccessible(true);
        ((List<?>) field.get(null)).clear();

        User.loadExtent();

        List<User> loaded = User.getExtent();
        assertEquals(3, loaded.size());
        assertEquals(1, loaded.stream().filter(u -> u instanceof RegularUser).count());
        assertEquals(1, loaded.stream().filter(u -> u.getClass().equals(Admin.class)).count()); // Exact match
        assertEquals(1, loaded.stream().filter(u -> u instanceof AnalystAdmin).count());
    }
}
