package jpabook.trello_project.domain.card.dto.response;

import jpabook.trello_project.domain.card.entity.Card;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CardRankResponseDto {
    private Long id;
    private String title;
    private String info;
    private Long viewCount;

    public CardRankResponseDto(Card card) {
        this.id = card.getId();
        this.title = card.getTitle();
        this.info = card.getInfo();
        this.viewCount = card.getViewCount();
    }

    public CardRankResponseDto(Card card, Long viewCount) {
        this.id = card.getId();
        this.title = card.getTitle();
        this.info = card.getInfo();
        this.viewCount = viewCount;
    }
}