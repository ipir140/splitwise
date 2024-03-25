package org.setu.splitwise.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.setu.splitwise.Utils.DateTimeUtils;
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
    public Transaction processTransaction(BaseTransactionRequest request) throws JsonProcessingException {
        EqualSplitTransactionRequest equalSplitTransactionRequest = (EqualSplitTransactionRequest) request;

        Double borrowedAmount = equalSplitTransactionRequest.getTotalAmountLent() / equalSplitTransactionRequest.getBorrowers().size();
        Map<Long, Double> borrowerIdToAmount = equalSplitTransactionRequest.getBorrowers().stream()
                .collect(Collectors.toMap(borrowerId -> borrowerId, borrowerId -> borrowedAmount));

        Transaction transaction = Transaction.builder()
                .groupId(equalSplitTransactionRequest.getGroupId())
                .lenderId(equalSplitTransactionRequest.getLenderId())
                .totalAmountLent(equalSplitTransactionRequest.getTotalAmountLent())
                .timestamp(DateTimeUtils.toLocalDateTime(equalSplitTransactionRequest.getTimestamp()))
                .build();
        transaction.setBorrowerIdToAmount(borrowerIdToAmount);
        return transaction;
    }
}
