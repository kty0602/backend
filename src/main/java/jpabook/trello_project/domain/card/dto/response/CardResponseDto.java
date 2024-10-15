package jpabook.trello_project.domain.card.dto.response;

import jpabook.trello_project.domain.card.entity.Card;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class CardResponseDto {
    private Long id;
    private String title;
    private String info;
    private LocalDate due;

    public CardResponseDto(Card card) {
        this.id = card.getId();
        this.title = card.getTitle();
        this.info = card.getInfo();
        this.due = card.getDue();
    }
}
