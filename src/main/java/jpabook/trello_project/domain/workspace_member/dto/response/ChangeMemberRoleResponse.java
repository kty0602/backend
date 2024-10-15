package jpabook.trello_project.domain.workspace_member.dto.response;

import jpabook.trello_project.domain.workspace_member.enums.WorkRole;
import lombok.Getter;

@Getter
public class ChangeMemberRoleResponse {
    private final Long workId;
    private final Long memberId;
    private final Long userId;
    private final WorkRole workRole;

    public ChangeMemberRoleResponse(Long workId, Long memberId, Long userId, WorkRole workRole) {
        this.workId = workId;
        this.memberId = memberId;
        this.userId = userId;
        this.workRole = workRole;
    }
}
