package jpabook.trello_project.domain.workspace.controller;

import jakarta.validation.Valid;
import jpabook.trello_project.domain.common.dto.AuthUser;
import jpabook.trello_project.domain.common.dto.ResponseDto;
import jpabook.trello_project.domain.workspace.dto.request.WorkspaceRequestDto;
import jpabook.trello_project.domain.workspace.dto.response.WorkspaceResponseDto;
import jpabook.trello_project.domain.workspace.service.WorkspaceService;
import jpabook.trello_project.domain.workspace_member.dto.response.WorkspaceMemberResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    /**
     * 워크스페이스 멤버 초대
     * @param authUser 사용자
     * @param workId 워크스페이스 아이디
     * @param email 추가할 멤버의 이메일
     * @return ResponseEntity<ResponseDto<WorkspaceMemberResponseDto>>
     */
    @PostMapping("{workId}/members")
    public ResponseEntity<ResponseDto<WorkspaceMemberResponseDto>> inviteMember(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long workId,
            @RequestParam String email
    ) {
        WorkspaceMemberResponseDto responseDto = workspaceService.inviteMember(authUser, workId, email);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.of(200, responseDto));

    }


    /**
     * 워크스페이스 목록 조회
     * @param page 페이지 수
     * @param size 사이즈 수
     * @param authUser 사용자
     * @return ResponseEntity<ResponseDto<Page<WorkspaceResponseDto>>>
     */
    @GetMapping
    public ResponseEntity<ResponseDto<Page<WorkspaceResponseDto>>> getWorkspaces(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        Page<WorkspaceResponseDto> responseDto = workspaceService.getWorkspaces(page, size, authUser);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.of(200, responseDto));
    }

    @PatchMapping("/{workId}")
    public ResponseEntity<ResponseDto<WorkspaceResponseDto>> updateWorkspace(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long workId,
            @Valid @RequestBody WorkspaceRequestDto workspaceRequestDto
    ) {
        WorkspaceResponseDto responseDto = workspaceService.updateWorkspace(authUser, workId, workspaceRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.of(200, responseDto));
    }

    @DeleteMapping("/{workId}")
    public ResponseEntity<ResponseDto<String>> deleteWorkspace(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long workId
    ) {
        workspaceService.deleteWorkspace(authUser, workId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.of(200, "워크스페이스가 정상적으로 삭제되었습니다."));
    }

}
