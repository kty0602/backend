package jpabook.trello_project.domain.lists.dto;

import jpabook.trello_project.domain.lists.entity.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ListSaveResponseDto {
    private Long listId;
    private String title;
    private long listOrder;

    public ListSaveResponseDto(Lists lists) {
        this.listId = lists.getId();
        this.title = lists.getTitle();
        this.listOrder = lists.getListOrder();
    }
}
