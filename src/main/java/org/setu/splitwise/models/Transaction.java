package org.setu.splitwise.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.setu.splitwise.Utils.JsonUtils;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Entity
@Table(name = "transaction_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String groupId;

    @NotNull
    private Long lenderId;

    @NotNull
    private String borrowerIdToAmountString;

    @NotNull
    private Double totalAmountLent;

    public Map<Long, Double> getBorrowerIdToAmount() throws JsonProcessingException {
        return JsonUtils.objectMapper.readValue(borrowerIdToAmountString, new TypeReference<Map<Long, Double>>() {});
    }

    public void setBorrowerIdToAmount(Map<Long, Double> borrowerIdToAmount) throws JsonProcessingException {
        this.borrowerIdToAmountString = JsonUtils.objectMapper.writeValueAsString(borrowerIdToAmount);
    }
}
