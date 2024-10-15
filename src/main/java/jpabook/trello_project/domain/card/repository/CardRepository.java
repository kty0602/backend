package jpabook.trello_project.domain.card.repository;

import jpabook.trello_project.domain.card.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long>, CardRepositoryQuery {
}
