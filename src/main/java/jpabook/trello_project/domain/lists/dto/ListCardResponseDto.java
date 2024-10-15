package jpabook.trello_project.domain.lists.dto;

import jpabook.trello_project.domain.card.dto.response.CardResponseDto;
import jpabook.trello_project.domain.lists.entity.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListCardResponseDto {
    private Long listId;
    private String title;
    private long listOrder;
    private List<CardResponseDto> cards;

    public ListCardResponseDto(Lists lists, List<CardResponseDto> cards) {
        this.listId = lists.getId();
        this.title = lists.getTitle();
        this.listOrder = lists.getListOrder();
        this.cards = cards;
    }
}
