package org.setu.splitwise.dtos.transaction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static org.setu.splitwise.Utils.Constants.DIRECT;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class DirectTransactionRequest extends BaseTransactionRequest {

    @NotNull(message = "lenderId cannot be null")
    private Long lenderId;

    @NotNull(message = "totalAmountLent cannot be null")
    private Double totalAmountLent;

    @NotNull(message = "borrowerId cannot be empty")
    private Long borrowerId;

    private Long timestamp;

    @JsonCreator // Indicates that this method should be used for deserialization
    public static DirectTransactionRequest create(@JsonProperty("lenderId") Long lenderId,
                                                  @JsonProperty("totalAmountLent") Double totalAmountLent,
                                                  @JsonProperty("borrowerId") Long borrowerId,
                                                  @JsonProperty("timestamp") Long timestamp) {
        return DirectTransactionRequest.builder()
                .type(DIRECT) // Set the type implicitly to "DIRECT"
                .lenderId(lenderId)
                .totalAmountLent(totalAmountLent)
                .borrowerId(borrowerId)
                .timestamp(timestamp)
                .build();
    }
}
