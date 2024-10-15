package jpabook.trello_project.domain.attachment.dto.response;

import jpabook.trello_project.domain.attachment.entity.Attachment;
import lombok.Getter;

@Getter
public class AttachmentResponseDto {
    private Long id;
    private String url;

    public AttachmentResponseDto(Attachment attachment) {
        this.id = attachment.getId();
        this.url = attachment.getUrl();
    }
}
