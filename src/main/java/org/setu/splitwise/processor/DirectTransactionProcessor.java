package org.setu.splitwise.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.setu.splitwise.dtos.transaction.BaseTransactionRequest;
import org.setu.splitwise.dtos.transaction.DirectTransactionRequest;
import org.setu.splitwise.models.Transaction;

import java.util.HashMap;
import java.util.Map;

import static org.setu.splitwise.Utils.Constants.DIRECT;

public class DirectTransactionProcessor implements TransactionProcessor {
    @Override
    public String getType() {
        return DIRECT;
    }

    @Override
    public Transaction processTransaction(BaseTransactionRequest request) throws JsonProcessingException {
        DirectTransactionRequest directTransactionRequest = (DirectTransactionRequest) request;


        Map<Long, Double> borrowerIdToAmount = new HashMap<Long, Double>() {{
           this.put(directTransactionRequest.getBorrowerId(), directTransactionRequest.getTotalAmountLent());
        }};

        Transaction transaction = Transaction.builder()
                .lenderId(directTransactionRequest.getLenderId())
                .totalAmountLent(directTransactionRequest.getTotalAmountLent())
                .build();
        transaction.setBorrowerIdToAmount(borrowerIdToAmount);
        return transaction;
    }
}
