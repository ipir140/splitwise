package org.setu.splitwise.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserNotFoundException extends Exception {

    public UserNotFoundException(List<Long> userIds) {
        super("User(s) with the following IDs not found: " + userIds);
    }

    public UserNotFoundException(Long userId) {
        super("User with the following IDs not found: " + userId);
    }
}
