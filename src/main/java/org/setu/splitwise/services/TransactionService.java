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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
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
                        .forEach(entry -> updateBalanceMap(userIdToBalances, transaction.getLenderId(), entry.getKey(), entry.getValue()));
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

    private void updateBalanceMap(Map<Long, Map<Long, Double>> userIdToBalances, @NotNull Long lenderId, Long borrowerId, Double amount) {
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
        userIdToBalances.get(borrowerId).put(lenderId, (-1.0*amount) + userIdToBalances.get(borrowerId).get(lenderId));
    }


    @Scheduled(fixedRate = 6 * 60 * 60 * 1000) // Execute every 6 hours
    @Transactional
    public void cancelTransactionsOlderThanThreeMonths() throws JsonProcessingException {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        List<String> distinctGroupIds = transactionRepository.findDistinctGroupIdsOlderThan(threeMonthsAgo);
        for (String groupId : distinctGroupIds) {
            List<Transaction> allByGroupIdAndOlderThanTimestamp = transactionRepository
                    .findAllByGroupIdAndOlderThanTimestamp(groupId, threeMonthsAgo);
            deleteCancellingTransactions(allByGroupIdAndOlderThanTimestamp);
        }
    }

    /**
     * Deletes consecutive transactions starting from the last one until the most recent transaction,
     * where all users have settled their debts, meaning no one owes any amount to anyone else.
     *
     * @param transactions List of sequential transactions of a group to be processed.
     */
    private void deleteCancellingTransactions(List<Transaction> transactions) throws JsonProcessingException {
        Map<Long, Map<Long, Double>> userIdToBalances = new HashMap<>();
        int k_most = -1;
        for (int i=0;i<transactions.size();++i) {
            Long lenderId = transactions.get(i).getLenderId();
            transactions.get(i).getBorrowerIdToAmount().entrySet().stream()
                    .forEach(entry -> {
                        Long borrowerId = entry.getKey();
                        Double amount = entry.getValue();
                        updateBalanceMap(userIdToBalances, lenderId, borrowerId, amount);
                        if (userIdToBalances.get(lenderId).get(borrowerId).equals(0.0)) {
                            userIdToBalances.get(lenderId).remove(borrowerId);
                            if (userIdToBalances.get(lenderId).size()==0) {
                                userIdToBalances.remove(lenderId);
                            }
                        }

                        if (userIdToBalances.get(borrowerId).get(lenderId).equals(0.0)) {
                            userIdToBalances.get(borrowerId).remove(lenderId);
                            if (userIdToBalances.get(borrowerId).size()==0) {
                                userIdToBalances.remove(borrowerId);
                            }
                        }
                    });

            if (userIdToBalances.size()==0) {
                k_most = i;
            }
        }

        if (k_most!=-1) {
            List<Long> deleteTransactionIds = new ArrayList<Long>();
            for (int i=0;i<=k_most;++i) {
                deleteTransactionIds.add(transactions.get(i).getId());
            }
            transactionRepository.deleteTransactionsByIds(deleteTransactionIds);
        }
    }
}
