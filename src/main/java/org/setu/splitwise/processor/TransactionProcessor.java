package org.setu.splitwise.processor;

import org.setu.splitwise.dtos.transaction.BaseTransactionRequest;
import org.setu.splitwise.models.Transaction;

public interface TransactionProcessor {

    String getType();
    Transaction processTransaction(BaseTransactionRequest request);
}
