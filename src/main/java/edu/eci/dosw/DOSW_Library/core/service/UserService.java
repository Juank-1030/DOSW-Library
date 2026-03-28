package edu.eci.dosw.DOSW_Library.core.service;

import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.repository.UserRepository;
import edu.eci.dosw.DOSW_Library.core.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final List<User> users = new ArrayList<>();

    public UserService() {
        this.userRepository = null;
    }

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(User user) {
        ValidationUtil.validateNotNull(user, "User");
        ValidationUtil.validateNotEmpty(user.getId(), "User ID");
        ValidationUtil.validateNotEmpty(user.getName(), "User Name");

        if (userRepository != null) {
            if (userRepository.existsById(user.getId())) {
                throw new IllegalArgumentException("User already exists with ID: " + user.getId());
            }
            return userRepository.save(user);
        }

        for (User u : users) {
            if (u.getId().equals(user.getId())) {
                throw new IllegalArgumentException("User already exists with ID: " + user.getId());
            }
        }
        users.add(user);
        return user;
    }

    public List<User> getAllUsers() {
        if (userRepository != null) {
            return userRepository.findAll();
        }
        return new ArrayList<>(users);
    }

    public User getUserById(String id) {
        ValidationUtil.validateNotEmpty(id, "User ID");

        if (userRepository != null) {
            return userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
        }

        for (User user : users) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        throw new IllegalArgumentException("User not found with ID: " + id);
    }

    public boolean userExists(String userId) {
        if (userRepository != null) {
            return userRepository.existsById(userId);
        }

        for (User user : users) {
            if (user.getId().equals(userId)) {
                return true;
            }
        }
        return false;
    }
}