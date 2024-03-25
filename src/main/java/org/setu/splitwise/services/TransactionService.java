package org.setu.splitwise.services;

import org.setu.splitwise.dtos.group.CreateInternalGroupRequest;
import org.setu.splitwise.dtos.group.GroupResponse;
import org.setu.splitwise.dtos.transaction.BaseTransactionRequest;
import org.setu.splitwise.dtos.transaction.DirectTransactionRequest;
import org.setu.splitwise.dtos.transaction.SplitTransactionRequest;
import org.setu.splitwise.dtos.transaction.TransactionResponse;
import org.setu.splitwise.exceptions.GroupNotFoundException;
import org.setu.splitwise.exceptions.UserNotFoundException;
import org.setu.splitwise.models.Transaction;
import org.setu.splitwise.processor.TransactionProcessor;
import org.setu.splitwise.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static org.setu.splitwise.Utils.Constants.DIRECT;

@Service
public class TransactionService {
    private final Map<String, TransactionProcessor> processors;
    private final GroupService groupService;
    private final UserService userService;
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(Map<String, TransactionProcessor> processors, GroupService groupService,
                              UserService userService, TransactionRepository transactionRepository) {
        this.processors = processors;
        this.groupService = groupService;
        this.userService = userService;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransactionResponse recordTransaction(BaseTransactionRequest request) throws UserNotFoundException, GroupNotFoundException {
        TransactionProcessor processor = processors.get(request.getType());
        if (processor == null) {
            throw new IllegalArgumentException("Unsupported transaction type: " + request.getType());
        }
        Transaction transaction = processor.processTransaction(request);
        List<Long> nonExistingUserIds = userService.getAllNonExistingUserIds(getUserIds(transaction));
        if (nonExistingUserIds.size() > 0) {
            throw new UserNotFoundException(nonExistingUserIds);
        }

        if (request.getType() == DIRECT) {
            DirectTransactionRequest directTransactionRequest = (DirectTransactionRequest) request;
            CreateInternalGroupRequest createInternalGroupRequest = groupService.getCreateInternalGroupRequest(Arrays.asList(
                    directTransactionRequest.getLenderId(), directTransactionRequest.getBorrowerId()));
            GroupResponse groupResponse = groupService.getOrCreateGroup(createInternalGroupRequest);
            transaction.setGroupId(groupResponse.getId());
        } else {
            SplitTransactionRequest splitTransactionRequest = (SplitTransactionRequest) request;
            Optional<GroupResponse> groupById = groupService.getGroupById(splitTransactionRequest.getGroupId());
            if (!groupById.isPresent()) {
                throw new GroupNotFoundException(splitTransactionRequest.getGroupId());
            }
        }

        Transaction recordedTransaction = transactionRepository.save(transaction);
        return TransactionResponse.builder()
                .id(recordedTransaction.getId())
                .type(request.getType())
                .lenderId(recordedTransaction.getLenderId())
                .groupId(recordedTransaction.getGroupId())
                .totalAmountLent(recordedTransaction.getTotalAmountLent())
                .borrowerIdToAmount(recordedTransaction.getBorrowerIdToAmount())
                .build();
    }

    private List<Long> getUserIds(Transaction transaction) {
        Set<Long> userIds = transaction.getBorrowerIdToAmount().keySet().stream().collect(Collectors.toSet());
        userIds.add(transaction.getLenderId());
        return userIds.stream().collect(Collectors.toList());
    }
}
