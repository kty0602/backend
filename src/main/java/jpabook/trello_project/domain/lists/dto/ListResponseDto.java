package jpabook.trello_project.domain.lists.dto;

import jpabook.trello_project.domain.lists.entity.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListResponseDto {
    private Long listId;
    private String title;
    private long listOrder;

    public ListResponseDto(Lists lists) {
        this.listId = lists.getId();
        this.title = lists.getTitle();
        this.listOrder = lists.getListOrder();
    }
}
