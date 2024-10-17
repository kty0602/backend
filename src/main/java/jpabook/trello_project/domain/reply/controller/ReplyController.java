package jpabook.trello_project.domain.reply.controller;

import jpabook.trello_project.domain.common.dto.AuthUser;
import jpabook.trello_project.domain.common.dto.ResponseDto;
import jpabook.trello_project.domain.reply.dto.request.CreateReplyRequestDto;
import jpabook.trello_project.domain.reply.dto.request.ModifyReplyRequestDto;
import jpabook.trello_project.domain.reply.dto.response.ReplyResponseDto;
import jpabook.trello_project.domain.reply.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workspaces/{work_Id}/boards/{board_id}/lists/{list_id}/cards/{card_id}")
public class ReplyController {
    private final ReplyService replyService;

    /**
     * @param workId
     * @param boardId
     * @param listId
     * @param cardId
     * @param requestDto
     * @param authUser
     * @return
     */
    @PostMapping("/replies")
    public ResponseEntity<ResponseDto<ReplyResponseDto>> createReply(
            @PathVariable("work_Id") Long workId,
            @PathVariable("board_id") Long boardId,
            @PathVariable("list_id") Long listId,
            @PathVariable("card_id") Long cardId,
            @RequestBody CreateReplyRequestDto requestDto,
            @AuthenticationPrincipal AuthUser authUser) {
        ReplyResponseDto responseDto = replyService.createReply(requestDto, cardId, workId, authUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.of(HttpStatus.CREATED, responseDto));
    }

    /**
     * @param workId
     * @param boardId
     * @param listId
     * @param cardId
     * @param replyId
     * @param requestDto
     * @param authUser
     * @return
     */
    @PatchMapping("/replies/{reply_id}")
    public ResponseEntity<ResponseDto<ReplyResponseDto>> modifyReply(
            @PathVariable("work_Id") Long workId,
            @PathVariable("board_id") Long boardId,
            @PathVariable("list_id") Long listId,
            @PathVariable("card_id") Long cardId,
            @PathVariable("reply_id") Long replyId,
            @RequestBody ModifyReplyRequestDto requestDto,
            @AuthenticationPrincipal AuthUser authUser) {
        ReplyResponseDto responseDto = replyService.modifyReply(replyId, requestDto, workId, authUser);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.of(200, responseDto));
    }

    /**
     * @param workId
     * @param boardId
     * @param listId
     * @param cardId
     * @param replyId
     * @param authUser
     * @return
     */
    @DeleteMapping("/replies/{reply_id}")
    public ResponseEntity<ResponseDto<String>> deleteReply(
            @PathVariable("work_Id") Long workId,
            @PathVariable("board_id") Long boardId,
            @PathVariable("list_id") Long listId,
            @PathVariable("card_id") Long cardId,
            @PathVariable("reply_id") Long replyId,
            @AuthenticationPrincipal AuthUser authUser) {
        replyService.deleteReply(replyId, workId, authUser);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.of(200, "성공적으로 삭제되었습니다."));
    }
}
