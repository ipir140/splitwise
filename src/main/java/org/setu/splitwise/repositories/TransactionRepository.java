package org.setu.splitwise.repositories;

import org.setu.splitwise.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.groupId = :groupId")
    List<Transaction> findAllByGroupId(@Param("groupId") String groupId);

    @Query("SELECT DISTINCT t.groupId FROM Transaction t WHERE t.timestamp < :timestamp")
    List<String> findDistinctGroupIdsOlderThan(@Param("timestamp") LocalDateTime timestamp);

    @Query("SELECT t FROM Transaction t WHERE t.groupId = :groupId AND t.timestamp < :timestamp ORDER BY t.timestamp ASC")
    List<Transaction> findAllByGroupIdAndOlderThanTimestamp(
            @Param("groupId") String groupId,
            @Param("timestamp") LocalDateTime timestamp
    );

    @Transactional
    @Modifying
    @Query("DELETE FROM Transaction t WHERE t.id IN :ids")
    void deleteTransactionsByIds(@Param("ids") List<Long> ids);

}
