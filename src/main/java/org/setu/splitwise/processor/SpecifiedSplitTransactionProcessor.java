package org.setu.splitwise.processor;

import org.setu.splitwise.dtos.transaction.BaseTransactionRequest;
import org.setu.splitwise.dtos.transaction.EqualSplitTransactionRequest;
import org.setu.splitwise.dtos.transaction.SpecifiedSplitTransactionRequest;
import org.setu.splitwise.exceptions.BadRequestException;
import org.setu.splitwise.models.Transaction;

import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Math.abs;
import static org.setu.splitwise.Utils.Constants.SPECIFIED_SPLIT;

public class SpecifiedSplitTransactionProcessor implements TransactionProcessor {
    @Override
    public String getType() {
        return SPECIFIED_SPLIT;
    }

    @Override
    public Transaction processTransaction(BaseTransactionRequest request) {
        SpecifiedSplitTransactionRequest specifiedSplitTransactionRequest = (SpecifiedSplitTransactionRequest) request;

        double borrowerSumValue = specifiedSplitTransactionRequest.getBorrowerIdToAmount().entrySet()
                .stream().mapToDouble(entry -> entry.getValue().doubleValue()).sum();

        return Transaction.builder()
                .groupId(specifiedSplitTransactionRequest.getGroupId())
                .lenderId(specifiedSplitTransactionRequest.getLenderId())
                .totalAmountLent(borrowerSumValue)
                .borrowerIdToAmount(specifiedSplitTransactionRequest.getBorrowerIdToAmount())
                .build();
    }
}
