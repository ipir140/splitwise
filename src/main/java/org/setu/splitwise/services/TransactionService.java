package org.setu.splitwise.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.setu.splitwise.dtos.group.CreateInternalGroupRequest;
import org.setu.splitwise.dtos.group.GroupBalanceResponse;
import org.setu.splitwise.dtos.group.GroupResponse;
import org.setu.splitwise.dtos.group.UserBalance;
import org.setu.splitwise.dtos.transaction.*;
import org.setu.splitwise.exceptions.BadRequestException;
import org.setu.splitwise.exceptions.GroupNotFoundException;
import org.setu.splitwise.exceptions.UserNotFoundException;
import org.setu.splitwise.models.Transaction;
import org.setu.splitwise.processor.*;
import org.setu.splitwise.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

import static org.setu.splitwise.Utils.Constants.*;

@Service
public class TransactionService {
    private final Map<String, TransactionProcessor> processors;
    private final GroupService groupService;
    private final UserService userService;
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(GroupService groupService, UserService userService, TransactionRepository transactionRepository) {
        this.processors = new HashMap<String, TransactionProcessor>() {{
            this.put(DIRECT, new DirectTransactionProcessor());
            this.put(EQUAL_SPLIT, new EqualSplitTransactionProcessor());
            this.put(PERCENTAGE_SPLIT, new PercentageSplitTransactionProcessor());
            this.put(SPECIFIED_SPLIT, new SpecifiedSplitTransactionProcessor());
        }};
        this.groupService = groupService;
        this.userService = userService;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransactionResponse recordTransaction(BaseTransactionRequest request)
            throws UserNotFoundException, GroupNotFoundException, BadRequestException, JsonProcessingException {
        TransactionProcessor processor = processors.get(request.getType());
        if (processor == null) {
            throw new IllegalArgumentException("Unsupported transaction type: " + request.getType());
        }
        Transaction transaction = processor.processTransaction(request);
        List<Long> nonExistingUserIds = userService.getAllNonExistingUserIds(getUserIds(transaction));
        if (nonExistingUserIds.size() > 0) {
            throw new UserNotFoundException(nonExistingUserIds);
        }

        if (request.getType().equals(DIRECT)) {
            DirectTransactionRequest directTransactionRequest = (DirectTransactionRequest) request;
            CreateInternalGroupRequest createInternalGroupRequest = groupService.getCreateInternalGroupRequest(Arrays.asList(
                    directTransactionRequest.getLenderId(), directTransactionRequest.getBorrowerId()));
            GroupResponse groupResponse = groupService.getOrCreateGroup(createInternalGroupRequest);
            transaction.setGroupId(groupResponse.getId());
        } else {
            String groupId;
            switch (request.getType()) {
                case EQUAL_SPLIT:
                    groupId = ((EqualSplitTransactionRequest) request).getGroupId();
                    break;
                case PERCENTAGE_SPLIT:
                    groupId = ((PercentageSplitTransactionRequest) request).getGroupId();
                    break;
                case SPECIFIED_SPLIT:
                    groupId = ((SpecifiedSplitTransactionRequest) request).getGroupId();
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported transaction type: " + request.getType());
            }
            Optional<GroupResponse> groupById = groupService.getGroupById(groupId);
            if (!groupById.isPresent()) {
                throw new GroupNotFoundException(groupId);
            }
        }

        System.out.println(transaction.toString());
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

    private List<Long> getUserIds(Transaction transaction) throws JsonProcessingException {
        Set<Long> userIds = transaction.getBorrowerIdToAmount().keySet().stream().collect(Collectors.toSet());
        userIds.add(transaction.getLenderId());
        return userIds.stream().collect(Collectors.toList());
    }

    public GroupBalanceResponse getBalancesOfGroup(String groupId) {
        List<Transaction> allByGroupId = transactionRepository.findAllByGroupId(groupId);
        Map<Long, Map<Long, Double>> userIdToBalances = new HashMap<>();
        allByGroupId.stream().forEach(transaction -> {
            try {
                transaction.getBorrowerIdToAmount().entrySet().stream()
                        .forEach(entry -> updateBalance(userIdToBalances, transaction.getLenderId(), entry.getKey(), entry.getValue()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });


        return GroupBalanceResponse.builder()
                .groupId(groupId)
                .userBalances(userIdToBalances.entrySet().stream()
                        .map(entry -> UserBalance.builder().userId(entry.getKey()).balances(entry.getValue()).build())
                        .collect(Collectors.toList())
                ).build();
    }

    private void updateBalance(Map<Long, Map<Long, Double>> userIdToBalances, @NotNull Long lenderId, Long borrowerId, Double amount) {
        if(!userIdToBalances.containsKey(lenderId)) {
            userIdToBalances.put(lenderId, new HashMap<Long, Double>());
        }
        if (!userIdToBalances.get(lenderId).containsKey(borrowerId)) {
            userIdToBalances.get(lenderId).put(borrowerId, 0.0);
        }
        userIdToBalances.get(lenderId).put(borrowerId, amount + userIdToBalances.get(lenderId).get(borrowerId));

        if(!userIdToBalances.containsKey(borrowerId)) {
            userIdToBalances.put(borrowerId, new HashMap<Long, Double>());
        }
        if (!userIdToBalances.get(borrowerId).containsKey(lenderId)) {
            userIdToBalances.get(borrowerId).put(lenderId, 0.0);
        }
        userIdToBalances.get(borrowerId).put(lenderId, amount - userIdToBalances.get(borrowerId).get(lenderId));
    }
}
