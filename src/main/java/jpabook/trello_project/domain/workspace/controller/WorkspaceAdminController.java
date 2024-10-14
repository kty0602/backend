package jpabook.trello_project.domain.workspace.controller;

import jakarta.validation.Valid;
import jpabook.trello_project.domain.common.dto.AuthUser;
import jpabook.trello_project.domain.common.dto.ResponseDto;
import jpabook.trello_project.domain.workspace.dto.request.WorkspaceRequestDto;
import jpabook.trello_project.domain.workspace.dto.response.WorkspaceResponseDto;
import jpabook.trello_project.domain.workspace.service.WorkspaceAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class WorkspaceAdminController {

    private final WorkspaceAdminService workspaceAdminService;

    /**
     * 워크스페이스 저장
     * @param authUser
     * @param workspaceRequestDto
     * @return ResponseEntity<ResponseDto<WorkspaceResponseDto>>
     */
    @PostMapping("/workspaces")
    public ResponseEntity<ResponseDto<WorkspaceResponseDto>> createWorkspace(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody WorkspaceRequestDto workspaceRequestDto
    ) {
        WorkspaceResponseDto responseDto = workspaceAdminService.saveWorkspace(authUser, workspaceRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.of(200, responseDto));
    }
}
