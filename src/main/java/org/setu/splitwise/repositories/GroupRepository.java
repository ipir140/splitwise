package org.setu.splitwise.repositories;

import org.setu.splitwise.models.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, String> {

//    @Query("SELECT g FROM Group g WHERE :userId IN elements(g.userIds)")
//    List<Group> findAllByUserIdsContains(Long userId);
//    @Query(value = "SELECT * FROM group_data WHERE :userId = ANY (user_ids::jsonb)", nativeQuery = true)
//    List<Group> findByUserId(@Param("userId") Long userId);
}