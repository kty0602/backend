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
@RequestMapping("/card/{card_id}")
public class ReplyController {
    private final ReplyService replyService;

    /**
     * @param cardId
     * @param requestDto
     * @param authUser
     * @return ResponseDto
     */
    @PostMapping("/replies")
    public ResponseEntity<ResponseDto<ReplyResponseDto>> createReply(
            @PathVariable("card_id") Long cardId,
            @RequestBody CreateReplyRequestDto requestDto,
            @AuthenticationPrincipal AuthUser authUser) {
        ReplyResponseDto responseDto = replyService.createReply(requestDto, cardId, authUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.of(HttpStatus.CREATED, responseDto));
    }

    /**
     * @param cardId
     * @param replyId
     * @param requestDto
     * @param authUser
     * @return ResponseDto
     */
    @PatchMapping("/replies/{reply_id}")
    public ResponseEntity<ResponseDto<ReplyResponseDto>> modifyReply(
            @PathVariable("card_id") Long cardId,
            @PathVariable("reply_id") Long replyId,
            @RequestBody ModifyReplyRequestDto requestDto,
            @AuthenticationPrincipal AuthUser authUser) {
        ReplyResponseDto responseDto = replyService.modifyReply(replyId, requestDto, authUser);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.of(200, responseDto));
    }

    /**
     * @param cardId
     * @param replyId
     * @param authUser
     * @return ResponseDto
     */
    @DeleteMapping("/replies/{reply_id}")
    public ResponseEntity<ResponseDto<String>> deleteReply(
            @PathVariable("card_id") Long cardId,
            @PathVariable("reply_id") Long replyId,
            @AuthenticationPrincipal AuthUser authUser) {
        replyService.deleteReply(replyId, authUser);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.of(200, "성공적으로 삭제되었습니다."));
    }
}
