package jpabook.trello_project.domain.card.dto.response;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CardSearchResponse {
    private final Long cardId;
    private final Long listId;
    private final String title;
    private final String info;
    private final LocalDate due;

    public CardSearchResponse(Long cardId, Long listId, String title, String info, LocalDate due) {
        this.cardId = cardId;
        this.listId = listId;
        this.title = title;
        this.info = info;
        this.due = due;
    }
}