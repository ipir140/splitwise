package org.setu.splitwise.dtos.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class EqualSplitTransactionRequest extends SplitTransactionRequest {

    @NotNull(message = "totalAmountLent cannot be null")
    private Double totalAmountLent;

    @NotEmpty(message = "borrowers cannot be empty")
    private List<Long> borrowers;
}
