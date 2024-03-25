package org.setu.splitwise.dtos.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {

    @NotNull(message = "id cannot be null")
    private Long id;

    @NotNull(message = "groupId cannot be null")
    private String groupId;

    @NotEmpty(message = "type cannot be empty")
    private String type;

    @NotNull(message = "lenderId cannot be null")
    private Long lenderId;

    @NotNull(message = "totalAmountLent cannot be null")
    private Double totalAmountLent;

    @NotEmpty(message = "borrowerIdToAmount cannot be empty")
    private Map<Long, Double> borrowerIdToAmount;
}
