package org.setu.splitwise.dtos.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupResponse {

    @NotNull
    private String id;

    @NotEmpty
    private Set<Long> userIds;
}