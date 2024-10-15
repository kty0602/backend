package jpabook.trello_project.domain.workspace.service;

import jpabook.trello_project.domain.common.dto.AuthUser;
import jpabook.trello_project.domain.common.exceptions.AccessDeniedException;
import jpabook.trello_project.domain.user.entity.User;
import jpabook.trello_project.domain.user.repository.UserRepository;
import jpabook.trello_project.domain.workspace.dto.request.WorkspaceRequestDto;
import jpabook.trello_project.domain.workspace.dto.response.WorkspaceResponseDto;
import jpabook.trello_project.domain.workspace.entity.Workspace;
import jpabook.trello_project.domain.workspace.repository.WorkspaceRepository;
import jpabook.trello_project.domain.workspace_member.dto.response.WorkspaceMemberResponseDto;
import jpabook.trello_project.domain.workspace_member.entity.WorkspaceMember;
import jpabook.trello_project.domain.workspace_member.enums.WorkRole;
import jpabook.trello_project.domain.workspace_member.repository.WorkspaceMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkspaceService {

    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;


    @Transactional
    public WorkspaceMemberResponseDto inviteMember(AuthUser authUser, Long workId, String email) {

        Workspace workspace = workspaceRepository.findById(workId)
                .orElseThrow(() -> new AccessDeniedException("워크스페이스를 찾을 수 없습니다."));

        User currentUser = User.fromAuthUser(authUser);
        if (!workspace.getUser().getId().equals(currentUser.getId())) {
            WorkspaceMember workspaceMember = workspaceMemberRepository.findByUserAndWorkspace(currentUser, workspace)
                    .orElseThrow(() -> new AccessDeniedException("워크스페이스의 멤버가 아닙니다."));

            if (workspaceMember.getWorkRole() != WorkRole.ROLE_WORKSPACE) {
                throw new AccessDeniedException("멤버를 초대할 권한이 없습니다.");
            }
        }

        User newMember = userRepository.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("초대할 멤버를 찾을 수 없습니다."));

        WorkspaceMember newWorkspaceMember = new WorkspaceMember(newMember, workspace, WorkRole.ROLE_READONLY);
        WorkspaceMember savedWorkspaceMember = workspaceMemberRepository.save(newWorkspaceMember);

        return new WorkspaceMemberResponseDto(savedWorkspaceMember);
    }


    public Page<WorkspaceResponseDto> getWorkspaces(int page, int size, AuthUser authUser) {
        Pageable pageable = PageRequest.of(page - 1, size);

        User user = userRepository.findById(authUser.getId())
                        .orElseThrow(()-> new AccessDeniedException("사용자를 찾을 수 없습니다."));

        Page<WorkspaceMember> workspaceMembers = workspaceMemberRepository.findByUser(user, pageable);

        return workspaceMembers.map(workspaceMember ->
                new WorkspaceResponseDto(workspaceMember.getWorkspace())
        );
    }



    @Transactional
    public WorkspaceResponseDto updateWorkspace(AuthUser authUser, Long workId, WorkspaceRequestDto workspaceRequestDto) {
        Workspace workspace = workspaceRepository.findById(workId)
                .orElseThrow(() -> new AccessDeniedException("워크스페이스를 찾을 수 없습니다."));

        WorkspaceMember workspaceMember = workspaceMemberRepository.findByUserAndWorkspace(User.fromAuthUser(authUser), workspace)
                .orElseThrow(() -> new AccessDeniedException("워크스페이스의 멤버가 아닙니다."));

        if (workspaceMember.getWorkRole() != WorkRole.ROLE_WORKSPACE) {
            throw new AccessDeniedException("워크스페이스를 수정할 권한이 없습니다.");
        }

        Workspace updatedWorkspace = workspace.update(workspaceRequestDto);

        return new WorkspaceResponseDto(updatedWorkspace);
    }


    @Transactional
    public void deleteWorkspace(AuthUser authUser, Long workId) {
        Workspace workspace = workspaceRepository.findById(workId)
                .orElseThrow(() -> new AccessDeniedException("워크스페이스를 찾을 수 없습니다."));

        WorkspaceMember workspaceMember = workspaceMemberRepository.findByUserAndWorkspace(User.fromAuthUser(authUser), workspace)
                .orElseThrow(() -> new AccessDeniedException("워크스페이스의 멤버가 아닙니다."));

        if (workspaceMember.getWorkRole() != WorkRole.ROLE_WORKSPACE) {
            throw new AccessDeniedException("워크스페이스를 삭제할 권한이 없습니다.");
        }

        workspaceRepository.delete(workspace);
    }
}
