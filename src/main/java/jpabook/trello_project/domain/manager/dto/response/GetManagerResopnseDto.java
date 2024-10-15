package jpabook.trello_project.domain.manager.dto.response;

import jpabook.trello_project.domain.manager.entity.Manager;
import lombok.Getter;

@Getter
public class GetManagerResopnseDto {
    // 카드 조회 시 매니저 정보 확인을 위한 dto
    private Long userId;
    private String managerName;

    public GetManagerResopnseDto(Manager manager) {
        this.userId = manager.getUser().getId();
        this.managerName = manager.getUser().getName();
    }
}
