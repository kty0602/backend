package jpabook.trello_project.domain.manager.dto.response;

import jpabook.trello_project.domain.manager.entity.Manager;
import lombok.Getter;

@Getter
public class ManagerResponseDto {
    private Long id;
    private Long cardId;
    private Long userId;

    public ManagerResponseDto(Manager manager) {
        this.id = manager.getId();
        this.cardId = manager.getCard().getId();
        this.userId = manager.getUser().getId();
    }
}
