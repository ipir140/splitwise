package org.setu.splitwise.services;

import org.setu.splitwise.dtos.group.CreateGroupRequest;
import org.setu.splitwise.dtos.group.GroupBalanceResponse;
import org.setu.splitwise.dtos.group.GroupResponse;
import org.setu.splitwise.dtos.group.CreateInternalGroupRequest;
import org.setu.splitwise.exceptions.UserNotFoundException;
import org.setu.splitwise.models.Group;
import org.setu.splitwise.repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public GroupResponse createGroup(CreateGroupRequest request) throws UserNotFoundException {
        List<Long> nonExistingUserIds = userService.getAllNonExistingUserIds(new ArrayList<>(request.getUserIds()));
        if (nonExistingUserIds.size() > 0) {
            throw new UserNotFoundException(nonExistingUserIds);
        }

        Group createRequest = Group.builder()
                .id(UUID.randomUUID().toString())
                .build();
        createRequest.setUserIds(request.getUserIds().stream().collect(Collectors.toSet()));
        System.out.println(createRequest.toString());
        Group createdGroup = groupRepository.save(createRequest);
        System.out.println(createdGroup.toString());
        return GroupResponse.builder()
                .id(createdGroup.getId())
                .userIds(createdGroup.getUserIds())
                .build();
    }

    @Transactional
    public GroupResponse createInternalGroup(CreateInternalGroupRequest request) throws UserNotFoundException {
        List<Long> nonExistingUserIds = userService.getAllNonExistingUserIds(new ArrayList<>(request.getUserIds()));
        if (nonExistingUserIds.size() > 0) {
            throw new UserNotFoundException(nonExistingUserIds);
        }

        Group createRequest = Group.builder()
                .id(request.getId())
                .build();
        createRequest.setUserIds(request.getUserIds().stream().collect(Collectors.toSet()));
        Group createdGroup = groupRepository.save(createRequest);

        return GroupResponse.builder()
                .id(createdGroup.getId())
                .userIds(createdGroup.getUserIds())
                .build();
    }

    public Optional<GroupResponse> getGroupById(String groupId) {
        Optional<Group> group = groupRepository.findById(groupId);
        if (group.isPresent()) {
            return Optional.of(
                    GroupResponse.builder()
                            .id(group.get().getId())
                            .userIds(group.get().getUserIds())
                            .build()
            );
        }
        return Optional.empty();
    }

    public List<GroupResponse> getGroupsOfUser(Long userId) {
        List<Group> allGroups = groupRepository.findAll();
//        List<Group> groups = groupRepository.findAllByUserIdsContains(userId);

        return allGroups.stream()
                .filter(group -> group.getUserIds().contains(userId))
                .map(group -> new GroupResponse(group.getId(), group.getUserIds()))
                .collect(Collectors.toList());
    }

    public GroupResponse getOrCreateGroup(CreateInternalGroupRequest request) throws UserNotFoundException {
        Optional<GroupResponse> groupById = getGroupById(request.getId());

        if (groupById.isPresent()) {
            return groupById.get();
        }
        return createInternalGroup(request);
    }

    public CreateInternalGroupRequest getCreateInternalGroupRequest(List<Long> userIds) {
        String internalGroupId = userIds.stream().sorted().map(Object::toString).collect(Collectors.joining("_"));
        return CreateInternalGroupRequest.builder()
                .id(internalGroupId)
                .userIds(userIds.stream().collect(Collectors.toList()))
                .build();
    }
}
