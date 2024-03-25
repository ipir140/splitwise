package org.setu.splitwise.dtos.transaction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

import static org.setu.splitwise.Utils.Constants.EQUAL_SPLIT;
import static org.setu.splitwise.Utils.Constants.PERCENTAGE_SPLIT;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PercentageSplitTransactionRequest extends BaseTransactionRequest {

    @NotEmpty(message = "type cannot be empty")
    private String type;

    @NotNull(message = "lenderId cannot be null")
    private Long lenderId;

    @NotNull(message = "groupId cannot be null")
    private String groupId;

    @NotNull(message = "totalAmountLent cannot be null")
    private Double totalAmountLent;

    @NotEmpty(message = "borrowerIdToPercentage cannot be empty")
    private Map<Long, Double> borrowerIdToPercentage;

    private Long timestamp;

    // Constructor with JsonCreator annotation to set type during deserialization
    @JsonCreator
    public static PercentageSplitTransactionRequest create(@JsonProperty("lenderId") Long lenderId,
                                                           @JsonProperty("groupId") String groupId,
                                                           @JsonProperty("totalAmountLent") Double totalAmountLent,
                                                           @JsonProperty("borrowerIdToPercentage") Map<Long, Double> borrowerIdToPercentage,
                                                           @JsonProperty("timestamp") Long timestamp) {
        return PercentageSplitTransactionRequest.builder()
                .type(PERCENTAGE_SPLIT)
                .lenderId(lenderId)
                .groupId(groupId)
                .totalAmountLent(totalAmountLent)
                .borrowerIdToPercentage(borrowerIdToPercentage)
                .timestamp(timestamp)
                .build();
    }
}
