package jpabook.trello_project.domain.workspace_member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeMemberRoleRequest {
    @NotBlank
    private String workRole;
}
