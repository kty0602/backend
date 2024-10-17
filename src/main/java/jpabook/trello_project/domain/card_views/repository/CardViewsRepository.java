package jpabook.trello_project.domain.card_views.repository;

import jpabook.trello_project.domain.card.entity.Card;
import jpabook.trello_project.domain.card_views.entity.CardViews;
import jpabook.trello_project.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardViewsRepository extends JpaRepository<CardViews, Long> {
    List<CardViews> findAllByUserAndCardOrderByCreatedAtDesc(User user, Card card);
}
