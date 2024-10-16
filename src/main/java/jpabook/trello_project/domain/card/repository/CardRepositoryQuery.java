package jpabook.trello_project.domain.card.repository;

import jpabook.trello_project.domain.card.dto.CardSearchCondition;
import jpabook.trello_project.domain.card.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface CardRepositoryQuery {
    Page<Card> findCardsByCondition(CardSearchCondition condition, Pageable pageable);

}
