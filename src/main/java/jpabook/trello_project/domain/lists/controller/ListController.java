package jpabook.trello_project.domain.lists.controller;

import jpabook.trello_project.domain.common.dto.AuthUser;
import jpabook.trello_project.domain.common.dto.ResponseDto;
import jpabook.trello_project.domain.lists.dto.*;
import jpabook.trello_project.domain.lists.service.ListService;
import jpabook.trello_project.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workspaces/{workId}/boards/{boardId}/lists")
public class ListController {
    private final ListService listService;

    /**
     * 리스트 생성 및 저장
     * @param authUser 로그인한 유저
     * @param workId 워크스페이스 아이디
     * @param boardId 보드 아이디
     * @param saveRequestDto 리스트 저장 데이터 용 dto
     * @return 저장된 list
     */
    @PostMapping
    public ResponseEntity<ResponseDto<ListSaveResponseDto>> saveList(@AuthenticationPrincipal AuthUser authUser,
                                                                     @PathVariable("workId") Long workId,
                                                                     @PathVariable("boardId") Long boardId,
                                                                     @RequestBody ListSaveRequestDto saveRequestDto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.of(201, listService.saveList(authUser, workId, boardId, saveRequestDto)));
    }

    /**
     * 리스트 단건 조회
     * @param workId 워크스페이스 아이디
     * @param boardId 보드 아이디
     * @param listId 조회하려는 리스트 아이디
     * @return listId를 갖고 있는 list response dto
     */
    @GetMapping("/{listId}")
    public ResponseEntity<ResponseDto<ListResponseDto>> getList(@PathVariable("workId") Long workId,
                                                                @PathVariable("boardId") Long boardId,
                                                                @PathVariable("listId") Long listId) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.of(200, listService.getList(workId, boardId, listId)));
    }

    /**
     * 전체 리스트 조회
     * @param workId 워크스페이스 아이디
     * @param boardId 보드 아이디
     * @return 전체 list response dto
     */
    @GetMapping
    public ResponseEntity<ResponseDto<List<ListResponseDto>>> getLists(@PathVariable("workId") Long workId,
                                                                       @PathVariable("boardId") Long boardId) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.of(200, listService.getLists(workId, boardId)));
    }

    /**
     * 리스트 수정
     * @param authUser 로그인한 유저
     * @param workId 워크스페이스 아이디
     * @param boardId 보드 아이디
     * @param listId 수정하려는 리스트 아이디
     * @param changeDto 수정 내용 dto
     * @return 수정된 list dto
     */
    @PutMapping("/{listId}")
    public ResponseEntity<ResponseDto<ListResponseDto>> changeList(@AuthenticationPrincipal AuthUser authUser,
                                                                   @PathVariable("workId") Long workId,
                                                                   @PathVariable("boardId") Long boardId,
                                                                   @PathVariable("listId") Long listId,
                                                                   @RequestBody ListChangeRequestDto changeDto) {

        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.of(200, listService.changeList(authUser, workId, boardId, listId, changeDto)));
    }

    /**
     * 리스트 순서 변경
     * @param authUser 로그인한 유저
     * @param workId 워크스페이스 아이디
     * @param boardId 보드 아이디
     * @param listId 순서 변경하려는 리스트 아이디
     * @param orderDto 수정하려는 순서 dto
     * @return 수정 완료 메시지
     */
    @PatchMapping("/{listId}")
    public ResponseEntity<ResponseDto<String>> changeListOrder(@AuthenticationPrincipal AuthUser authUser,
                                                               @PathVariable("workId") Long workId,
                                                               @PathVariable("boardId") Long boardId,
                                                               @PathVariable("listId") Long listId,
                                                               @RequestBody ListOrderChangeRequestDto orderDto) {

        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.of(200, listService.changeListOrder(authUser, workId, boardId, listId, orderDto)));
    }

    /**
     * 리스트 삭제
     * @param authUser 로그인한 유저
     * @param workId 워크스페이스 아이디
     * @param boardId 보드 아이디
     * @param listId 삭제하려는 리스트 아이디
     * @return 삭제 완료 메시지
     */
    @DeleteMapping("/{listId}")
    public ResponseEntity<ResponseDto<String>> changeListOrder(@AuthenticationPrincipal AuthUser authUser,
                                                               @PathVariable("workId") Long workId,
                                                               @PathVariable("boardId") Long boardId,
                                                               @PathVariable("listId") Long listId) {

        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.of(200, listService.deleteList(authUser, workId, boardId, listId)));
    }
}
