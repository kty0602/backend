package jpabook.trello_project.domain.workspace_member.dto.response;

import jpabook.trello_project.domain.workspace.entity.Workspace;
import jpabook.trello_project.domain.workspace_member.entity.WorkspaceMember;
import jpabook.trello_project.domain.workspace_member.enums.WorkRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WorkspaceMemberResponseDto {
    private Long workMemberId;
    private Long workId;
    private Long memberId;
    private WorkRole workRole;


    public WorkspaceMemberResponseDto(WorkspaceMember workspaceMember) {
        this.workMemberId = workspaceMember.getId();
        this.workId = workspaceMember.getWorkspace().getId();
        this.memberId = workspaceMember.getUser().getId();
        this.workRole = workspaceMember.getWorkRole();
    }
}
