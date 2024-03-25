package org.setu.splitwise.repositories;

import org.setu.splitwise.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.group.id = :groupId")
    List<Transaction> findAllByGroupId(@Param("groupId") String groupId);
}
