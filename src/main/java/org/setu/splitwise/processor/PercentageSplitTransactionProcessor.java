package org.setu.splitwise.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.setu.splitwise.dtos.transaction.BaseTransactionRequest;
import org.setu.splitwise.dtos.transaction.PercentageSplitTransactionRequest;
import org.setu.splitwise.dtos.transaction.SpecifiedSplitTransactionRequest;
import org.setu.splitwise.exceptions.BadRequestException;
import org.setu.splitwise.models.Transaction;

import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Math.abs;
import static org.setu.splitwise.Utils.Constants.PERCENTAGE_SPLIT;

public class PercentageSplitTransactionProcessor implements TransactionProcessor {
    @Override
    public String getType() {
        return PERCENTAGE_SPLIT;
    }

    @Override
    public Transaction processTransaction(BaseTransactionRequest request) throws BadRequestException, JsonProcessingException {
        PercentageSplitTransactionRequest percentageSplitTransactionRequest = (PercentageSplitTransactionRequest) request;

        double overallPercent = percentageSplitTransactionRequest.getBorrowerIdToPercentage().entrySet()
                .stream().mapToDouble(entry -> entry.getValue().doubleValue()).sum();

        if (abs(overallPercent-100.0)>0) {
            throw new BadRequestException("Overall percent not equal to 100");
        }

        Map<Long, Double> borrowerIdToAmount = percentageSplitTransactionRequest.getBorrowerIdToPercentage().entrySet()
                .stream().collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> (entry.getValue()/100.00)*percentageSplitTransactionRequest.getTotalAmountLent()
                ));

        Transaction transaction = Transaction.builder()
                .groupId(percentageSplitTransactionRequest.getGroupId())
                .lenderId(percentageSplitTransactionRequest.getLenderId())
                .totalAmountLent(percentageSplitTransactionRequest.getTotalAmountLent())
                .build();
        transaction.setBorrowerIdToAmount(borrowerIdToAmount);
        return transaction;
    }
}
