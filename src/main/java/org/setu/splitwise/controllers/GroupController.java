package org.setu.splitwise.controllers;

import org.setu.splitwise.dtos.CreateGroupRequest;
import org.setu.splitwise.dtos.GroupResponse;
import org.setu.splitwise.services.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @PostMapping
    public ResponseEntity<?> createGroup(@Valid CreateGroupRequest request, BindingResult result) {
        if (result.hasErrors()) {
            // If request validation fails, return bad request response with error details
            return new ResponseEntity<>(result.getAllErrors(), HttpStatus.BAD_REQUEST);
        }

        GroupResponse groupResponse = groupService.createGroup(request);
        return new ResponseEntity<>(groupResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupResponse> getGroupById(@PathVariable Long groupId) {
        Optional<GroupResponse> groupOptional = groupService.getGroupById(groupId);
        return groupOptional.map(group -> new ResponseEntity<>(group, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
