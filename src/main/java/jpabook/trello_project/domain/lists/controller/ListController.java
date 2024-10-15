package jpabook.trello_project.domain.lists.controller;

import jpabook.trello_project.domain.common.dto.AuthUser;
import jpabook.trello_project.domain.common.dto.ResponseDto;
import jpabook.trello_project.domain.lists.dto.*;
import jpabook.trello_project.domain.lists.service.ListService;
import jpabook.trello_project.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workspaces/{workId}/boards/{boardId}/lists")
public class ListController {
    private final ListService listService;

    @PostMapping
    public ResponseEntity<ResponseDto<ListSaveResponseDto>> saveList(@PathVariable("workId") Long workId,
                                                                     @PathVariable("boardId") Long boardId,
                                                                     @RequestBody ListSaveRequestDto saveRequestDto) {
        AuthUser user = new AuthUser(1L, "email@email.com", UserRole.ROLE_USER);    // !!임의값!! (@AuthUser 사용)

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.of(201, listService.saveList(user, workId, boardId, saveRequestDto)));
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
    public ResponseEntity<ResponseDto<ListResponseDto>> changeList(@PathVariable("workId") Long workId,
                                                                   @PathVariable("boardId") Long boardId,
                                                                   @PathVariable("listId") Long listId,
                                                                   @RequestBody ListChangeRequestDto changeDto) {

        AuthUser user = new AuthUser(1L, "email@email.com", UserRole.ROLE_USER);    // !!임의값!! (@AuthUser 사용)

        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.of(200, listService.changeList(user, workId, boardId, listId, changeDto)));
    }

    @PatchMapping("/{listId}")
    public ResponseEntity<ResponseDto<String>> changeListOrder(@PathVariable("workId") Long workId,
                                                                   @PathVariable("boardId") Long boardId,
                                                                   @PathVariable("listId") Long listId,
                                                                   @RequestBody ListOrderChangeRequestDto orderDto) {

        AuthUser user = new AuthUser(1L, "email@email.com", UserRole.ROLE_USER);    // !!임의값!! (@AuthUser 사용)

        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.of(200, listService.changeListOrder(user, workId, boardId, listId, orderDto)));
    }

    @DeleteMapping("/{listId}")
    public ResponseEntity<ResponseDto<String>> changeListOrder(@PathVariable("workId") Long workId,
                                                               @PathVariable("boardId") Long boardId,
                                                               @PathVariable("listId") Long listId) {
        AuthUser user = new AuthUser(1L, "email@email.com", UserRole.ROLE_USER);    // !!임의값!! (@AuthUser 사용)

        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.of(200, listService.deleteList(user, workId, boardId, listId)));
    }
}
