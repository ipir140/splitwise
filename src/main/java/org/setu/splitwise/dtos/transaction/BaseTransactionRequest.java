package org.setu.splitwise.dtos.transaction;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

import static org.setu.splitwise.Utils.Constants.*;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = EqualSplitTransactionRequest.class, name = EQUAL_SPLIT),
        @JsonSubTypes.Type(value = SpecifiedSplitTransactionRequest.class, name = SPECIFIED_SPLIT),
        @JsonSubTypes.Type(value = PercentageSplitTransactionRequest.class, name = PERCENTAGE_SPLIT),
        @JsonSubTypes.Type(value = DirectTransactionRequest.class, name = DIRECT)
})
public class BaseTransactionRequest {

//    @NotEmpty(message = "type cannot be empty")
    private String type;
}
