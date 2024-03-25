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

import static org.setu.splitwise.Utils.Constants.EQUAL_SPLIT;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class EqualSplitTransactionRequest extends BaseTransactionRequest {

    @NotEmpty(message = "type cannot be empty")
    private String type;

    @NotNull(message = "lenderId cannot be null")
    private Long lenderId;

    @NotNull(message = "groupId cannot be null")
    private String groupId;

    @NotNull(message = "totalAmountLent cannot be null")
    private Double totalAmountLent;

    @NotEmpty(message = "borrowers cannot be empty")
    private List<Long> borrowers;

    private Long timestamp;

    // Constructor with JsonCreator annotation to set type during deserialization
    @JsonCreator
    public static EqualSplitTransactionRequest create(@JsonProperty("lenderId") Long lenderId,
                                                      @JsonProperty("groupId") String groupId,
                                                      @JsonProperty("totalAmountLent") Double totalAmountLent,
                                                      @JsonProperty("borrowers") List<Long> borrowers,
                                                      @JsonProperty("timestamp") Long timestamp) {
        return EqualSplitTransactionRequest.builder()
                .type(EQUAL_SPLIT)
                .lenderId(lenderId)
                .groupId(groupId)
                .totalAmountLent(totalAmountLent)
                .borrowers(borrowers)
                .timestamp(timestamp)
                .build();
    }
}
