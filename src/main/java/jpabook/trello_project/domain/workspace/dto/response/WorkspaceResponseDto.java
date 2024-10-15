package jpabook.trello_project.domain.workspace.dto.response;

import jpabook.trello_project.domain.workspace.entity.Workspace;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WorkspaceResponseDto {
    private Long workId;
    private String title;
    private String info;


    public WorkspaceResponseDto(Workspace workspace) {
        this.workId = workspace.getId();
        this.title = workspace.getTitle();
        this.info = workspace.getInfo();
    }
}
