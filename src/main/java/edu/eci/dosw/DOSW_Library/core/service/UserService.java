package edu.eci.dosw.DOSW_Library.core.service;

import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.util.ValidationUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final List<User> users = new ArrayList<>();

    public User registerUser(User user) {
        ValidationUtil.validateNotNull(user, "User");
        ValidationUtil.validateNotEmpty(user.getId(), "User ID");
        ValidationUtil.validateNotEmpty(user.getName(), "User Name");

        for (User u : users) {
            if (u.getId().equals(user.getId())) {
                throw new IllegalArgumentException("User already exists with ID: " + user.getId());
            }
        }
        users.add(user);
        return user;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public User getUserById(String id) {
        ValidationUtil.validateNotEmpty(id, "User ID");
        for (User user : users) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        throw new IllegalArgumentException("User not found with ID: " + id);
    }

    public boolean userExists(String userId) {
        for (User user : users) {
            if (user.getId().equals(userId)) {
                return true;
            }
        }
        return false;
    }
}