package org.setu.splitwise.dtos.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInternalGroupRequest extends CreateGroupRequest {

    @NotEmpty(message = "Id cannot be empty")
    private String id;
}
