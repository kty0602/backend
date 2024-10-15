package jpabook.trello_project.domain.attachment.controller;

import jpabook.trello_project.domain.attachment.dto.response.AttachmentResponseDto;
import jpabook.trello_project.domain.attachment.service.AttachmentService;
import jpabook.trello_project.domain.common.dto.AuthUser;
import jpabook.trello_project.domain.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workspaces/{work_Id}/boards/{board_id}/lists/{list_id}/cards/{card_id}/attachments")
public class AttachmentController {
    private final AttachmentService attachmentService;

    /**
     * @param workId
     * @param boardId
     * @param listId
     * @param cardId
     * @param files
     * @param authUser
     * @return
     * @throws IOException
     */
    @PostMapping()
    public ResponseEntity<ResponseDto<List<AttachmentResponseDto>>> uploadAttachments (
            @PathVariable("work_Id") Long workId,
            @PathVariable("board_id") Long boardId,
            @PathVariable("list_id") Long listId,
            @PathVariable("card_id") Long cardId,
            @RequestParam("files") List<MultipartFile> files,
            @AuthenticationPrincipal AuthUser authUser) throws IOException {
        List<AttachmentResponseDto> responseDtos = attachmentService.uploadAttachments(files, workId, cardId, authUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.of(HttpStatus.CREATED, "파일 업로드가 완료되었습니다.", responseDtos));
    }

    /**
     * @param workId
     * @param boardId
     * @param listId
     * @param cardId
     * @param attachmentId
     * @param authUser
     * @return
     */
    @DeleteMapping("/{attachment_id}")
    public ResponseEntity<ResponseDto<String>> deleteAttachment(
            @PathVariable("work_Id") Long workId,
            @PathVariable("board_id") Long boardId,
            @PathVariable("list_id") Long listId,
            @PathVariable("card_id") Long cardId,
            @PathVariable("attachment_id") Long attachmentId,
            @AuthenticationPrincipal AuthUser authUser) {
        attachmentService.deleteAttachment(attachmentId, workId, authUser);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.of(200, "성공적으로 삭제되었습니다."));
    }
}
