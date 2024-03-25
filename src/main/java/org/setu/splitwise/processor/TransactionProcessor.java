package org.setu.splitwise.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.setu.splitwise.dtos.transaction.BaseTransactionRequest;
import org.setu.splitwise.exceptions.BadRequestException;
import org.setu.splitwise.models.Transaction;

public interface TransactionProcessor {

    String getType();
    Transaction processTransaction(BaseTransactionRequest request) throws BadRequestException, JsonProcessingException;
}
