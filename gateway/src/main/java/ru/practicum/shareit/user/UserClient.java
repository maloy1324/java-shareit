package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDTO;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createUser(UserDTO userDTO) {
        String url = "";
        return post(url, userDTO);
    }

    public ResponseEntity<Object> getUserById(Long userId) {
        String url = "/" + userId;
        return get(url);
    }

    public ResponseEntity<Object> getAllUsers() {
        String url = "";
        return get(url);
    }

    public ResponseEntity<Object> updateUser(Long userId, UserDTO userDTO) {
        String url = "/" + userId;
        return patch(url, userDTO);
    }

    public void deleteUser(Long userId) {
        String url = "/" + userId;
        delete(url);
    }
}
