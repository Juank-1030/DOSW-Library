package edu.eci.dosw.DOSW_Library;

import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    // ========== ESCENARIOS EXITOSOS ==========

    @Test
    void shouldRegisterUserSuccessfully() {
        User user = userService.registerUser(new User("U001", "John Doe"));
        assertNotNull(user);
        assertEquals("U001", user.getId());
    }

    @Test
    void shouldGetAllUsers() {
        userService.registerUser(new User("U001", "John"));
        userService.registerUser(new User("U002", "Jane"));
        List<User> users = userService.getAllUsers();
        assertEquals(2, users.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoUsers() {
        assertTrue(userService.getAllUsers().isEmpty());
    }

    @Test
    void shouldGetUserById() {
        userService.registerUser(new User("U001", "John Doe"));
        User user = userService.getUserById("U001");
        assertEquals("John Doe", user.getName());
    }

    @Test
    void shouldCheckUserExists() {
        userService.registerUser(new User("U001", "John Doe"));
        assertTrue(userService.userExists("U001"));
        assertFalse(userService.userExists("U999"));
    }

    // ========== ESCENARIOS DE ERROR ==========

    @Test
    void shouldThrowWhenRegisteringNullUser() {
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(null));
    }

    @Test
    void shouldThrowWhenRegisteringUserWithNullId() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(new User(null, "John")));
    }

    @Test
    void shouldThrowWhenRegisteringUserWithEmptyName() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(new User("U001", "")));
    }

    @Test
    void shouldThrowWhenRegisteringDuplicateUser() {
        userService.registerUser(new User("U001", "John"));
        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(new User("U001", "Jane")));
    }

    @Test
    void shouldThrowWhenGettingUserByNonExistentId() {
        assertThrows(IllegalArgumentException.class, () -> userService.getUserById("NONE"));
    }

    @Test
    void shouldThrowWhenGettingUserByNullId() {
        assertThrows(IllegalArgumentException.class, () -> userService.getUserById(null));
    }

    @Test
    void shouldThrowWhenGettingUserByEmptyId() {
        assertThrows(IllegalArgumentException.class, () -> userService.getUserById(""));
    }
}