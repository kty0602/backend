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

    @PostMapping
    public ResponseEntity<ResponseDto<ListSaveResponseDto>> saveList(@AuthenticationPrincipal AuthUser authUser,
                                                                     @PathVariable("workId") Long workId,
                                                                     @PathVariable("boardId") Long boardId,
                                                                     @RequestBody ListSaveRequestDto saveRequestDto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.of(201, listService.saveList(authUser, workId, boardId, saveRequestDto)));
    }

    @GetMapping("/{listId}")
    public ResponseEntity<ResponseDto<ListResponseDto>> getList(@PathVariable("workId") Long workId,
                                                                @PathVariable("boardId") Long boardId,
                                                                @PathVariable("listId") Long listId) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.of(200, listService.getList(workId, boardId, listId)));
    }

    @GetMapping
    public ResponseEntity<ResponseDto<List<ListResponseDto>>> getLists(@PathVariable("workId") Long workId,
                                                                       @PathVariable("boardId") Long boardId) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.of(200, listService.getLists(workId, boardId)));
    }

    @PutMapping("/{listId}")
    public ResponseEntity<ResponseDto<ListResponseDto>> changeList(@AuthenticationPrincipal AuthUser authUser,
                                                                   @PathVariable("workId") Long workId,
                                                                   @PathVariable("boardId") Long boardId,
                                                                   @PathVariable("listId") Long listId,
                                                                   @RequestBody ListChangeRequestDto changeDto) {

        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.of(200, listService.changeList(authUser, workId, boardId, listId, changeDto)));
    }

    @PatchMapping("/{listId}")
    public ResponseEntity<ResponseDto<String>> changeListOrder(@AuthenticationPrincipal AuthUser authUser,
                                                               @PathVariable("workId") Long workId,
                                                               @PathVariable("boardId") Long boardId,
                                                               @PathVariable("listId") Long listId,
                                                               @RequestBody ListOrderChangeRequestDto orderDto) {

        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.of(200, listService.changeListOrder(authUser, workId, boardId, listId, orderDto)));
    }

    @DeleteMapping("/{listId}")
    public ResponseEntity<ResponseDto<String>> changeListOrder(@AuthenticationPrincipal AuthUser authUser,
                                                               @PathVariable("workId") Long workId,
                                                               @PathVariable("boardId") Long boardId,
                                                               @PathVariable("listId") Long listId) {

        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.of(200, listService.deleteList(authUser, workId, boardId, listId)));
    }
}
