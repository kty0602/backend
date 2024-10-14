package jpabook.trello_project.domain.workspace.service;

import jpabook.trello_project.domain.common.dto.AuthUser;
import jpabook.trello_project.domain.common.exceptions.AccessDeniedException;
import jpabook.trello_project.domain.user.entity.User;
import jpabook.trello_project.domain.user.repository.UserRepository;
import jpabook.trello_project.domain.workspace.dto.request.WorkspaceRequestDto;
import jpabook.trello_project.domain.workspace.dto.response.WorkspaceResponseDto;
import jpabook.trello_project.domain.workspace.entity.Workspace;
import jpabook.trello_project.domain.workspace.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkspaceAdminService {

    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    public WorkspaceResponseDto saveWorkspace(AuthUser authUser, WorkspaceRequestDto workspaceRequestDto) {
        User user = userRepository.findById(authUser.getId())
                        .orElseThrow(()-> new AccessDeniedException("사용자를 찾을 수 없습니다."));


        Workspace newWorkspace = new Workspace(
                user,
                workspaceRequestDto.getTitle(),
                workspaceRequestDto.getInfo()
        );

        Workspace savedWorkspace = workspaceRepository.save(newWorkspace);

        return new WorkspaceResponseDto(savedWorkspace);
    }
}
