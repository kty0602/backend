package jpabook.trello_project.domain.board.controller;

import jakarta.validation.Valid;
import jpabook.trello_project.domain.board.dto.request.BoardRequestDto;
import jpabook.trello_project.domain.board.dto.response.BoardResponseDto;
import jpabook.trello_project.domain.board.dto.response.OneBoardResponseDto;
import jpabook.trello_project.domain.board.service.BoardService;
import jpabook.trello_project.domain.common.dto.AuthUser;
import jpabook.trello_project.domain.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;

@RestController
@RequestMapping("/workspaces/{workId}/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    /**
     * 보드 생성 (이미지 파일 업로드 포함)
     * @param authUser 사용자
     * @param workId 워크스페이스 아이디
     * @param boardRequestDto 보드 requestDto
     * @return ResponseEntity<ResponseDto<BoardResponseDto>>
     */
    @PostMapping
    public ResponseEntity<ResponseDto<BoardResponseDto>> createBoard(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long workId,
            @RequestPart BoardRequestDto boardRequestDto, // JSON 데이터
            @RequestPart(required = false) MultipartFile backgroundImageFile  // 파일 데이터
    ) throws IOException {
        // 서비스에 DTO와 파일을 전달
        BoardResponseDto responseDto = boardService.createBoard(authUser, workId, boardRequestDto, backgroundImageFile);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.of(200, responseDto));
    }

    /**
     * 보드 단건 조회
     * @param authUser 사용자
     * @param workId 워크스페이스 아이디
     * @param boardId 보드 아이디
     * @return ResponseEntity<ResponseDto<OneBoardResponseDto>>
     */
    @GetMapping("/{boardId}")
    public ResponseEntity<ResponseDto<OneBoardResponseDto>> getOneBoard(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long workId,
            @PathVariable Long boardId
    ) {
        OneBoardResponseDto responseDto = boardService.getOneBoard(authUser, workId, boardId);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.of(200, responseDto));
    }

    /**
     * 보드 다건 조회
     * @param page 페이지 수
     * @param size 사이즈
     * @param authUser 사용자
     * @param workId 워크스페이스 아이디
     * @return ResponseEntity<ResponseDto<Page<BoardResponseDto>>>
     */
    @GetMapping
    public ResponseEntity<ResponseDto<Page<BoardResponseDto>>> getBoards(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long workId
    ) {
        Page<BoardResponseDto> responseDto = boardService.getBoards(page, size, authUser, workId);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.of(200, responseDto));
    }

    /**
     * 보드 수정 (이미지 파일 업로드 포함)
     * @param authUser 사용자
     * @param workId 워크스페이스
     * @param boardId 보드 아이디
     * @param boardRequestDto 보드 requestDto
     * @param backgroundImageFile 배경 이미지 파일 (Optional)
     * @return ResponseEntity<ResponseDto<BoardResponseDto>>
     */
    @PatchMapping("/{boardId}")
    public ResponseEntity<ResponseDto<BoardResponseDto>> updateBoard(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long workId,
            @PathVariable Long boardId,
            @Valid @RequestPart BoardRequestDto boardRequestDto,
            @RequestPart(required = false) MultipartFile backgroundImageFile  // Optional image file
    ) throws IOException {
        BoardResponseDto responseDto = boardService.updateBoard(authUser, workId, boardId, boardRequestDto, backgroundImageFile);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.of(200, responseDto));
    }

    /**
     * 보드 삭제
     * @param authUser 사용자
     * @param workId 워크스페이스 아이디
     * @param boardId 보드 아이디
     * @return ResponseEntity<ResponseDto<String>>
     */
    @DeleteMapping("/{boardId}")
    public ResponseEntity<ResponseDto<String>> deleteBoard(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long workId,
            @PathVariable Long boardId
    ) {
        boardService.deleteBoard(authUser, workId, boardId);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.of(200, "보드가 성공적으로 삭제되었습니다."));
    }
}

