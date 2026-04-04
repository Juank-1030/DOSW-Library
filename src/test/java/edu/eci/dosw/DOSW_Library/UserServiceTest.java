package edu.eci.dosw.DOSW_Library;

import edu.eci.dosw.DOSW_Library.core.exception.UserNotFoundException;
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
    void shouldGetUserById() throws UserNotFoundException {
        userService.registerUser(new User("U001", "John Doe"));

        User user = userService.getUserById("U001");

        assertEquals("John Doe", user.getName());
    }

    @Test
    void shouldGetUserByEmail() throws UserNotFoundException {
        User user = new User("U001", "John Doe");
        user.setEmail("john@library.com");
        userService.registerUser(user);

        User found = userService.getUserByEmail("john@library.com");

        assertEquals("U001", found.getId());
    }

    @Test
    void shouldUpdateUser() throws UserNotFoundException {
        User original = new User("U001", "John Doe");
        original.setEmail("john@library.com");
        userService.registerUser(original);

        User changes = new User("IGNORED", "John Updated");
        changes.setEmail("john.updated@library.com");

        User updated = userService.updateUser("U001", changes);

        assertEquals("John Updated", updated.getName());
        assertEquals("john.updated@library.com", updated.getEmail());
    }

    @Test
    void shouldDeleteUser() throws UserNotFoundException {
        userService.registerUser(new User("U001", "John Doe"));

        userService.deleteUser("U001");

        assertFalse(userService.existsById("U001"));
        assertEquals(0, userService.getTotalUsers());
    }

    @Test
    void shouldCheckExistsById() {
        userService.registerUser(new User("U001", "John Doe"));
        assertTrue(userService.existsById("U001"));
        assertFalse(userService.existsById("U999"));
    }

    @Test
    void shouldThrowWhenRegisteringDuplicateUser() {
        userService.registerUser(new User("U001", "John"));
        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(new User("U001", "Jane")));
    }

    @Test
    void shouldThrowWhenRegisteringDuplicateEmail() {
        User first = new User("U001", "John");
        first.setEmail("john@library.com");
        userService.registerUser(first);

        User second = new User("U002", "Jane");
        second.setEmail("john@library.com");

        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(second));
    }

    @Test
    void shouldThrowWhenUpdatingWithTakenEmail() {
        User first = new User("U001", "John");
        first.setEmail("john@library.com");
        userService.registerUser(first);

        User second = new User("U002", "Jane");
        second.setEmail("jane@library.com");
        userService.registerUser(second);

        User changes = new User("IGNORED", "Jane Updated");
        changes.setEmail("john@library.com");

        assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser("U002", changes));
    }

    @Test
    void shouldThrowWhenUserByIdNotFound() {
        assertThrows(UserNotFoundException.class,
                () -> userService.getUserById("NONE"));
    }

    @Test
    void shouldThrowWhenUserByEmailNotFound() {
        assertThrows(UserNotFoundException.class,
                () -> userService.getUserByEmail("none@library.com"));
    }
}