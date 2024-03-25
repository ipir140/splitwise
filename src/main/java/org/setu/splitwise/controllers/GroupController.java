package org.setu.splitwise.controllers;

import org.setu.splitwise.dtos.group.CreateGroupRequest;
import org.setu.splitwise.dtos.group.GroupResponse;
import org.setu.splitwise.dtos.transaction.BaseTransactionRequest;
import org.setu.splitwise.dtos.transaction.TransactionResponse;
import org.setu.splitwise.exceptions.GroupNotFoundException;
import org.setu.splitwise.exceptions.UserNotFoundException;
import org.setu.splitwise.services.GroupService;
import org.setu.splitwise.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<?> createGroup(@Valid CreateGroupRequest request, BindingResult result) {
        if (result.hasErrors()) {
            // If request validation fails, return bad request response with error details
            return new ResponseEntity<>(result.getAllErrors(), HttpStatus.BAD_REQUEST);
        }

        try {
            GroupResponse groupResponse = groupService.createGroup(request);
            return new ResponseEntity<>(groupResponse, HttpStatus.CREATED);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupResponse> getGroupById(@PathVariable String groupId) {
        Optional<GroupResponse> groupOptional = groupService.getGroupById(groupId);
        return groupOptional.map(groupResponse -> new ResponseEntity<>(groupResponse, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GroupResponse>> getGroupsOfUser(@PathVariable Long userId) {
        List<GroupResponse> groups = groupService.getGroupsOfUser(userId);
        return ResponseEntity.ok(groups);
    }

    @PostMapping("/record")
    public ResponseEntity<?> recordTransaction(@Valid BaseTransactionRequest request, BindingResult result) {
        if (result.hasErrors()) {
            // If request validation fails, return bad request response with error details
            return new ResponseEntity<>(result.getAllErrors(), HttpStatus.BAD_REQUEST);
        }

        try {
            TransactionResponse transactionResponse = transactionService.recordTransaction(request);
            return new ResponseEntity<>(transactionResponse, HttpStatus.ACCEPTED);
        } catch (UserNotFoundException | GroupNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
