package org.setu.splitwise.services;

import org.setu.splitwise.dtos.user.CreateUserRequest;
import org.setu.splitwise.dtos.user.UserResponse;
import org.setu.splitwise.models.User;
import org.setu.splitwise.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
        Optional<User> userOptional = userRepository.findById(id);

        return userOptional.map(user -> Optional.of(UserResponse.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .build())
                ).orElseGet(() -> Optional.empty());
    }

    public List<Long> getAllNonExistingUserIds(List<Long> userIds) {
        List<Long> existingUserIds = userRepository.findIdsByIdIn(userIds);
        userIds.removeAll(existingUserIds);
        return userIds;
    }
}
