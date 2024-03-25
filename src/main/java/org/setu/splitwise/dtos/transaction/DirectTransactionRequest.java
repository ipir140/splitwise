package org.setu.splitwise.dtos.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class DirectTransactionRequest extends BaseTransactionRequest {

    @NotNull(message = "totalAmountLent cannot be null")
    private Double totalAmountLent;

    @NotEmpty(message = "borrowerId cannot be empty")
    private Long borrowerId;
}
