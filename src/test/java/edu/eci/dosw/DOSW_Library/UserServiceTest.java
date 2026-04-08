package edu.eci.dosw.DOSW_Library;

import edu.eci.dosw.DOSW_Library.core.exception.UserNotFoundException;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.repository.UserRepository;
import edu.eci.dosw.DOSW_Library.core.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        User user = new User("U001", "John Doe");
        when(userRepository.existsById("U001")).thenReturn(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.registerUser(user);

        assertNotNull(result);
        assertEquals("U001", result.getId());
    }

    @Test
    void shouldGetAllUsers() {
        User user1 = new User("U001", "John");
        User user2 = new User("U002", "Jane");
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<User> users = userService.getAllUsers();
        assertEquals(2, users.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList());
        assertTrue(userService.getAllUsers().isEmpty());
    }

    @Test
    void shouldGetUserById() throws UserNotFoundException {
        User user = new User("U001", "John Doe");
        when(userRepository.findById("U001")).thenReturn(Optional.of(user));

        User found = userService.getUserById("U001");

        assertEquals("John Doe", found.getName());
    }

    @Test
    void shouldGetUserByEmail() throws UserNotFoundException {
        User user = new User("U001", "John Doe");
        user.setEmail("john@library.com");
        when(userRepository.findByEmail("john@library.com")).thenReturn(Optional.of(user));

        User found = userService.getUserByEmail("john@library.com");

        assertEquals("U001", found.getId());
    }

    @Test
    void shouldUpdateUser() throws UserNotFoundException {
        User original = new User("U001", "John Doe");
        original.setEmail("john@library.com");

        User changes = new User("IGNORED", "John Updated");
        changes.setEmail("john.updated@library.com");

        when(userRepository.findById("U001")).thenReturn(Optional.of(original));
        when(userRepository.findByEmail("john.updated@library.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = userService.updateUser("U001", changes);

        assertEquals("John Updated", updated.getName());
        assertEquals("john.updated@library.com", updated.getEmail());
    }

    @Test
    void shouldDeleteUser() throws UserNotFoundException {
        User user = new User("U001", "John Doe");
        when(userRepository.findById("U001")).thenReturn(Optional.of(user));

        userService.deleteUser("U001");

        // Verificar que se llamó a deleteById
    }

    @Test
    void shouldCheckExistsById() {
        when(userRepository.existsById("U001")).thenReturn(true);
        when(userRepository.existsById("U999")).thenReturn(false);

        assertTrue(userService.existsById("U001"));
        assertFalse(userService.existsById("U999"));
    }

    @Test
    void shouldThrowWhenRegisteringDuplicateUser() {
        User user = new User("U001", "John");
        when(userRepository.existsById("U001")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(user));
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