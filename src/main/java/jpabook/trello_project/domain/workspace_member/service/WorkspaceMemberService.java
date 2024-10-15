package jpabook.trello_project.domain.workspace_member.service;

import jpabook.trello_project.domain.common.dto.AuthUser;
import jpabook.trello_project.domain.common.exceptions.ApiException;
import jpabook.trello_project.domain.common.exceptions.ErrorStatus;
import jpabook.trello_project.domain.user.entity.User;
import jpabook.trello_project.domain.user.service.UserService;
import jpabook.trello_project.domain.workspace.entity.Workspace;
import jpabook.trello_project.domain.workspace.repository.WorkspaceRepository;
import jpabook.trello_project.domain.workspace_member.dto.request.ChangeMemberRoleRequest;
import jpabook.trello_project.domain.workspace_member.dto.response.ChangeMemberRoleResponse;
import jpabook.trello_project.domain.workspace_member.entity.WorkspaceMember;
import jpabook.trello_project.domain.workspace_member.enums.WorkRole;
import jpabook.trello_project.domain.workspace_member.repository.WorkspaceMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkspaceMemberService {
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceRepository workspaceRepository;
    private final UserService userService;

    @Transactional
    public ChangeMemberRoleResponse changeMemberRole(AuthUser authUser,
                                                     Long workId,
                                                     Long userId,
                                                     ChangeMemberRoleRequest changeMemberRoleRequest) {
        User adminUser = userService.findByEmail(authUser.getEmail());

        Workspace workspace = workspaceRepository.findById(workId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_WORKSPACE));

        User user = userService.findById(userId);

        // 요청을 보낸 어드민이 워크스페이스의 멤버가 아닌 경우
        if (!workspaceMemberRepository.existsByUser(adminUser)) throw new ApiException(ErrorStatus._UNAUTHORIZED_USER);

        // 해당 유저가 워크스페이스의 멤버가 아닌 경우 (_NOT_FOUND_MEMBER)
        WorkspaceMember workspaceMember = workspaceMemberRepository.findByUser(user)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_MEMBER));

        workspaceMember.changeRole(WorkRole.of(changeMemberRoleRequest.getWorkRole()));
        return new ChangeMemberRoleResponse(workId, workspaceMember.getId(), userId, workspaceMember.getWorkRole());
    }

}
