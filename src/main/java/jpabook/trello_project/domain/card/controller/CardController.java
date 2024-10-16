package jpabook.trello_project.domain.card.controller;

import jpabook.trello_project.domain.card.dto.request.CreateCardRequestDto;
import jpabook.trello_project.domain.card.dto.request.ModifyCardRequestDto;
import jpabook.trello_project.domain.card.dto.response.CardRankResponseDto;
import jpabook.trello_project.domain.card.dto.response.CardResponseDto;
import jpabook.trello_project.domain.card.dto.response.GetCardResponseDto;
import jpabook.trello_project.domain.card.service.CardService;
import jpabook.trello_project.domain.common.dto.AuthUser;
import jpabook.trello_project.domain.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workspaces/{work_Id}/boards/{board_id}/lists/{list_id}")
public class CardController {
    private final CardService cardService;

    /**
     * @param workId
     * @param boardId
     * @param listId
     * @param requestDto
     * @param authUser
     * @return ResponseDto
     */
    @PostMapping("/cards")
    public ResponseEntity<ResponseDto<CardResponseDto>> createCard(
            @PathVariable("work_Id") Long workId,
            @PathVariable("board_id") Long boardId,
            @PathVariable("list_id") Long listId,
            @RequestBody CreateCardRequestDto requestDto,
            @AuthenticationPrincipal AuthUser authUser) {
        CardResponseDto responseDto = cardService.createCard(requestDto, workId, listId, authUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.of(HttpStatus.CREATED, responseDto));
    }

    /**
     * @param boardId
     * @param listId
     * @param cardId
     * @param authUser
     * @return ResponseDto
     */
    @GetMapping("/cards/{card_id}")
    public ResponseEntity<ResponseDto<GetCardResponseDto>> getCard(
            @PathVariable("work_Id") Long workId,
            @PathVariable("board_id") Long boardId,
            @PathVariable("list_id") Long listId,
            @PathVariable("card_id") Long cardId,
            @AuthenticationPrincipal AuthUser authUser) {
        GetCardResponseDto responseDto = cardService.getCard(cardId, workId, authUser);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.of(200, responseDto));
    }

    @GetMapping("/cards/top5")
    public ResponseEntity<ResponseDto<List<CardRankResponseDto>>> getTop5(
            @PathVariable("work_Id") Long workId,
            @PathVariable("board_id") Long boardId,
            @PathVariable("list_id") Long listId,
            @AuthenticationPrincipal AuthUser authUser) {
        List<CardRankResponseDto> responseDto = cardService.getTop5Cards(workId, boardId, listId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.of(200, responseDto));
    }


    /**
     * @param boardId
     * @param listId
     * @param cardId
     * @param requestDto
     * @param authUser
     * @return ResponseDto
     */
    @PatchMapping("/cards/{card_id}")
    public ResponseEntity<ResponseDto<CardResponseDto>> modifyCard(
            @PathVariable("work_Id") Long workId,
            @PathVariable("board_id") Long boardId,
            @PathVariable("list_id") Long listId,
            @PathVariable("card_id") Long cardId,
            @RequestBody ModifyCardRequestDto requestDto,
            @AuthenticationPrincipal AuthUser authUser) {
        CardResponseDto responseDto = cardService.modifyCard(cardId, requestDto, workId, authUser);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.of(200, responseDto));
    }

    /**
     * @param boardId
     * @param listId
     * @param cardId
     * @param authUser
     * @return ResponseDto
     */
    @DeleteMapping("/cards/{card_id}")
    public ResponseEntity<ResponseDto<String>> deleteCard(
            @PathVariable("work_Id") Long workId,
            @PathVariable("board_id") Long boardId,
            @PathVariable("list_id") Long listId,
            @PathVariable("card_id") Long cardId,
            @AuthenticationPrincipal AuthUser authUser) {
        cardService.deleteCard(cardId, workId, authUser);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.of(200, "성공적으로 삭제되었습니다."));
    }
}
