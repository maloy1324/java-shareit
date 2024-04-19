package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryUserDaoImpl implements UserDao {

    private final Map<Long, User> users = new HashMap<>();
    private Long nextUserId = 1L;

    @Override
    public User createUser(User user) {
        checkEmailExists(user.getEmail());
        Long userId = nextUserId++;
        user.setId(userId);
        users.put(userId, user);
        return user;
    }

    @Override
    public User getUserById(Long userId) {
        return users.get(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User updateUser(User user) {
        checkEmailExists(user.getEmail(), user.getId()); // Передаем userId для исключения
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    @Override
    public boolean isExists(Long id) {
        return users.containsKey(id);
    }

    private void checkEmailExists(String email, Long userIdToExclude) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email) && !user.getId().equals(userIdToExclude)) {
                throw new ConflictException("Email уже существует.");
            }
        }
    }

    private void checkEmailExists(String email) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email)) {
                throw new ConflictException("Email уже существует.");
            }
        }
    }
}
