package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    @Override
    public User toModel(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }
        return User.builder()
                .id(userDTO.getId())
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .build();
    }

    @Override
    public List<UserDTO> toListDTO(List<User> modelList) {
        if (modelList == null) {
            return null;
        }
        return modelList.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> toModelList(List<UserDTO> userDTOList) {
        if (userDTOList == null) {
            return null;
        }
        return userDTOList.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }
}
