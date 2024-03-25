package org.setu.splitwise.repositories;

import org.setu.splitwise.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u.id FROM User u WHERE u.id IN (:userIds)")
    List<Long> findIdsByIdIn(List<Long> userIds);
}
