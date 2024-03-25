package org.setu.splitwise.repositories;

import org.setu.splitwise.models.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, String> {

    @Query("SELECT g FROM Group g WHERE :userId MEMBER OF g.userIds")
    List<Group> findAllByUserIdsContains(Long userId);
}