package org.setu.splitwise.processor;

import org.setu.splitwise.dtos.transaction.BaseTransactionRequest;
import org.setu.splitwise.dtos.transaction.EqualSplitTransactionRequest;
import org.setu.splitwise.models.Transaction;

import java.util.Map;
import java.util.stream.Collectors;

import static org.setu.splitwise.Utils.Constants.EQUAL_SPLIT;

public class EqualSplitTransactionProcessor implements TransactionProcessor {
    @Override
    public String getType() {
        return EQUAL_SPLIT;
    }

    @Override
    public Transaction processTransaction(BaseTransactionRequest request) {
        EqualSplitTransactionRequest equalSplitTransactionRequest = (EqualSplitTransactionRequest) request;

        Double borrowedAmount = equalSplitTransactionRequest.getTotalAmountLent() / equalSplitTransactionRequest.getBorrowers().size();
        Map<Long, Double> borrowerIdToAmount = equalSplitTransactionRequest.getBorrowers().stream()
                .collect(Collectors.toMap(borrowerId -> borrowerId, borrowerId -> borrowedAmount));

        return Transaction.builder()
                .groupId(equalSplitTransactionRequest.getGroupId())
                .lenderId(equalSplitTransactionRequest.getLenderId())
                .totalAmountLent(equalSplitTransactionRequest.getTotalAmountLent())
                .borrowerIdToAmount(borrowerIdToAmount)
                .build();
    }
}
