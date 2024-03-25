package org.setu.splitwise.dtos.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SplitTransactionRequest extends BaseTransactionRequest {

    @NotNull(message = "groupId cannot be null")
    private String groupId;
}
