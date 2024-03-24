package org.setu.splitwise.services;

import org.setu.splitwise.dtos.CreateGroupRequest;
import org.setu.splitwise.dtos.GroupResponse;
import org.setu.splitwise.models.Group;
import org.setu.splitwise.repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    public GroupResponse createGroup(CreateGroupRequest request) {
        Group createdGroup = groupRepository.save(
                Group.builder()
                        .userIds(request.getUserIds())
                        .build()
        );
        return GroupResponse.builder()
                .id(createdGroup.getId())
                .userIds(createdGroup.getUserIds())
                .build();
    }

    public Optional<GroupResponse> getGroupById(Long groupId) {
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
}
