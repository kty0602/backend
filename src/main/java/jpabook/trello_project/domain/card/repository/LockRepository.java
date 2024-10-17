package jpabook.trello_project.domain.card.repository;

import jpabook.trello_project.domain.card.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LockRepository extends JpaRepository<Card, Long> {

    @Query(value = "SELECT get_lock(:key, 3000)", nativeQuery = true)
    void getLock(String key);

    @Query(value = "SELECT release_lock(:key)", nativeQuery = true)
    void releaseLock(String key);
}
