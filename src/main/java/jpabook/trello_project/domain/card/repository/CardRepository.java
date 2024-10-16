package jpabook.trello_project.domain.card.repository;

import jakarta.persistence.LockModeType;
import jpabook.trello_project.domain.card.entity.Card;
import jpabook.trello_project.domain.lists.entity.Lists;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface CardRepository extends JpaRepository<Card, Long>, CardRepositoryQuery {
    List<Card> findByList(Lists list);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Card c WHERE c.id = :id")
    Optional<Card> findByIdWithPessimisticLock(@Param("id") Long id);

    @Lock(value = LockModeType.OPTIMISTIC)
    @Query("SELECT c FROM Card c WHERE c.id = :id")
    Optional<Card> findByIdWithOptimisticLock(@Param("id") Long id);
}
