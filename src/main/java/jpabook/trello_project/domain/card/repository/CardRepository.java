package jpabook.trello_project.domain.card.repository;

import jpabook.trello_project.domain.card.entity.Card;
import jpabook.trello_project.domain.lists.entity.Lists;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface CardRepository extends JpaRepository<Card, Long>, CardRepositoryQuery {
  List<Card> findByList(Lists list);
}
