package org.setu.splitwise.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @NotEmpty
    @ElementCollection
    private Map<Long, Double> borrowerIdToAmount;

    @NotNull
    private Double totalAmountLent;
}
