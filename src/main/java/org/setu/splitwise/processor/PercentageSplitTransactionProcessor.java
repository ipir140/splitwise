package org.setu.splitwise.processor;

import org.setu.splitwise.dtos.transaction.BaseTransactionRequest;
import org.setu.splitwise.dtos.transaction.PercentageSplitTransactionRequest;
import org.setu.splitwise.dtos.transaction.SpecifiedSplitTransactionRequest;
import org.setu.splitwise.models.Transaction;

import java.util.Map;
import java.util.stream.Collectors;

import static org.setu.splitwise.Utils.Constants.PERCENTAGE_SPLIT;

public class PercentageSplitTransactionProcessor implements TransactionProcessor {
    @Override
    public String getType() {
        return PERCENTAGE_SPLIT;
    }

    @Override
    public Transaction processTransaction(BaseTransactionRequest request) {
        PercentageSplitTransactionRequest percentageSplitTransactionRequest = (PercentageSplitTransactionRequest) request;

        Map<Long, Double> borrowerIdToAmount = percentageSplitTransactionRequest.getBorrowerIdToPercentage().entrySet()
                .stream().collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> (entry.getValue()/100.00)*percentageSplitTransactionRequest.getTotalAmountLent()
                ));

        return Transaction.builder()
                .groupId(percentageSplitTransactionRequest.getGroupId())
                .lenderId(percentageSplitTransactionRequest.getLenderId())
                .totalAmountLent(percentageSplitTransactionRequest.getTotalAmountLent())
                .borrowerIdToAmount(borrowerIdToAmount)
                .build();
    }
}
