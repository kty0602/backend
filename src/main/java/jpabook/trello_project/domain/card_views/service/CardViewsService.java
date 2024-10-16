package jpabook.trello_project.domain.card_views.service;

import jpabook.trello_project.domain.card.entity.Card;
import jpabook.trello_project.domain.card_views.entity.CardViews;
import jpabook.trello_project.domain.card_views.repository.CardViewsRepository;
import jpabook.trello_project.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardViewsService {
    private final CardViewsRepository cardViewsRepository;

    @Transactional
    public void save(User user, Card card) {
        CardViews cardViews = new CardViews(user, card);
        cardViewsRepository.save(cardViews);
    }

    public CardViews findByUserAndCard(User user, Card card) {
        List<CardViews> histories = cardViewsRepository.findAllByUserAndCardOrderByCreatedAtDesc(user, card);

        // 기록 존재하지 않으면 null 반환
        if (histories.isEmpty()) return null;

        // 기록 존재시 가장 최근 기록 반환
        return histories.get(0);
    }
}
