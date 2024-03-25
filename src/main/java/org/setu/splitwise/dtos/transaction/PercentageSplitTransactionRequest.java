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
public class PercentageSplitTransactionRequest extends SplitTransactionRequest {

    @NotNull(message = "totalAmountLent cannot be null")
    private Double totalAmountLent;

    @NotEmpty(message = "borrowerIdToPercentage cannot be empty")
    private Map<Long, Double> borrowerIdToPercentage;
}
