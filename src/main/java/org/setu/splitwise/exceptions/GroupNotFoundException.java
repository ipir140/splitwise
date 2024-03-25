package org.setu.splitwise.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GroupNotFoundException extends Exception {

    public GroupNotFoundException(List<String> groupIds) {
        super("Group(s) with the following IDs not found: " + groupIds);
    }

    public GroupNotFoundException(String groupId) {
        super("Group with the following IDs not found: " + groupId);
    }
}
