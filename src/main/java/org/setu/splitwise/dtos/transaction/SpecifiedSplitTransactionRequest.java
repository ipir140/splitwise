package org.setu.splitwise.dtos.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SpecifiedSplitTransactionRequest extends SplitTransactionRequest {

    @NotEmpty(message = "borrowerIdToAmount cannot be empty")
    private Map<Long, Double> borrowerIdToAmount;
}
