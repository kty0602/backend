package jpabook.trello_project.domain.workspace_member.controller;

import jakarta.validation.Valid;
import jpabook.trello_project.domain.common.dto.AuthUser;
import jpabook.trello_project.domain.common.dto.ResponseDto;
import jpabook.trello_project.domain.workspace_member.dto.request.ChangeMemberRoleRequest;
import jpabook.trello_project.domain.workspace_member.dto.response.ChangeMemberRoleResponse;
import jpabook.trello_project.domain.workspace_member.service.WorkspaceMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminWorkspaceMemberController {
    private final WorkspaceMemberService workspaceMemberService;

    @RequestMapping("/workspaces/{workId}/members/{userId}")
    public ResponseDto<ChangeMemberRoleResponse> changeMemberRole(@AuthenticationPrincipal AuthUser authUser,
                                                                  @PathVariable Long workId,
                                                                  @PathVariable Long userId,
                                                                  @Valid @RequestBody ChangeMemberRoleRequest changeMemberRoleRequest) {
        return ResponseDto.of(HttpStatus.OK, workspaceMemberService.changeMemberRole(authUser, workId, userId, changeMemberRoleRequest));
    }
}
