package org.setu.splitwise.dtos.transaction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

import static org.setu.splitwise.Utils.Constants.SPECIFIED_SPLIT;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SpecifiedSplitTransactionRequest extends BaseTransactionRequest {

    @NotEmpty(message = "type cannot be empty")
    private String type;

    @NotNull(message = "lenderId cannot be null")
    private Long lenderId;

    @NotNull(message = "groupId cannot be null")
    private String groupId;

    @NotEmpty(message = "borrowerIdToAmount cannot be empty")
    private Map<Long, Double> borrowerIdToAmount;

    private Long timestamp;

    // Constructor with JsonCreator annotation to set type during deserialization
    @JsonCreator
    public static SpecifiedSplitTransactionRequest create(@JsonProperty("lenderId") Long lenderId,
                                                          @JsonProperty("groupId") String groupId,
                                                          @JsonProperty("borrowerIdToAmount") Map<Long, Double> borrowerIdToAmount,
                                                          @JsonProperty("timestamp") Long timestamp) {
        return SpecifiedSplitTransactionRequest.builder()
                .type(SPECIFIED_SPLIT)
                .lenderId(lenderId)
                .groupId(groupId)
                .borrowerIdToAmount(borrowerIdToAmount)
                .timestamp(timestamp)
                .build();
    }
}
