package jpabook.trello_project.domain.manager.controller;

import jpabook.trello_project.domain.common.dto.AuthUser;
import jpabook.trello_project.domain.common.dto.ResponseDto;
import jpabook.trello_project.domain.manager.dto.request.ManagerRequestDto;
import jpabook.trello_project.domain.manager.dto.response.ManagerResponseDto;
import jpabook.trello_project.domain.manager.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards/{board_id}/lists/{list_id}/cards/{card_id}")
public class ManagerController {
    private final ManagerService managerService;

    /**
     * @param boardId
     * @param listId
     * @param cardId
     * @param requestDto
     * @param authUser
     * @return ResponseDto
     */
    @PostMapping
    public ResponseEntity<ResponseDto<ManagerResponseDto>> createManager(
            @PathVariable("board_id") Long boardId,
            @PathVariable("list_id") Long listId,
            @PathVariable("card_id") Long cardId,
            @RequestBody ManagerRequestDto requestDto,
            @AuthenticationPrincipal AuthUser authUser) {
        ManagerResponseDto responseDto = managerService.createManager(requestDto, cardId, authUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.of(HttpStatus.CREATED, responseDto));
    }
}
