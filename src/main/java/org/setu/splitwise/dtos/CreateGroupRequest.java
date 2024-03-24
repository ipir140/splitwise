package org.setu.splitwise.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupRequest {

    @NotEmpty(message = "User IDs cannot be empty")
    private Set<Long> userIds;
}