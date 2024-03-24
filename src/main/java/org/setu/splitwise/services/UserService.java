package org.setu.splitwise.services;

import org.setu.splitwise.dtos.CreateUserRequest;
import org.setu.splitwise.dtos.GetUserResponse;
import org.setu.splitwise.dtos.UserResponse;
import org.setu.splitwise.models.User;
import org.setu.splitwise.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserResponse createUser(CreateUserRequest createUserRequest) {
        User createdUser = userRepository.save(
                User.builder()
                        .name(createUserRequest.getName())
                        .build()
        );

        return UserResponse.builder()
                .id(createdUser.getId())
                .name(createUserRequest.getName())
                .build();
    }

    public Optional<UserResponse> findById(Long id) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return Optional.empty();
        }
        return Optional.of(
                UserResponse.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .build()
        );
    }

}
